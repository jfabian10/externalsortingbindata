import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Client in this project performs a QuickSort on the records stored in the file
 * by retrieving records from the BufferPool at all times. It does so by
 * indicating byte position where the desired record starts.
 * 
 * @author l3ogio22
 * @version Oct 21, 2016
 */
public class Client {
    private BufferPool bfpool;

    /**
     * recordsize
     */
    private static final int RECORDSIZE = 4;
    /**
     * size of Buffer
     */
    static final int BUFFERSIZE = 4096;
    /**
     * left record
     */
    private byte[] leftRec = new byte[RECORDSIZE];
    /**
     * right record
     */
    private byte[] rightRec = new byte[RECORDSIZE];

    private byte[] leftRecSwap = new byte[RECORDSIZE];
    private byte[] rightRecSwap = new byte[RECORDSIZE];
    private byte[] pivRecord = new byte[RECORDSIZE];

    private int levels;

    /**
     * Creates client
     * 
     * @param pool
     *            that it will use
     * @param binary
     *            Whether or not the file we are sorting is binary
     */
    public Client(BufferPool pool, boolean binary) {
        bfpool = pool;
        if (binary) {
            levels = 29;
        }
        else {
            levels = 18;
        }
    }

    /**
     * To be written recursively Pick a pivot, then swap it for the last element
     * in the array. Let i be the leftmost element in array and j be one before
     * pivot Move i LEFT until it crosses a value BIGGER or equal to pivot and
     * move j RIGHT until it crosses a value less than the pivot OR j crosses i.
     * Swap arr[i] and arr[j]. Everything to the left of the i will be less than
     * the pivot and all elements to the right of i are bigger. Swap where i and
     * j crossed with pivot. Proceed until array to sort is one which implies
     * it's already sorted.
     * 
     * @param i
     *            leftRecord
     * @param j
     *            rightRecord
     * @param level
     *            how far we've recursed
     * @throws IOException
     * 
     * 
     * 
     */
    public void quickSort(int i, int j, int level) throws IOException {
        // i is the left bound, j is the right bound

        // System.out.println("sorting " + i + "th and " + j + "th record ");
        int pivRecPos = findPivot(i, j); // Pick a pivot
        // System.out.println("pivot: " + pivRecPos);
        swap(pivRecPos, j);

        int k = partition(i, j - 1, j);

        swap(k, j); // put pivot in place

        if ((k - i) > 1) {
            if (level > levels) { // too deep, use different sort
                insertionSort(i, k - 1);
            }
            else {
                quickSort(i, k - 1, level + 1); // sort left partition
            }
        }
        if ((j - k) > 1) {
            if (level > levels) { // too deep, use different sort
                insertionSort(k + 1, j);
            }
            else {
                quickSort(k + 1, j, level + 1); // sort right partition
            }
        }

    }

    /**
     * 
     * 
     * @param record
     *            to extract first two bytes from
     * @return key
     */
    public short getKey(byte[] record) {
        ByteBuffer bb = ByteBuffer.wrap(record);
        short key = bb.getShort();
        return key;
    }

    /**
     * @param i
     *            leftmost position
     * @param j
     *            rightmost position
     * @return pivot
     */

    public int findPivot(int i, int j) {
        return (i + j) / 2;
    }

    /**
     * 
     * @param left
     *            gets moved to right pos
     * @param right
     *            gets moved to left pos
     * @throws IOException
     */
    public void swap(int left, int right) throws IOException {

        // Would it be better to have these be dedicated and not need to be
        // allocated each time?

        loadRecord(left, leftRecSwap);
        loadRecord(right, rightRecSwap);
        insertRecord(right, leftRecSwap);
        insertRecord(left, rightRecSwap);
    }

    /**
     * Partition
     * 
     * @param left
     * @param right
     * @param j
     *            pivot pos
     * @throws IOException
     *
     *             int partition(Comparable[] A, int left, int right, Comparable
     *             pivot) { while (left <= right) { // Move bounds inward until
     *             they meet while (A[left].compareTo(pivot) < 0) left++; while
     *             ((right >= left) && (A[right].compareTo(pivot) >= 0))
     *             right--; if (right > left) swap(A, left, right); // Swap
     *             out-of-place values } return left; // Return first position
     *             in right partition }
     * 
     * 
     *             function partitionFunc(left, right, pivot) leftPointer = left
     *             -1 rightPointer = right
     * 
     *             while True do while A[++leftPointer] < pivot do //do-nothing
     *             end while
     * 
     *             while rightPointer > 0 && A[--rightPointer] > pivot do
     *             //do-nothing end while
     * 
     *             if leftPointer >= rightPointer break else swap
     *             leftPointer,rightPointer end if
     * 
     *             end while
     * 
     *             swap leftPointer,right return leftPointer
     * 
     *             end function
     * 
     * 
     * 
     */
    /*
     * public int partition(int left, int right, int pivotPosition) throws
     * IOException { int leftPointer = left; int rightPointer = right;
     * 
     * byte[] leftRecord = new byte[RECORDSIZE]; byte[] rightRecord = new
     * byte[RECORDSIZE]; byte[] pivRecord = new byte[RECORDSIZE];
     * 
     * loadRecord(pivotPosition, pivRecord); pivKey = getKey(pivRecord);
     * 
     * loadRecord(rightPointer, rightRecord); rightKey = getKey(rightRecord);
     * 
     * loadRecord(leftPointer, leftRecord); leftKey = getKey(leftRecord);
     * 
     * while(true){
     * 
     * while(!(leftKey >= pivKey)){ //If the left key is greater than or equal
     * to the pivot, its good leftPointer++; loadRecord(leftPointer,
     * leftRecord); leftKey = getKey(leftRecord); } while(rightPointer > 0 &&
     * !(rightKey <= pivKey)){ rightPointer--; loadRecord(rightPointer,
     * rightRecord); rightKey = getKey(rightRecord); }
     * 
     * if(leftPointer >= rightPointer){ break; } else{ swap(leftPointer,
     * rightPointer); //Prime leftPointer++; loadRecord(leftPointer,
     * leftRecord); leftKey = getKey(leftRecord); loadRecord(rightPointer,
     * rightRecord); rightKey = getKey(rightRecord); }
     * 
     * }
     * 
     * swap(leftPointer,right); return leftPointer;
     * 
     * }
     * 
     * 
     * 
     * public static void insertionSort(int array[]) { int length =
     * array.length; for (int j = 1; j < length; j++) { int key = array[j]; int
     * i = j-1; while ( (i > -1) && ( array [i] > key ) ) { array [i+1] = array
     * [i]; i--; } array[i+1] = key; printNumbers(array); }
     * 
     * 
     * 
     */

    // Makes everything between left and right completely sorted
    /**
     * Insertion sort algorithm, excels when array is mostly sorted already
     * 
     * @param left
     *            left position to start at
     * @param right
     *            right position to start at
     * @throws IOException
     */
    public void insertionSort(int left, int right) throws IOException {

        short leftKey;
        short key;

        for (int j = left + 1; j < right + 1; j++) {

            loadRecord(j, pivRecord);
            key = getKey(pivRecord);
            int i = j - 1;

            loadRecord(i, leftRec);
            leftKey = getKey(leftRec);

            while ((i > -1) && leftKey > key) {
                swap(i + 1, i);
                i--;
                loadRecord(i, leftRec);
                leftKey = getKey(leftRec);
            }

            insertRecord(i + 1, pivRecord);

        }

    }

    /**
     * @param left
     *            leftrecord
     * @param right
     *            record
     * @param j
     *            pivot records
     * @return the left records last position
     * 
     */
    public int partition(int left, int right, int j) throws IOException {

        short leftKey;
        short rightKey;
        short pivKey;

        loadRecord(j, pivRecord);
        pivKey = getKey(pivRecord);

        // System.out.println("PivRecValue: " + pivKey);
        bigloop: while (left <= right) {
            // System.out.println("leftRec1: " + left + "value is: " +
            // String.valueOf(leftKey));
            loadRecord(left, leftRec);
            leftKey = getKey(leftRec);
            while (leftKey < pivKey) {
                left++;
                // System.out.println("leftRec2: " + left + "value is: " +
                // String.valueOf(leftKey));
                loadRecord(left, leftRec);
                leftKey = getKey(leftRec);

            }

            // System.out.println("rightRec1: " + right + "value is: " +
            // String.valueOf(rightKey));
            loadRecord(right, rightRec);
            rightKey = getKey(rightRec);
            while (right >= left && rightKey >= pivKey) {
                right--;
                if (right == -1) {
                    break bigloop;
                }
                // System.out.println("rightRec2: " + right + "value is: " +
                // String.valueOf(rightKey));
                loadRecord(right, rightRec);
                rightKey = getKey(rightRec);
            }
            if (right > left) {
                swap(left, right);
            }
        }
        return left;
    }

    /**
     * Calls on buffer pool's loadRecord().
     * 
     * @param pos
     *            position
     * @param record
     *            to load
     * @throws IOException
     */
    public void loadRecord(int pos, byte[] record) throws IOException {

        int recordPos = pos * RECORDSIZE;
        bfpool.loadRecord(recordPos, record);
    }

    /**
     * @param pos
     *            where to insert
     * @param record
     *            to insert
     * @throws IOException
     */
    public void insertRecord(int pos, byte[] record) throws IOException {
        int recordPos = pos * RECORDSIZE;
        bfpool.insertRecord(recordPos, record);
    }

    /**
     * Cleans up
     */
    public void cleanUp() throws IOException {
        // bfpool.writeAllBuffersToFile();
        bfpool.cleanUp();
    }

}

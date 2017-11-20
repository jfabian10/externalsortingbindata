import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Pool will serve as our virtual memory
 * 
 * @author l3oGi_000
 * @version Oct 21, 2016
 */
public class BufferPool {

    /**
     * Buffer's max cap
     */
    static final int BUFFERSIZE = 4096;
    /**
     * size of record
     */
    static final int RECORDSIZE = 4;
    /**
     * array of Buffers
     */
    private Buffer[] bp;
    /**
     * max cap
     */
    private int numOfBuffers;
    /**
     * number of loaded Buffers
     */
    private int loadedBuffers = 0;
    /**
     * raf
     */
    private RandomAccessFile raf;
    /**
     * length of file
     */
    private long fileLength;
    /**
     * a record is 4 bytes
     */
    static final byte[] RECORD = new byte[RECORDSIZE];
    /**
     * useless record
     */
    static final byte[] GARBAGERECORD = new byte[RECORDSIZE];

    private byte[] byteArr = new byte[BUFFERSIZE];
    // private Buffer buffer;
    private byte[] outputByteArr = new byte[BUFFERSIZE];
    // private int tempid;

    private int cacheHits = 0;
    private int diskReads = 0;
    private int diskWrites = 0;

    private int bufferPos;

    /**
     * BufferPool is created.
     * 
     * @param num
     *            of buffers to create
     * @param filename
     *            input file
     * @throws IOException
     */
    public BufferPool(String filename, int num) throws IOException {
        numOfBuffers = num;
        bp = new Buffer[numOfBuffers];
        for (int i = 0; i < numOfBuffers; i++) {
            bp[i] = new Buffer();
        }

        raf = new RandomAccessFile(filename, "rw");
        fileLength = raf.length();

    }

    /**
     * When attempting to retrieve a record it will first look if one of its
     * buffers contains it, else it will have to read from the file itself but
     * doing so will load a buffer so that it doesn't read from file again if a
     * record from this buffer is to be retrieved.
     * 
     * @param pos
     *            byte offset
     * @param rec
     *            to load
     * 
     * @throws IOException
     */

    public void loadRecord(int pos, byte[] rec) throws IOException {

        int blockid = (int) Math.floor(pos / BUFFERSIZE);

        // int blockid = pos / BUFFERSIZE;

        // System.out.println("blockid: " + blockid + " , bytepos: " + pos);
        if (bufferLoaded(blockid)) {
            bufferPos = bufferPos(blockid);

            bp[bufferPos].loadRecord(pos, rec);

            cacheHits++;
            if (bufferPos != 0) {
                moveBufferToFront(bufferPos);
            }

        }

        else {
            loadABuffer(blockid);

            // pool has no loaded Buffers
            if (loadedBuffers == 0) {
                bp[0].loadBuffer(blockid, byteArr);
                loadedBuffers++;
            }

            // Pool has space
            else if (loadedBuffers != numOfBuffers) {
                pushBuffers();
                bp[0].reset();
                // bufferpool[0] = new Buffer(blockid); //BAD - but guaranteed
                // to not cause collision and multi-copies
                bp[0].setBufferID(blockid);
                bp[0].loadBuffer(blockid, byteArr);

                loadedBuffers++;
            }

            /*
             * Pool needs to load a record but buffers are full THE LRU Buffer
             * is at the very end: bufferpool[numOfBuffers - 1]
             * 
             */
            else {
                /*
                 * First, write the last buffer to the file. This function also
                 * checks if its dirty and needs to be written
                 */
                writeBufferToFile(numOfBuffers - 1);
                bp[numOfBuffers - 1].reset(); // Reset the buffer
                moveBufferToFront(numOfBuffers - 1);

                bp[0].setBufferID(blockid);
                bp[0].loadBuffer(blockid, byteArr);
            }

            bp[0].loadRecord(pos, rec);
        }

    }

    /**
     * @param blockid
     *            of Buffer to load
     */

    public void loadABuffer(int blockid) throws IOException {

        int filepos = blockid * BUFFERSIZE;
        // System.out.println("Loading a Buffer in BufferPool starting at
        // filepos: " + filepos);
        raf.seek(filepos);
        raf.read(byteArr);
        diskReads++;
    }

    /**
     * Shifts Buffers to the right one, Last buffer guaranteed to be empty.
     */
    public void pushBuffers() {

        Buffer temp = bp[numOfBuffers - 1];

        for (int i = numOfBuffers - 1; i > 0; i--) {

            // bufferpool[i] = new Buffer(bufferpool[i-1]); //DEEP copy
            bp[i] = bp[i - 1]; // SHALLOW copy

            // Say we have 4 buffers:
            // A B C D
            // After this loop we will have:
            // A A B C

        }
        // And now we will have: //D A B C
        bp[0] = temp;
    }

    /**
     * Brings LRU Buffer to front of list.
     * 
     * @param pos
     *            of Buffer to bring to frontF NOTE: With 5 blocks and 5 blocks
     *            in input file, this takes 20.746s on Johns computer using DEEP
     *            copy (and same input). Using shallow copy the new time is
     *            18.609s
     */
    public void moveBufferToFront(int pos) {

        Buffer temp = bp[pos];
        for (int i = pos; i > 0; i--) {

            // say we have 4 blocks and pos = 2
            // A B C D, where C is marked by pos
            // After this for loop runs we will have:
            // A A B D
            // bufferpool[i] = new Buffer(bufferpool[i-1]); //DEEP copy
            bp[i] = bp[i - 1]; // SHALLOW copy
        }

        // Now we will have:
        // C A B D
        bp[0] = temp;
    }

    /**
     * @param pos
     *            we write record within
     * @param rec
     *            source
     * @throws IOException
     */
    public void insertRecord(int pos, byte[] rec) throws IOException {
        int blockid = (int) Math.floor(pos / BUFFERSIZE);
        // ByteBuffer bb = ByteBuffer.wrap(record);
        // short key = bb.getShort();
        // System.out.println("insert record at bytepos: " + pos + " in Buffer
        // with id: " + blockid + " Record Value: " + String.valueOf(key));

        if (!bufferLoaded(blockid)) {
            // Need to load the buffer in
            // Need to make sure this method doesn't increment cacheHits,
            // incrementing diskReads is ok
            loadRecord(pos, GARBAGERECORD);
        }

        // Now we are guaranteed to have the buffer loaded
        bufferPos = bufferPos(blockid);
        bp[bufferPos].insertRecord(pos, rec);

    }

    /**
     * Goes through all the loaded Buffers in Pool and writes them to file.
     */
    public void cleanUp() throws IOException {
        // System.out.println("cleaning");
        // System.out.println("Writing to file");

        raf.seek(0);

        for (int i = 0; i < numOfBuffers; i++) {
            writeBufferToFile(i);
        }
    }

    /**
     * @param bufferNumber
     *            buffid
     * 
     */
    public void writeBufferToFile(int bufferNumber) throws IOException {

        // Check if its dirty & loaded before writing to disk
        if (bp[bufferNumber].isLoaded() && bp[bufferNumber].isDirty()) {
            outputByteArr = bp[bufferNumber].getByteArr();
            raf.seek(bp[bufferNumber].getBufferID() * BUFFERSIZE);
            raf.write(outputByteArr);
            bp[bufferNumber].clearDirty(); // buffer is no longer dirty
            diskWrites++;
        }
    }

    /**
     * @return fileLength
     */
    public long fileSize() {
        return fileLength;
    }

    /**
     * @return cacheHits
     */
    public int getCacheHits() {
        return cacheHits;
    }

    /**
     * @return diskReads
     */
    public int getDiskReads() {
        return diskReads;
    }

    /**
     * @return diskWrites
     */
    public int getDiskWrites() {
        return diskWrites;
    }

    /**
     * @return true if Buffer with corresponding
     * @param id
     *            is loaded
     */

    public boolean bufferLoaded(int id) {
        for (int i = 0; i < loadedBuffers; i++) {
            if (bp[i].getBufferID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return index in bp[]
     * @param id
     *            of Buffer
     */

    public int bufferPos(int id) {
        for (int i = 0; i < numOfBuffers; i++) {
            if (bp[i].getBufferID() == id) {
                return i;
            }
        }
        return -10;
    }

}

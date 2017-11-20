import java.util.Arrays;

/**
 * A buffer is used to hold certain number of bytes. When created they don't
 * hold any data but they will hold some data
 * 
 * @author l3ogio22
 * @version Oct 21, 2016
 */

public class Buffer {
    /**
     * A Buffer will at hold 4096 bytes.
     */
    static final int BUFFERSIZE = 4096;

    /**
     * size of record
     */
    static final int RECORDSIZE = 4;
    /**
     * Array used to hold bytes.
     */
    private byte[] byteArr = new byte[BUFFERSIZE];
    // private byte[] record = new byte[RECORDSIZE];
    /**
     * to indicate if Buffer was overwritten
     */
    private boolean dirty = false;
    /*
     * Buffers are not loaded when created.
     */
    private boolean loaded = false;
    /**
     * Implied buffer is not loaded or unloaded.
     */
    private int bufferID = -1;
    /**
     * To "empty" out array.
     */
    private byte trash = 0;

    /**
     * Creates a buffer
     */
    public Buffer() {
        // Default constructor
    }

    /**
     * @param pos
     *            of record
     * @param record
     *            to load
     * 
     */
    public void loadRecord(int pos, byte[] record) {
        System.arraycopy(byteArr, pos % BUFFERSIZE, record, 0, RECORDSIZE);
    }

    /**
     * @param pos
     *            where to insert
     * 
     * @param record
     *            source
     */
    public void insertRecord(int pos, byte[] record) {
        System.arraycopy(record, 0, byteArr, pos % BUFFERSIZE, RECORDSIZE);
        dirty = true;
    }

    /**
     * @return loaded condition
     */

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Loads buffer with 4096 bytes and set its ID.
     * 
     * @param id
     *            of buffer
     * @param buffer
     *            to load
     */
    public void loadBuffer(int id, byte[] buffer) {

        System.arraycopy(buffer, 0, byteArr, 0, BUFFERSIZE);
        bufferID = id;
        loaded = true;
    }

    /**
     * @return Buffers byte array
     */

    public byte[] getByteArr() {
        return byteArr;
    }

    /**
     * @param arr
     *            that will belong to Buffer
     */

    public void setByteArr(byte[] arr) {
        byteArr = arr;
    }

    /**
     * @return bufferID
     */

    public int getBufferID() {
        return bufferID;
    }

    /**
     * @param id
     *            that will belong to Buffer
     */
    public void setBufferID(int id) {
        bufferID = id;
    }

    /**
     * Unloads Buffer
     */

    public void unloadBuffer() {
        Arrays.fill(byteArr, trash);
        bufferID = -1;
        loaded = false;
    }

    /**
     * @return startingPosition
     */

    public int getStartPosition() {
        return bufferID * BUFFERSIZE;
    }

    /**
     * @return true if dirty
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * clears dirty
     */

    public void clearDirty() {
        dirty = false;
    }

    /**
     * Call this to completely reset the buffer to the state it was in when it
     * was first created.
     */
    public void reset() {
        unloadBuffer();
        dirty = false;
        loaded = false;
        bufferID = -1; /// implies Buffer is not loaded
        trash = 0;
    }

}

import student.TestCase;

/**
 * @author john9570
 * @version November 1, 2016
 */
public class BufferTest extends TestCase {
    /**
     * A Buffer will at hold 4096 bytes.
     */
    static final int BUFFERSIZE = 4096;

    /**
     * size of record
     */
    static final int RECORDSIZE = 4;

    /**
     * Tests the buffer class
     */
    public void testBuffer() {
        Buffer myBuf;
        myBuf = new Buffer();
        assertEquals(myBuf.getStartPosition(), -BUFFERSIZE);

        byte[] byteArr = new byte[BUFFERSIZE];
        assertFalse(myBuf.isDirty());
        assertFalse(myBuf.isLoaded());

        myBuf.loadBuffer(2, byteArr);
        assertTrue(myBuf.isLoaded());
        assertFalse(myBuf.isDirty());

        byte[] record = new byte[RECORDSIZE];

        myBuf.insertRecord(2 * BUFFERSIZE + 3, record);
        assertTrue(myBuf.isDirty());

        myBuf.setByteArr(byteArr);

    }

}
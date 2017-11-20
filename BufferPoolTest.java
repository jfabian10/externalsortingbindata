import java.io.IOException;

import student.TestCase;

/**
 * @author john9570
 * @version November 1, 2016
 */
public class BufferPoolTest extends TestCase {
    /**
     * A Buffer will at hold 4096 bytes.
     */
    static final int BUFFERSIZE = 4096;

    /**
     * size of record
     */
    static final int RECORDSIZE = 4;

    private BufferPool myPool;

    /**
     * Simple test of the buffer pool
     * 
     * @throws IOException
     */
    public void testBufferPool() throws IOException {

        byte[] myRecord = new byte[RECORDSIZE];

        Quicksort.generateFile("test.txt", "2", 'b');
        myPool = new BufferPool("test.txt", 1);

        myPool.loadRecord(2 * RECORDSIZE, myRecord);

        assertEquals(myPool.bufferPos(3), -10);
        assertEquals(myPool.bufferPos(0), 0);

    }

    /**
     * Tests the pushBuffer, moveBuffer, and writeBufferToFile functions
     * 
     * @throws IOException
     */
    public void testPushAndMoveBuffer() throws IOException {

        byte[] myRecord = new byte[RECORDSIZE];

        Quicksort.generateFile("test.txt", "2", 'b');
        myPool = new BufferPool("test.txt", 2);

        myPool.writeBufferToFile(0);

        myPool.loadRecord(2 * RECORDSIZE, myRecord);

        assertEquals(myPool.bufferPos(0), 0);
        myPool.pushBuffers();
        assertEquals(myPool.bufferPos(0), 1);

        myPool.moveBufferToFront(1);
        assertEquals(myPool.bufferPos(0), 0);

        myPool.writeBufferToFile(0);
        myPool.insertRecord(2 * RECORDSIZE, myRecord);
        myPool.writeBufferToFile(0);

        myPool.loadRecord(2 * RECORDSIZE + RECORDSIZE, myRecord);

    }

}
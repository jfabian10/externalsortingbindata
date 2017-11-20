import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class QuicksortTest extends TestCase {

    private CheckFile fileChecker;

    /**
     * Sets up the tests that follow. In general, used for initialization.
     */
    public void setUp() {
        fileChecker = new CheckFile();
    }

    /**
     * Pass
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSort() throws Exception {

        Quicksort quicky = new Quicksort();

        // This tests the bad input condition
        String[] arr = new String[2];
        arr[0] = "input.txt";
        arr[1] = "1";
        quicky.main(arr);

        Quicksort.generateFile("Bin.txt", "10", 'b');
        arr = new String[3];
        arr[0] = "Bin.txt";
        arr[1] = "1";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("Bin.txt"));
    }

    /**
     * Test 2 of Project 3
     */
    public void testSort2() throws Exception {
        Quicksort.generateFile("input.txt", "10", 'b');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "1";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

    /**
     * Test 3 of Project 3
     */
    public void testSort3() throws Exception {
        Quicksort.generateFile("input.txt", "10", 'b');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "10";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

    /**
     * Test 4 of Project 3
     */
    public void testSort4() throws Exception {
        Quicksort.generateFile("input.txt", "10", 'b');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "4";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

    /**
     * Test 5 of Project 3
     */
    public void testSort5() throws Exception {
        Quicksort.generateFile("input.txt", "10", 'b');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "1";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

    /**
     * Test 6 of Project 3
     */
    public void testSort6() throws Exception {
        Quicksort.generateFile("input.txt", "100", 'b');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "10";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

    /**
     * Test 8 of Project 3
     */
    public void testSort8() throws Exception {
        Quicksort.generateFile("input.txt", "1000", 'b');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "10";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

    /**
     * Test 11 ish of Project 3
     */
    public void testSort11() throws Exception {
        Quicksort.generateFile("input.txt", "1000", 'a');
        String[] arr = new String[3];
        arr[0] = "input.txt";
        arr[1] = "10";
        arr[2] = "outputFile.txt";
        Quicksort.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
    }

}

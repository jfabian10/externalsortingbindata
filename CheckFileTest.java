import student.TestCase;

/**
 * @author john9570
 * @version November 1, 2016
 */
public class CheckFileTest extends TestCase {

    /**
     * tests the check file
     * 
     * @throws Exception
     */
    public void testCheckFileFalse() throws Exception {

        Quicksort.generateFile("input.txt", "1", 'b');
        CheckFile fileChecker = new CheckFile();
        assertFalse(fileChecker.checkFile("input.txt"));

    }

}
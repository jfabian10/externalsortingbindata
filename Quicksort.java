import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * {Project Description Here}
 */

/**
 * The class containing the main method.
 *
 * @author {Your Name Here}
 * @version {Put Something Here}
 */

public class Quicksort {
    /**
     * @param args
     *            Command line parameters.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // This is the main file for the program.

        if (args.length != 3) {
            System.out.println("Invalid Arguments");
            return;
        }
        else {
            String fileName = args[0];

            boolean binary = false;

            if (fileName.contains("Bin")) {
                binary = true;
            }

            int numOfBuffers = Integer.parseInt(args[1]);
            String outputfile = args[2];
            RandomAccessFile rafOut = new RandomAccessFile(outputfile, "rw");

            // generateFile(fileName, "10", 'a');
            BufferPool bfpool = new BufferPool(fileName, numOfBuffers);
            int records = (int) (bfpool.fileSize() / 4) - 1;
            Client client = new Client(bfpool, binary);

            long start = System.currentTimeMillis();
            client.quickSort(0, records, 0);
            client.cleanUp();
            long end = System.currentTimeMillis();

            long totalRunTime = end - start;

            rafOut.seek(rafOut.length());
            rafOut.writeBytes("Sort on " + fileName + "\n");
            System.out.println("Sort on " + fileName);

            rafOut.writeBytes("Cache Hits: "
                    + Integer.toString(bfpool.getCacheHits()) + "\n");
            System.out.println(
                    "Cache Hits: " + Integer.toString(bfpool.getCacheHits()));

            rafOut.writeBytes("Disk Reads: "
                    + Integer.toString(bfpool.getDiskReads()) + "\n");
            System.out.println("Disk Reads: " + bfpool.getDiskReads());

            rafOut.writeBytes("Disk Writes: "
                    + Integer.toString(bfpool.getDiskWrites()) + "\n");
            System.out.println("Disk Writes: " + bfpool.getDiskWrites());

            rafOut.writeBytes(
                    "Time is: " + Long.toString(totalRunTime) + "\n");
            System.out.println("Time is: " + totalRunTime);

            rafOut.close();
            // client.writeSortedToOtherFile();
        }
    }

    /**
     * This method is used to generate a file of a certain size, containing a
     * specified number of records.
     *
     * @param filename
     *            the name of the file to create/write to
     * @param blockSize
     *            the size of the file to generate
     * @param format
     *            the format of file to create
     * @throws IOException
     *             throw if the file is not open and proper
     */
    public static void generateFile(String filename, String blockSize,
            char format) throws IOException {
        FileGenerator generator = new FileGenerator();
        String[] inputs = new String[3];
        inputs[0] = "-" + format;
        inputs[1] = filename;
        inputs[2] = blockSize;
        generator.generateFile(inputs);
    }
}

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

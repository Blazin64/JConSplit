package com.argon.jconsplit;
import org.riversun.bigdoc.bin.BigFileSearcher;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
/**
 * @author Blazin64
 */
public class Splitter {
    public static void split(String inFileName, String outFileName, String outFileExtension, byte[] searchBytes) throws InterruptedException, Exception
    {
        RunningIndicator scanningIndicator = new RunningIndicator();
        RunningIndicator writingIndicator = new RunningIndicator();
        File file = new File(inFileName);
        //Kudos to our friend riversun for making life easier! :)
        //riversun's BigFileSearcher is used to search for a given pattern in
        //the input file.
        BigFileSearcher searcher = new BigFileSearcher();
        scanningIndicator.setText("Scanning "+inFileName);
        scanningIndicator.start();
        //Start searching for the pattern in our file and store a list of
        //points of all occurences of the pattern.
        List<Long> findList = searcher.searchBigFile(file, searchBytes);
        scanningIndicator.stop();
        ArrayList<Long> endList = new ArrayList<Long>();
        writingIndicator.setText("Writing output");
        writingIndicator.start();
        if (findList.size() > 1)
        {
            for (int i = 1; i < findList.size(); i++)
            {
                endList.add(findList.get(i)-1);
            }
            endList.add(file.length());
        }
        if (findList.size() == 1)
        {
            endList.add(file.length());
        }        
        //Now that the location of each occurence of our pattern is known, pass
        //the list to writeFiles. From here, writeFiles will (as the name
        //implies) write our output files.
        writeFiles(inFileName, outFileName, outFileExtension, findList, endList);
        writingIndicator.stop();
        System.out.println("Processing complete. Enjoy your data!");
        
    }
    public static void writeFiles(String inFileName, String outFileName, String outFileExtension, List<Long> startPoints, ArrayList<Long> endPoints) throws Exception
    {
        BufferedInputStream inBuff;
        BufferedOutputStream outBuff;
        FileInputStream inFile;
        FileOutputStream outFile;
        for (int i = 0; i < startPoints.size(); i++)
        {
                   //Set up an output file stream.
                   outFile = new FileOutputStream(outFileName + i + "." + outFileExtension);
                   //Create a new input stream for reading the input file.
                   inFile = new FileInputStream(inFileName);
                   //Set up a buffered output stream that uses the output file stream.
                   //A buffer size of 32MB is used for this particular stream.
                   outBuff = new BufferedOutputStream(outFile, 1024*1024*32);
                   //Create a buffered input stream that uses the input file stream.
                   //Again the buffer size is 32MB.
                   inBuff = new BufferedInputStream(inFile, 1024*1024*32);
                   //Now read the file using the buffered streams and store the output in a file.
                   readFile(inBuff, outBuff, startPoints.get(i), endPoints.get(i));
                   //Flush the output buffer.
                   outBuff.flush();
        }
    }
    public static void readFile(BufferedInputStream streamer, BufferedOutputStream output, long start, long end) throws Exception
    {
        //Set the current position to the starting position.
        long currentPos = start;
        //Skip to the point in the stream we're interested in reading.
        streamer.skip(start);
        //This integer is the value of the current byte in the BufferedInputStream
        int currentByte;
        //Run as long as there is still data to be read or until the end of the range.
        while(((currentByte = streamer.read()) != -1) && (currentPos <= end))
        {
            if (currentPos >= start)
            {
                output.write(currentByte);
            }
            currentPos++;
        }
    }
}

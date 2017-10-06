/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argon.jconsplit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author Argonitious
 */
public class Splitter {
    //Reads a specified range of bytes from a buffered input stream and writes them into a buffered output stream.
    //TODO: Use the correct exceptions!
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
    //This is where the magic happens!
    //TODO: Split the code up into separate methods if possible.
    //TODO: Use the correct exceptions!
    public static void split(String inFileName, String outFileExtension, String outFileName, int[] pattern) throws Exception
    {
       int currentByte=0;
       int counter=0;
       int index=0;
       //This will be the percentage of the scanning process that has been completed.
       int percentage=0;
       //This will be the percentage currently being displayed.
       int percentDisplay=0;
       int num=0;
       //This is the number of bytes in the file, counted from zero.
       long bytesFromZero=0;
       long bytesRead=0;
       //The time the process was started at.
       long timeStart=0;
       //The time the process was completed.
       long timeEnd=0;


       int[] inputThing;

       //This ArrayList stores the exact position of the beginning of
       //each individual file that was concatenated into the input file.
       ArrayList<Long> startPoints = new ArrayList<>();
       //Same as above, but for storing the exact point where the files end.
       ArrayList<Long> endPoints = new ArrayList<>();
       //Used for formatting the program's progress as a percentage.
       NumberFormat percentFormat = NumberFormat.getPercentInstance();
       //Set to round to a tenth of a percent.
       percentFormat.setMinimumFractionDigits(0);

       //Initialize our file streams.
       FileInputStream originalFile;
       FileInputStream twoFile;
       FileOutputStream newFile;

       //Initialize our buffered streams.
       //
       //These are used to read the input streams more easily and efficiently.
       //Reading directly from input streams on large files is not very friendly
       //to a computer's resources.
       BufferedInputStream bufferThing;
       BufferedInputStream bufferTwo;

       BufferedOutputStream outBuff;

       //This variable is the number of bytes in the file, starting from zero.
       //To do this, we get the length of the file and subtract one from it.
       bytesFromZero = new File(inFileName).length()-1;
       //Set these two file streams to stream from the input file.
       //These first stream is used for scanning the file, while the other is
       //for copying data to the output files.
       originalFile = new FileInputStream(inFileName);
       twoFile = new FileInputStream(inFileName);
       //Set these buffered streams to use the two above file streams.
       bufferThing = new BufferedInputStream(originalFile, 1024*1024*32);
       bufferTwo = new BufferedInputStream(twoFile, 1024*1024*32);

       //This array holds the current portion of the file being scanned.
       //As the file is scanned, the first byte is removed from the array and
       //the bytes in the array are shifted to the left. The empty element at
       //the end of the array is replaced with the next byte.
       inputThing = new int[pattern.length];

       //Get the current time in milliseconds before the below processes start.
       //The time between the process starting and stopping will be displayed later.
       timeStart = System.currentTimeMillis();

       //Start scanning and de-concatinating the file.
       do
       {
           //Measure the percentage of the input file that has been read so far.
           percentage = Math.round((int)(bytesRead * 100.0 / bytesFromZero + 0.5));
           //This if statement decides if it is necessary to print the current percentage.
           //It will only be printed if the new percentage is bigger than the previous
           //one and is bigger than zero.
           if(percentage > 0 && percentage > percentDisplay && percentage < 100)
           {
                   //If the percentage modulus 1 is 0, we know the percentage is whole.
                   //We only want to print whole percentages after all.
               if (percentage % 1 == 0)
               {
                   //Make note of the percentage that is going to be displayed.
                   percentDisplay = percentage;
                   //Display the percentage.
                   System.out.print("\rProgress:\t" + percentFormat.format(percentage/100.0) + "\tExtracting data.");
               }
           }
           //Get the next byte from the file.
           currentByte = bufferThing.read();
           //The buffered stream will put out -1 if the are no more bytes to read.
           //We know we have reached the end of the file at this point. Now things
           //just need to be wrapped up.
           if (currentByte == -1)
           {
                   //If the magic bytes occurred at least once, copy the very last concatenated
                   //file and tell the user that the process has finished.
               if (startPoints.size() > 0)
               {
                   //Use the end of the file as the last end point of a concatenated file.
                   endPoints.add(bytesFromZero);
                   //Set up an output file stream.
                   newFile = new FileOutputStream(outFileName + counter + "." + outFileExtension);
                   //Set up a buffered output stream that uses the output file stream.
                   //A buffer size of 32MB is used for this particular stream.
                   outBuff = new BufferedOutputStream(newFile, 1024*1024*32);
                   //Create a new input stream for reading the input file.
                   twoFile = new FileInputStream(inFileName);
                   //Create a buffered input stream that uses the input file stream.
                   //Again the buffer size is 32MB.
                   bufferTwo = new BufferedInputStream(twoFile, 1024*1024*32);
                   //Now read the file using the buffered streams and store the output in a file.
                   readFile(bufferTwo, outBuff, startPoints.get(startPoints.size()-1), endPoints.get(endPoints.size()-1));
                   //Flush the output buffer.
                   outBuff.flush();
                   //Tell the user that the process has completed.
                   System.out.print("\rProgress:\t" + percentFormat.format(1.0) + "\tYay! Your files are ready!\n");
               }
               if (startPoints.isEmpty())
               {
                   //If no occurrences of the magic bytes are found, tell the user.
                   System.out.print("\rProgress:\t" + percentFormat.format(1.0) + "\tSorry, no matches found!\n");
               }
           }
           //Keep running if there is more data to read.
           if (currentByte != -1)
           {
               //Put the value of the current byte into the array at the current index.
               //The index starts at zero.
               inputThing[index] = currentByte;
               //Count the number of bytes read.
               bytesRead++;
               //If the index is less than or equal to the array's length -1, increment.
               if (index <= (inputThing.length - 1))
               {
                   index++;
               }
               //If the index is bigger than the array's length -1, the array is full.
               if (index > (inputThing.length - 1))
               {

                   if (Arrays.equals(pattern, inputThing))
                   {
                       //Mark the starting point for each file at the beginning of each occurrence
                       //of the magic bytes inside the input file.
                       startPoints.add(bytesRead - inputThing.length);
                       //If more than one starting point was found, throw in some more operations.
                       if(startPoints.size() > 1)
                       {
                           //If another occurrence of the magic bytes is found, mark an end point for
                           //the previous concatenated file that was found.
                           endPoints.add(bytesRead - inputThing.length - 1);
                           //Generate a new file.
                           newFile = new FileOutputStream(outFileName + counter + "." + outFileExtension);
                           //Prepare a buffered output stream for the new file.
                           outBuff = new BufferedOutputStream(newFile, 1024*1024*32);
                           //Reset the input file stream that the buffered input stream uses.
                           twoFile = new FileInputStream(inFileName);
                           //Prepare a buffered input stream to copy data from.
                           bufferTwo = new BufferedInputStream(twoFile, 1024*1024*32);
                           //Now read the file using the buffered streams and store the output in a file.
                           readFile(bufferTwo, outBuff, startPoints.get(counter), endPoints.get(counter));
                           //Flush the buffered output stream.
                           outBuff.flush();
                           
                           //Count each file that will be created.
                            counter++;
                       }
                   }
                   //Shift the bytes in the array down by 1, making room for the next byte.
                   //The first byte is removed from the array in the process, since we only
                   //need to look at 16 bytes (or whatever number of bytes make up the file's
                   //magic bytes) at any given time.
                   for(int j = 1; j < inputThing.length; j++)
                   {
                           //Copy the byte at the current index.
                       num = inputThing[j];
                       //Move the byte down to the previous index.
                       inputThing[j-1] = num;
                   }
                   //Set the index to the end of the array, since we need to put a new byte
                   //into the last index.
                   index = inputThing.length - 1;
               }
           }
       } while (currentByte != -1);

       //Get the current time in milliseconds after the above processes finish.
       //The time between the process starting and stopping will be displayed later.
       timeEnd = System.currentTimeMillis();

       //Close all of the buffered streams
       bufferThing.close();
       bufferTwo.close();

       //For some reason, closing the output stream causes a null pointer exception
       //outBuff.close();

       //Close all of the file streams
       originalFile.close();

       //For some reason, closing the output stream causes a null pointer exception
       //newFile.close();
       twoFile.close();
       //Print out the time taken to generate the output files.
       System.out.println(counter+1 + " files created in " + ((timeEnd-timeStart)/1000) + " seconds.");
    }
}

/**
 * This java program separates concatenated files. It splits the concatenated file whenever it finds
 * the magic numbers for the file type the you want to get back. If you wanted to split concatenated
 * WAV audio files, for example, you would need to give the program the magic numbers 52494646 - the
 * hexadecimal equivalent of "RIFF".
 * 
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
public class Main
{
   //This is where the magic happens!
   //TODO: Split the code up into separate methods if possible.
   //TODO: Use the correct exceptions!
   public static void main(String[] args) throws Exception
   {
       int currentByte = 0;
       int counter = 0;
       int counterToo = 0;
       int index = 0;
       //This will be the percentage of the scanning process that has been completed.
       int percentage = 0;
       //This will be the percentage currently being displayed.
       int percentDisplay = 0;
       int num = 0;
       //This is the number of bytes in the file, counted from zero.
       long bytesFromZero = 0;
       long bytesRead = 0;
       //The time the process was started at.
       long timeStart = 0;
       //The time the process was completed.
       long timeEnd = 0;
       
       ArrayList<Integer> pattern2 = new ArrayList<>();
       int[] pattern;
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
       
       //A string for storing the input filename.
       String inFileName = "";
       //Strings for storing the output filename and extension.
       String outFileName = "";
       String outFileExtension = "";
       
       //Initialize our file streams.
       FileInputStream originalFile = null;
       FileInputStream twoFile = null;
       FileOutputStream newFile = null;
       
       //Initialize our buffered streams.
       //
       //These are used to read the input streams more easily and efficiently.
       //Reading directly from input streams on large files is not very friendly
       //to a computer's resources.
       BufferedInputStream bufferThing = null;
       BufferedInputStream bufferTwo = null;
       
       BufferedOutputStream outBuff = null;
       
       //Print out the title and some warnings about the nature of the software.
       System.out.println("THIS IS EXPERIMENTAL SOFTWARE! I AM NOT RESPONSIBLE FOR ANY");
       System.out.println("DAMAGE THIS SOFTWARE MAY CAUSE! USE IT AT YOUR OWN RISK!");
       System.out.println("\n-----Concatenated File Splitter (EXPERIMENTAL)-----\n");
       
       //Ask the user for magic numbers and store the input in the ArrayList, pattern2.
       askMagicNumbers(pattern2);
       
       //Ask the user for an input filename.
       inFileName = askInFile();
       //Ask the user for a base name for the output files.
       outFileName = askOutFileName();
       //Ask the user for the extension to use on the output files.
       outFileExtension = askOutFileExtension();
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
       //Make an array for storing the pattern that was given earlier.
       pattern = new int[pattern2.size()];
       //Copy everything from "pattern2" into the pattern array.
       for(int i = 0; i < pattern.length; i++)
       {
           pattern[i] = pattern2.get(i);
       }
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
           if(percentage > 0 && percentage > percentDisplay)
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
                   newFile = new FileOutputStream(outFileName + counterToo + "." + outFileExtension);
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
               if (startPoints.size() == 0)
               {
            	 //IF no occurrences of the magic bytes are found, tell the user.
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
                	   //Count each file that will be created.
                       counter++;
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
                           newFile = new FileOutputStream(outFileName + counterToo + "." + outFileExtension);
                           //Prepare a buffered output stream for the new file.
                           outBuff = new BufferedOutputStream(newFile, 1024*1024*32);
                           //Prepare a buffered input stream to copy data from.
                           bufferTwo = new BufferedInputStream(twoFile, 1024*1024*32);
                           //Reset the input file stream that the buffered input stream uses.
                           twoFile = new FileInputStream(inFileName);
                           //Now read the file using the buffered streams and store the output in a file.
                           readFile(bufferTwo, outBuff, startPoints.get(counterToo), endPoints.get(counterToo));
                           //Flush the buffered output stream.
                           outBuff.flush();
                           //Increment the file number.
                           counterToo++;
                       }
                   }
                   //Shift the bytes in the array down by 1, making room for the next byte.
                   //The first byte is removed from the array in the process, since we only
                   //need to look at 4 bytes (or whatever number of bytes make up the file's
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
       System.out.println(counter + " files created in " + ((timeEnd-timeStart)/1000) + " seconds.");
   }
   
   //TODO: Check the logic in the input and output filename getters.
   //Ask the user for an input filename.
   public static String askInFile()
   {
	   //Ask the user for the input file.
       Scanner input = new Scanner(System.in);
       //Store the filename here.
       String fileName = "";
       //Indicates whether the user entered an acceptable filename.
       boolean nameEntered = false;
       //Loop until the user enters a usable filename.
       do
       {
    	   //Ask for input.
           System.out.print("Enter the name of your input file: ");
           //Get the input.
           fileName = input.nextLine();
           //See if the filename and path already exists.
           nameEntered = isFilenameValid(fileName);
           //If a filename that does not exist is entered...
           if (!nameEntered)
           {
        	   //Display a message.
               System.out.println("Not a valid filename!");
           }
       } while (!nameEntered);
       //Close the scanner.
       //input.close();
       //Return the filename.
       return fileName;
   }
   
   //TODO: Check the logic in the input and output filename getters.
   //Ask the user for the extension to be used on the output files.
   public static String askOutFileExtension()
   {
	   //A scanner for user input.
       Scanner input = new Scanner(System.in);
       //A string for storing the file extension.
       String extension = "";
       //Indicate if the user entered a filename that already exists.
       boolean nameEntered = false;
       //Run until a filename that doesn't already exist is entered.
       do
       {
    	   //Ask for input.
           System.out.print("Enter the extension to use for your output file(s): ");
           //Store the user's input.
           extension = input.nextLine();
           //See if the input filename already exists.
           nameEntered = isFilenameValid(extension);
           //If it does not already exist, display a  message.
           if (!nameEntered)
           {
               System.out.println("Not a valid extension!");
           }
       } while (!nameEntered);
       System.out.println();
       //Close the scanner.
       //input.close();
       //Return the extension
       return extension;
   }
   
   //TODO: Check the logic in the input and output filename getters.
   //Ask the user for an output filename.
   public static String askOutFileName()
   {
	   //A scanner for user input.
       Scanner input = new Scanner(System.in);
       //A string for storing the filename.
       String fileName = "";
       //Indicates if the user entered a filename that already exists.
       //We don't want to accidentally overwrite something, do we?
       boolean nameEntered = false;
       //Run until the user enters a filename that does not yet exist.
       do
       {
    	   //Ask for input.
           System.out.print("Enter the base name for your output file(s): ");
           //Get the user's input.
           fileName = input.nextLine();
           //Was a pre-existing filename given?
           nameEntered = isFilenameValid(fileName);
           //The user entered a filename that currently exists.
           if (!nameEntered)
           {
        	   //Display a warning message.
               System.out.println("Not a valid filename!");
           }
       } while (!nameEntered);
       //Close the input.
       //input.close();
       //Return the filename.
       return fileName;
   }
   
   //TODO: Check the logic in the input and output filename getters.
   //Ask the user for the magic numbers of the desired files.
   public static void askMagicNumbers(ArrayList<Integer> pattern)
   {
	   //Indicates if the user's input string is in hexadecimal format.
       boolean isHex = false;
       //Indicates whether the user's input is even and long enough.
       boolean userCheck = false;
       //The index of the last character that was read.
       int lastIndex = 0;
       //A scanner for user input.
       Scanner input = new Scanner(System.in);
       //A string for storing the user's input.
       String userChoice = null;
       //Run this loop while the userCheck variable is true.
       do
       {
    	   //Ask for some input.
           System.out.print("Enter the magic numbers for your file type: ");
           //Get the input.
           userChoice = input.nextLine();
           //See if the input is in hexadecimal format.
           isHex = userChoice.matches("^[0-9a-fA-F]+$");
           //If the length of the user's input is too short or has an odd length...
           if ((userChoice.length() < 4 || ((userChoice.length() % 2) != 0)) || !isHex)
           {
        	   //Set userCheck to true, so the loop keeps running.
               userCheck = true;
               //Display a message.
               System.out.println("Input must be in hexadecimal format.");
           }
           //Things check out, so let's go!
           else
           {
        	   //Walk through the input string.
               for (int i = 1; i < userChoice.length(); i+=2)
               {
            	   //Get the decimal value of each hexadecimal digit and add it
            	   //to the pattern list for later.
                   pattern.add(hex2decimal(userChoice.substring(lastIndex, i+1)));
                   //Increase the index by 1, so we know where the previously
                   //read character is inside the input string.
                   lastIndex = i+1;
               }
               //Our work here is done. Set userCheck to false.
               userCheck = false;
           }
       } while(userCheck);
       //Close the scanner.
       //input.close();
   }
   
   //Reads a specified range of bytes from a buffered input stream and writes them into a buffered output stream.
   //TODO: Use the correct exceptions!
   public static void readFile(BufferedInputStream streamer, BufferedOutputStream output, long start, long end) throws Exception
   {
	   //Set the current position to the starting position.
       long currentPos = start;
       //Skip to the point in the stream we're interested in reading.
       streamer.skip(start);
       //This integer is the value of the current byte in the BufferedInputStream
       int currentByte = 0;
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
   
   //See if a given filename and path actually exists.
   public static boolean isFilenameValid(String file)
   {
	   //Initialize a file object
       File f = new File(file);
       
       //Try to get the canonical path of the file.
       //If it succeeds, the path is valid.
       try
       {
           f.getCanonicalPath();
            return true;
       }
       //Something about the path is obviously incorrect, so return false.
       catch (IOException e)
       {
           return false;
       }
       //Hopefully, there won't be anything other than an IOException.
       //This is just here for safety's sake. Better safe than sorry!
       catch (Exception e)
       {
    	   return false;
       }
   }
   
   //Converts hexadecimal strings into decimal integers
   public static int hex2decimal(String s)
   {
	    //These are all the possible hex values arranged from lowest to highest.
        String digits = "0123456789ABCDEF";
        //Convert all the letters to upper case, for the sake of simplicity.
        s = s.toUpperCase();
        //The decimal value of the hex string.
        int val = 0;
        //Walk through the input string and add the value of each hex digit.
        for (int i = 0; i < s.length(); i++)
        {
        	//Get the character at index i of the input string.
            char currentByte = s.charAt(i);
            //Get the index of the current character in digits.
            int d = digits.indexOf(currentByte);
            //In the case there are multiple digits, multiply the previous result
            //by 16 before adding the current digit. This makes a lot more sense
            //when you try it on paper!
            val = 16*val + d;
        }
        return val;
   }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argon.jconsplit;

/**
 *
 * @author Argonitious
 */
/**
 * This java program separates concatenated files. It splits the concatenated file whenever it finds
 * the magic numbers for the file type the you want to get back. If you wanted to split concatenated
 * WAV audio files, for example, you would need to give the program the magic numbers 52494646 - the
 * hexadecimal equivalent of "RIFF".
 * 
 */
import static com.argon.jconsplit.Splitter.split;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
public class Main
{
   public static void main(String[] args) throws Exception
   {
       //Print out the title and some warnings about the nature of the software.
       System.out.println("JConSplit ~ The Concatenated File Splitter for Java");
       System.out.println("--------------------DEVELOPMENT--------------------\n");
       
       String inFileName = askInFile();
       String outFileName = askOutFileName();
       String outFileExtension = askOutFileExtension();
       int[] pattern = askMagicNumbers();
       
       split(inFileName, outFileExtension, outFileName, pattern);
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
   public static int[] askMagicNumbers()
   {
       //Indicates if the user's input string is in hexadecimal format.
       boolean isHex;
       //Indicates whether the user's input is even and long enough.
       boolean userCheck = false;
       //The index of the last character that was read.
       int lastIndex = 0;
       //A scanner for user input.
       Scanner input = new Scanner(System.in);
       //A string for storing the user's input.
       String userChoice;
       int[] pattern;
       //Run this loop while the userCheck variable is true.
       do
       {
    	   //Ask for some input.
           System.out.print("Enter the magic numbers for your file type: ");
           //Get the input.
           userChoice = input.nextLine();
           pattern = new int[userChoice.length()/2];
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
               int position = 0;
        	   //Walk through the input string.
               for (int i = 1; i < userChoice.length(); i+=2)
               {
            	   //Get the decimal value of each hexadecimal digit and add it
            	   //to the pattern array for later use.
                   pattern[position] = hex2decimal(userChoice.substring(lastIndex, i+1));
                   position += 1;
                   //Increase the index by 1, so we know where the previously
                   //read character is inside the input string.
                   lastIndex = i+1;
               }
               //Our work here is done. Set userCheck to false.
               userCheck = false;
           }
       } while(userCheck);
       System.out.print(Arrays.toString(pattern));
       return pattern;
       //Close the scanner.
       //input.close();
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
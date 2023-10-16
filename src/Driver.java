import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Driver {
  
//===========\\
  // CONSTANTS \\
  //===========\\
  
  // Note that these static fields (class variables) are final, so they
  // cannot be changed during program execution.
  
  // Literal file names. Changes not recommended.
  final static String CAGED_BIRD_F = "cagedBirdPassage.txt";
  final static String HOBBIT_F = "theHobbitPassage.txt";
  final static String PANAMA_F = "panama.txt";
  final static String MYSTERY_F = "mysteryPassage.txt";

  // Output file names. Changes not recommended.
  final static String ENCODE_OUT_F = "encoded.txt";
  final static String DECODE_OUT_F = "decoded.txt";
  
  // Options for which kind of tree to use. DO NOT CHANGE.
  // 0. Attempt to construct new Huffman Tree from a text file and
  //    the character frequencies therein.
  // 1. Use a predefined "standard" tree with all typical English
  //    characters and frequencies based on a typical text passage.
  // 2. Use a small test tree with only the characters and frequencies
  //    from "a man, a plan, a canal, panama".
  final static int TREE_TYPE_FROM_TEXT = 0;
  final static int TREE_TYPE_STD = 1;
  final static int TREE_TYPE_TEST = 2;

  // Tree build selection. CHANGE AS DESIRED.
  final static int TREE_TYPE = TREE_TYPE_STD;
  
  final static boolean SAVE_TREE = true;
  final static boolean DONT_SAVE_TREE = false;
  final static boolean SAVE_TREE_SETTING = SAVE_TREE;
  
  // File selections. CHANGE AS DESIRED.
  final static String TREE_F = CAGED_BIRD_F;   // File to build tree from
  final static String ENCODE_F = CAGED_BIRD_F; // File to encode
  final static String DECODE_F = MYSTERY_F;    // File to decode
  // (You may want DECODE_F = ENCODE_OUT_F to decode what was just encoded.)
  
  /**
   * Tree output format. CHANGE AS DESIRED.
   * true:  Display tree with all bits shown before each leaf
   * false: Only show each "branch bit" once to illustrate branching structure
   */
  final static boolean DISP_ALL_BITS = true;
  
  /**
   * Tree build option. CHANGE AS DESIRED.
   * true:  "Fill in gaps" in character map for a more versatile tree
   * false: Build tree that ONLY encodes characters found in source text
   * 
   * Note that until you complete the appropriate function, the program will
   * always act as if this were false.
   */
  final static boolean FILL_GAPS = true;
  
  /**
   * Displayed text size limit. CHANGE AS DESIRED.
   */
  final static int DISPLAY_LIMIT = 1000;
  
  //=============\\
  // MAIN METHOD \\
  //=============\\
  
  /**
   * Driver method to build Huffman tree, encode, and decode files.
   * Tree mode and file selection is specified by class constants.
   */
  public static void main(String[] args) {
    //======================================\\
    // GENERATE TREE, STANDARD OR FROM FILE \\
    //======================================\\
    System.out.println("===============");
    System.out.println("GENERATING TREE");
    System.out.println("===============");
    
    HuffmanTree tree = makeTree();
    if (!tree.isValid()) {
      System.err.println("Error: Invalid tree. Program aborted.");
      return;
    }
    tree.display(); // (OPTIONAL) View generated tree.
    
    // Map each character in tree to corresponding bit String representation.
    //   This will be necessary for encoding characters into bits.
    Map<Character, String> bitStrings = tree.bitStrings();
    
    // (OPTIONAL) View bit String mappings.
    System.out.println(bitStrings);
    if(TREE_TYPE != TREE_TYPE_STD) {
    	tree.printStats();
        System.out.println("This is the Bit Rep of the tree, it's kind of long...");
        System.out.println(tree.bitRep());
    }
    if(SAVE_TREE_SETTING) {
    	writeFile("treeBitRep.txt", tree.bitRep());
    }
    // TODO (OPTIONAL): Convert tree ITSELF to bit String and display.
    

    //======================\\
    // READ AND ENCODE FILE \\
    //======================\\
    System.out.println();
    System.out.println("=============");
    System.out.println("ENCODING FILE");
    System.out.println("=============");

    System.out.println("Attempting to encode " + ENCODE_F);
    char[] encodeFileChars = fileChars(ENCODE_F);
    if (encodeFileChars == null) {
      System.err.println("Warning: Could not read file to encode: " + ENCODE_F);
    }
    else {
      String encodedText = encode(bitStrings, encodeFileChars);
      System.out.println("Encoded Text Length: " + encodedText.length());
      if (encodedText.length() < DISPLAY_LIMIT) {
        System.out.println("Encoded text:");
        System.out.println(encodedText);
      }
      else {
        System.out.println("Encoded text too long to display, see file.");
      }
      
      writeFile(ENCODE_OUT_F, encodedText);
      System.out.println("Encoded text written to " + ENCODE_OUT_F);
    }
    
    //======================\\
    // READ AND DECODE FILE \\
    //======================\\
    System.out.println();
    System.out.println("=============");
    System.out.println("DECODING FILE");
    System.out.println("=============");
    
    System.out.println("Attempting to decode " + DECODE_F);
    char[] decodeFileChars = fileChars(DECODE_F);
    if (decodeFileChars == null) {
      System.err.println("Warning: Could not read file to decode: " + DECODE_F);
    }
    else {
      String decodedText = tree.decode(decodeFileChars);
      System.out.println("Decoded Text Length: " + decodedText.length());
      if (decodedText.length() < DISPLAY_LIMIT) {
        System.out.println("Decoded text:");
        System.out.println(decodedText);
      }
      else {
        System.out.println("Decoded text too long to display, see file.");
      }
      
      writeFile(DECODE_OUT_F, decodedText);

      System.out.println("Decoded text written to " + DECODE_OUT_F);
    }

    
    
  }
  
  static HuffmanTree makeTree() {
    if (TREE_TYPE == TREE_TYPE_STD) {
      System.out.println("Using standard tree.");
      return HuffmanTree.stdTree();
    }
    if (TREE_TYPE == TREE_TYPE_TEST) {
      System.out.println("Using small test tree.");
      return HuffmanTree.testTree();
    }
    
    // Attempt to read characters of the file selected for tree generation.
    System.out.println("Using " + TREE_F + " for tree generation.");
    char[] treeGenChars = fileChars(TREE_F);
    if (treeGenChars == null) {
      // Fall back on "standard tree" if necessary.
      // System.err is similar to System.out but intended for error messages.
      System.err.println("Warning: Could not read file for tree generation. "
                       + "Using standard tree.");
      return HuffmanTree.stdTree();
    }
    
    // Generate tree from text file.
    HuffmanTree tree = new HuffmanTree(treeGenChars);
    if (!tree.isValid()) {
      System.err.println("Warning: Failed to generate tree from file. Using standard tree.");
      return HuffmanTree.stdTree();
    }
    
    return tree;
  }
  
  /**
   * Encodes text to bit String using provided mapping (most likely generated
   * by traversing Huffman Tree, one mapping per leaf). Ignores/skips any
   * chars absent from map.
   * 
   * @param bitStrings mappings from chars to bit Strings for each character.
   * @param text       text to encode.
   * @return           the encoded version of the text
   */
  static String encode(Map<Character,String> bitStrings, char[] text) {
    StringBuilder output = new StringBuilder();
    
    for(char c: text) {
    	String encodedChar = bitStrings.get(c);
    	if(encodedChar != null) {
    		output.append(encodedChar);
    	}
    }
    
    // TODO: encode the text using the bitStrings given
    
    return output.toString();
  }

  
  //=============================\\
  // FILE INPUT / OUTPUT METHODS \\
  //=============================\\
  
  /**
   * Reads in entire file from project base directory.
   * 
   * @param fileName name of file to read.
   * @return         chars from the file, including newlines.
   *                 or null if file could not be read.
   */
  static char[] fileChars(String fileName) {
    // Scanner reads from sources like files.
    Scanner scan = null;
    
    try {
      scan = new Scanner(new File(fileName));
    }
    // If file could not be read, return nothing.
    catch (FileNotFoundException fnfe) {
      return null;
    }
    
    // Set scanner to only stop at end of file (\Z is regex code for EoF).
    scan.useDelimiter("\\Z");
    char[] chars;
    try {
      // .next() returns the String starting from current Scanner position in
      //   file including all contents up to next delimiter - in this case, the
      //   end of the file.
      chars = scan.next().toCharArray();
    }
    catch (NoSuchElementException nsee) {
      System.err.println("Warning: Empty file " + fileName + ".");
      chars = new char[0]; // 0-length array.
    }
    scan.close(); // Close file.
    
    return chars;
  }
  
  /**
   * Writes provided String to any file in project base directory.
   * 
   * @param fileName file to write to.
   * @param text     contents to put in file.
   */
  static void writeFile(String fileName, String text) {
    try {
      // PrintWriter allows writing to various destinations such as files
      // using functions similar to System.out's.
      PrintWriter out = new PrintWriter(new File(fileName));
      out.print(text);  // Write text to file.
      out.close();      // Close file.
    }
    catch (FileNotFoundException fnfe) {
      System.err.println("Warning: Could not write to file: " + fileName);
    }
  }

}

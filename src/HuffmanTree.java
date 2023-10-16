import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class HuffmanTree {
  /**
   * Bit String representation of "standard" tree. DO NOT CHANGE.
   * This bit String was generated from "cagedBirdPassage.txt" with 
   * "gaps" in the character frequency map filled in, so it should
   * be able to encode most English text. 
   */
  private final static String STD_TREE_BIT_STR =
    "000010110100000101111001101110000010110001000000000100111111001010100011010101101010110100001010110000100100001101000100100111010010011101110111000110101100110101000001010100101010011000101000111010100101110010111101010000111001011011000011011011001010010111001110110111000101101100000010010100010010100110111011010000101010110011010111001000010110111110110000100000010100111010101011110110101110010111010110011110110010010111010000000000101000101101101010101001001010100001001011110001010001100010100110110100000110101001110110110101011101110001010101000101001010100100010001001001111010010000010111101010101010110100111110010110001011010010101110101101100011100100000";

  private final static String TEST_TREE_BIT_STR =
    "0010010000001001011000101100011101110000001011011100101101101101101100101100001";
  
  private Map<Character,Integer> freqs;
  // frequencies of chars used to create this tree
  
  private HuffmanNode root;
  
  /**
   * Constructs Huffman Tree based on the frequencies of a set of chars.
   * 
   * @param chars array to base frequencies on.
   */
  public HuffmanTree(char[] chars) {
    genFrequencyMap(chars);
    
    // If option is set, don't just make do with the characters provided,
    //   also include certain absent characters, before converting to tree.
    if (Driver.FILL_GAPS) freqGapCheck();
    
    mapToTree();
  }
  
  // Private constructor! Can only be used by internal methods, for
  //   instance stdTree().
  private HuffmanTree(HuffmanNode root) {
    this.root = root;
  }
  
  public void printStats() {
    // TODO: Add frequency statistics/other calculations.
	System.out.println("Total Unique Characters: " + freqs.size());
	int sum = 0;
	for(Character key: freqs.keySet()) {
		if(freqs.get(key) > 0) sum += freqs.get(key);
	}
	System.out.println("Total Characters: " + sum);
  }
  
  private void getStats() {
	  
  }
  
  public boolean isValid() {
    return root != null;
  }
  
  /**
   * Counts frequencies of each unique character from provided array.
   * Stores frequencies in this tree's frequency map.
   * 
   * @param chars set to count frequencies from.
   */
  private void genFrequencyMap(char[] chars) {
    freqs = new HashMap<>();
    // Could use TreeMap or HashMap. TreeMap might be nice for testing,
    //   but no long-term reason we actually need sorted chars.
    freqGapCheck();
    for (char c : chars) {
      // TODO: Complete.
    	Character charC = new Character(c);
    	if(freqs.containsKey(charC)) freqs.replace(charC, freqs.get(charC) + 1);
    	else freqs.put(charC, new Integer(1));
    }
  }
  
  /**
   * Fills in "gaps" in frequency map of characters to ensure certain
   *   characters are present, even if with a frequency of 0.
   *   
   * Gaps to check:
   * - All capital letters
   * - All lowercase letters
   * - Characters with ASCII values 32-34: ' ' '!' and '"'
   * - Characters with ASCII values 39-41: '\'' '(' and ')'
   * - Characters with ASCII values 44-47: ',' '-' '.' and '/'
   * - These characters with inconvenient ASCII values:
   *      '\n' '\r' ':' ';' '?'
   * 
   * @param freqs the frequency map to be filled in.
   */
  private void freqGapCheck() {
    // TODO (OPTIONAL): Complete.
	  for(int i = 65; i <= 90; i++) {
		  freqs.put(new Character((char)i), new Integer(0));
		  freqs.put(new Character((char)(i+22)), new Integer(0));
	  }
	  int[] uncomChars = new int[] {32,33,34,39,40,41,44,45,46,47,110,114,58,59,63};
	  for(int i: uncomChars) {
		  freqs.put(new Character((char)i), new Integer(0));
	  }
  }
  
  /**
   * Converts a frequency map to a Huffman Tree encoding the characters from
   * the map, with the more frequent chars higher in the tree.
   *
   * @param freqs mappings from unique chars to their frequencies;
   *              must include at least one character.
   * @return      root of Huffman Tree generated.
   */
  private void mapToTree() {
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<HuffmanNode>();
    // The built-in Java PriorityQueue automatically prioritizes
    //   for *lowest* comparison first. This can use a provided Comparator,
    //   or the "natural ordering" (including Comparable objects).
    // HuffmanNodes implement Comparable<HuffmanNode> based on frequency.
    
    // Useful PriorityQueue functions: .add(), .poll(), .size()
    //   (see online documentation for details)
    // TODO: Follow Huffman Tree construction process to assemble a tree
    //   based on the "freqs" map of characters and frequencies.
    for(Character key: freqs.keySet()) {
    	pq.add(new HuffmanLeaf(key, freqs.get(key)));
    }
    
    while(pq.peek() != null && pq.size() > 1) {
    	pq.add(new HuffmanParent(pq.poll(), pq.poll()));
    }
    root = pq.poll();
  }
  
  /**
   * Decodes a sequence of 1s and 0s using this Huffman Tree.
   * 
   * @param bits the '1's and '0's to decode.
   * 
   * @return     text decoded from the provided bits and this tree.
   */
  String decode(char[] bits) {
    StringBuilder output = new StringBuilder();
    
    CharArrayIterator bitsIt = new CharArrayIterator(bits);
    while (bitsIt.hasNext()) {
      Character decoded = root.decode(bitsIt);
      
      // null result indicates no char could be generated because
      //   not enough bits were available to reach a tree leaf.
      if (decoded != null) {
        output.append(decoded);
      }
    }
    
    // Convert StringBuilder back to String.
    return output.toString();
  }
  
  /**
   * Generates the full standard tree from the predefined constant.
   * The standard tree has all typical characters, at reasonable
   * frequencies.
   * 
   * @return Standard tree.
   */
  public static HuffmanTree stdTree() {
    char[] chars = STD_TREE_BIT_STR.toCharArray();
    System.out.println("Test Tree Char Array: " + new String(chars));
    CharArrayIterator treeBits = new CharArrayIterator(chars);
    
    // Try this with the expectation of a possible error (Exception)
    try {
      HuffmanTree result = new HuffmanTree(HuffmanNode.loadNode(treeBits));
      if (treeBits.hasNext()) {
        System.err.println("Warning: Some bits were not used while loading standard tree.");
      }
      return result;
    }
    // If process runs out of bits to form a valid tree, it will "throw" an
    //   Exception. We can "catch" it to keep the program from crashing, and
    //   instead just give up on this tree.
    catch (NoSuchElementException nsee) {
      System.err.println("Warning: Could not read standard tree.");
      return null;
    }
    // Most types of Exception can be caught; each catch statement catches
    //   any its type could apply to. It is best practice to only catch
    //   the most specific type of Exception you are expecting and intending
    //   to handle, rather than a very general Exception type that could
    //   mask underlying problems with the code.
    
    // You could catch multiple Exceptions after a try block with additional
    //   catch blocks, and handle each differently, much like a chain of
    //   else if blocks.
  }
  
  /**
   * Generates small test tree from the predefined constant.
   * The test tree has characters and frequencies from the text
   * "a man, a plan, a canal, panama". 
   * 
   * @return Test tree.
   */
  public static HuffmanTree testTree() {
    char[] chars = TEST_TREE_BIT_STR.toCharArray();
    String charsToString = new String(chars);
    System.out.println("Test Tree Char Array: " + charsToString);
    CharArrayIterator treeBits = new CharArrayIterator(chars);
    
    try {
      HuffmanTree result = new HuffmanTree(HuffmanNode.loadNode(treeBits));
      if (treeBits.hasNext()) {
        System.err.println("Warning: Some bits were not used while loading test tree.");
      }
      return result;
    }
    catch (NoSuchElementException nsee) {
      System.err.println("Warning: Could not read test tree.");
      return null;
    }
  }
  
  /**
   * Builds bit String representation of tree starting from root.
   * Each node is represented in pre-order by:
   *    0 for parent node (followed by its children), OR
   *    1 for child node, followed by 8-bit ASCII code for its character
   * 
   * Kick-starts recursive HuffmanNode.buildBitRep(StringBuilder) method.
   * 
   * @return bit String representing this tree. 
   */
  public String bitRep() {
    StringBuilder sb = new StringBuilder();
    root.buildBitRep(sb);
    return sb.toString();
  }
  
  /**
   * Generates bit map giving quick access to encoding Strings for ALL
   * chars in leaves of a finished tree.
   * 
   * Kick-starts recursive HuffmanNode.setBitStrings(String, Map) method.
   * 
   * @return the mappings from each leaf character to the bit String
   *         encoding its position in the tree.
   */
  public Map<Character, String> bitStrings() {
    Map<Character, String> bitMap = new HashMap<>();
    // It takes no bits to get to the root node: ""
    root.setBitStrings("", bitMap);
    return bitMap;
  }

  /**
   * Displays tree with root at left, tree "descending" to right. Display of
   * 0s & 1s in tree can be toggled with DISP_ALL_BITS constant.
   * 
   * Kick-starts recursive HuffmanNode.display(StringBuilder) method.
   */
  void display() {
    if (Driver.DISP_ALL_BITS) {
      System.out.println("Tree displayed in left-to-right order of leaves, with all parent bits shown for each leaf.");
    }
    else {
      System.out.println("Tree displayed with root at left, leaves at right. Each 0 or 1 is a branch in tree.");
    }
    root.display(new StringBuilder());
  }
}

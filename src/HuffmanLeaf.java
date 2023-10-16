import java.util.Map;


/**
 * Leaf node of Huffman Tree.
 */
class HuffmanLeaf extends HuffmanNode {
  /**
   * Character represented by this node.
   */
  Character c;
  
  /**
   * Sole constructor. Constructs leaf for given character at
   * specified frequency.
   * buildBitRep
   * @param c         represented character.
   * @param frequency character's frequency in source. May be 0 if tree
   *                  is pre-determined rather than built from source.
   */
  HuffmanLeaf(Character c, int frequency) {
    this.c = c;
    this.frequency = frequency;
  }
  
  //===================\\
  // METHODS TO FINISH \\
  //===================\\
  
  @Override
  void setBitStrings(String bitCode, Map<Character, String> bitMap) {
    // TODO: How does this fit into encoding process?
	  // I think that this is for assembling out bitmap so we can encode messages into bits
	  bitMap.put(c, bitCode);
	  
  }
  
  @Override
  Character decode(CharArrayIterator bits) {
    // TODO: How does this fit into decoding process?
	return c;
  }
  
  //===================\\
  // COMPLETED METHODS \\
  //===================\\
  
  @Override
  void buildBitRep(StringBuilder sb) {
    // Appends a leaf to the bit String as follows:
    
    // Each leaf node is represented by 1 followed by 8-bit char code.
    sb.append('1');
    
    // This is complex. Don't panic!
    
    // 0x100 (hexadecimal) = 1 0000 0000 (binary)
    // | is "bitwise or" - each aligned bit of the operands are "or'd"
    //   1 | 1 = 1         1 | 0 = 1
    //   0 | 1 = 1         0 | 0 = 0
    
    // ex: 0011          1 0000 0000      
    //   | 1010        | 0 0110 1010
    //   = 1011        = 1 0110 1010
    
    // So, 0x100 & c means:
    // - The rightmost 8 bits of the character code for c control those bits
    //   of the result.
    // - The 9th (leftmost) bit is always 1.
    
    // So, the String conversion function works, but won't remove leading
    //   0s in the 8 right bits as it normally does (due to 9th bit being 1).
    
    // Then, .substring(1) gets the substring that skips the leftmost bit
    //   (the always-1 bit) leaving exactly the 8 bits of the char code,
    //   including leading 0s.
    sb.append(Integer.toBinaryString(0x100 | c).substring(1));
  }
  
  @Override
  void display(StringBuilder prefix) {
    // Prefix parameter is needed for branches only. All parent nodes
    // handle the prefix for their children; this node only needs
    // to display its own info.
    
    // Display newline without disrupting tree structure.
    if (c == '\n') {
      System.out.println("'\\n'");
    }
    // Same for carriage return.
    else if (c == '\r') {
      System.out.println("'\\r'");
    }
    // Display non-line-breaking chars directly, in single quotes.
    else {
      System.out.println("'" + c + "'");
    }
  }
}
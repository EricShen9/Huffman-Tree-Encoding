import java.util.HashMap;
import java.util.Map;

/**
 * Node of a Huffman Tree, which must be a parent or a leaf node. In
 * either case, nodes are comparable by frequency to enable easy
 * prioritization while building the tree.
 */
abstract class HuffmanNode implements Comparable<HuffmanNode> {
  /**
   * Reference to the parent of this node. Not actually needed in
   * this program, but would be needed for a bottom-up recursive
   * method to find the bit String for a given leaf/node.
   */
  HuffmanParent parent;
  
  /**
   * Frequency of the characters in or under this node. (May be 0
   * if tree is pre-generated.)
   */
  int frequency;
  
  /**
   * Compares HuffmanNodes by frequency. Required for Comparable.
   * Useful for priority queue.
   * 
   * @param other node to compare this one to
   * @return      negative if this has smaller frequency than other,
   *              positive if this has greater frequency,
   *              and 0 if they have the same frequency
   */
  public int compareTo(HuffmanNode other) {
    // TODO: how should nodes be compared?
	if(other.frequency > frequency) {
		return -1;
	} else if(other.frequency < this.frequency) {
		return 1;
	}
    return 0;
  }
  
  //=============================\\
  // ENCODING / DECODING METHODS \\
  //=============================\\

  /**
   * Recursively traverses tree from top to bottom, building up bit Strings
   * and adding them to map when leaves are reached.
   * 
   * Helper function for bitStrings().
   * 
   * @param prefix Bit String representing pathway from the root down
   *               to this node. Any children will add to this.
   * @param bitMap Map of bit Strings to add to when leaves are reached.
   */ 
  abstract void setBitStrings(String prefix, Map<Character, String> bitMap);
  
  /**
   * Decodes a bit sequence by following corresponding tree branches
   * from this node. Will only read until a leaf is found; will not
   * attempt to use all bits.
   * 
   * @param bits iteration to advance through while decoding, advanced to
   *             appropriate state for next character
   * @return     decoded Character OR null (if leaf node not reached)
   */
  abstract Character decode(CharArrayIterator bits);
  
  //=============================\\
  // TREE REPRESENTATION METHODS \\
  //=============================\\
  
  /**
   * Recursively builds bit String representation of sub-tree rooted at this
   * node, working its way down tree in pre-order.
   * 
   * Helper function for bitRep().
   * 
   * @param sb bit representation to append to (encoding nodes down to this
   *           node in tree)
   */
  abstract void buildBitRep(StringBuilder sb);
  
  /**
   * Recursively generates Huffman sub-tree from bit char array iterator.
   * Used for predefined tree loading.
   * 
   * Postcondition: Bit char array iterator will be advanced past the 
   * bits encoding this sub-tree.
   * 
   * @param bits the iterator, advanced to the next bits
   *             to use to generate Huffman Tree nodes
   * @return     the root of this sub-tree.
   */
  static HuffmanNode loadNode(CharArrayIterator bits) {
    // '0' represents parent node
    if (bits.next() == '0') {
      // Create parent with children based on next bits.
      return new HuffmanParent(loadNode(bits), loadNode(bits));
    }
    // '1' represents leaf node
    else {
      // Read in 8 bits left to right for char code.
      //   0  0  0  0    0 0 0 0
      // 128 64 32 16    8 4 2 1
      char c = 0;
      for (int bitValue = 128; bitValue > 0; bitValue /= 2) {
        // Convert each bit char into a real 0 or 1 before multiplying.
        c += bitValue * (bits.next() - '0');
      }
      
      // Create leaf with read character.
      // Frequency is irrelevant when tree is pre-constructed.
      return new HuffmanLeaf(c, 0);
    }
  }
  
  /**
   * Displays this section of tree, using prefix to indent tree appropriately.
   * 
   * Recursive worker function for HuffmanTree.display().
   * 
   * @param prefix contents to put before any children. May be whitespace or
   *               0s and 1s, but it should match the spacing already printed
   *               before this function call, so that both children are
   *               displayed with the same indentation.
   */
  abstract void display(StringBuilder prefix);
}
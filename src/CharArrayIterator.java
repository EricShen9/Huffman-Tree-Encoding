import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over a char[]. Standard iterators don't work on primitive
 *   types, but we can build our own.
 */
class CharArrayIterator implements Iterator<Character> {
  /**
   * Index of next element to iterate through.
   */
  private int i = 0;
  /**
   * Array to iterate through.
   */
  private char[] a;
  
  /**
   * Sole constructor. Constructs a new iterator over the specified array.
   * @param chars the char array to iterate over
   */
  CharArrayIterator(char[] chars) {
    a = chars;
  }

  /**
   * Returns the next available char.
   * @return true if the iteration has more elements
   */
  public boolean hasNext() {
    return i < a.length;
  }
  
  /**
   * Returns the next available char.
   * 
   * @return the next element in the iteration
   * @throws NoSuchElementException - if iteration has no more elements
   */
  public Character next() {
    // Crash with correct Exception when out of elements.
    if (i >= a.length) {
      throw new NoSuchElementException("Only " + a.length + "elements.");
      // Exceptions can contain messages.
      
      // You can throw any class that extends Throwable, including
      // your own. Try to extend the most applicable existing
      // Exception class if making your own.
      
      // Class hierarchy in this case:
      // NoSuchElementException extends
      //       RuntimeException extends
      //              Exception extends
      //              Throwable extends Object
    }
    
    return a[i++];
    // Equivalent to:
    // char c = a[i];
    // i++;
    // return new Character(c);
  }
}
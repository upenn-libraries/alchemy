package edu.upenn.library.elements;

public class Util {

  /**
   * Pad an object array by prepending a number of values to it.
   * Returns a new array.
   */
  public static Object[] prepad(Object[] o, int count, Object value) {
    Object[] newO = new Object[o.length + count];
    System.arraycopy(o, 0, newO, count, o.length);
    for(int i = 0 ; i< count; i++) {
      newO[i] = value;
    }
    return newO;
  }

}

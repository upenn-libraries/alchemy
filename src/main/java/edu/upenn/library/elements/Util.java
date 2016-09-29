package edu.upenn.library.elements;

import java.io.Console;
import org.jdom2.Element;

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

  public static String readPassword(String prompt) throws Exception {
    Console c = System.console();
    if (c == null) {
      throw new Exception("no console available for reading password");
    }
    return new String(c.readPassword(prompt));
  }

  public static Element getChildIgnoreNS(Element element, String name) {
    return element.getChildren().stream().filter(child -> name.equals(child.getName())).findFirst().orElse(null);
  }

}

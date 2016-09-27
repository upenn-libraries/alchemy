package edu.upenn.library.elements.csv;

/**
 * This exists to make it possible to define fields
 * to map to a header column using 'key', while displaying
 * a different value for header.
 */
public class Field {

  private String key;
  private String display;

  public Field(String key) {
    this(key, key);
  }

  public Field(String key, String display) {
    this.key = key;
    this.display = display;
  }

  public String getKey() {
    return key;
  }

  public String getDisplay() {
    return display;
  }

}

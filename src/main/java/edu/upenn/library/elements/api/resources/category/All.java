package edu.upenn.library.elements.api.resources.category;

public class All extends CategoryResource {

  public All(Category category) {
    super(category);
  }

  public String getPath() {
    return "/" + getCategory().plural;
  }

  public String getAtomEntryElement() {
    return "object";
  }

}

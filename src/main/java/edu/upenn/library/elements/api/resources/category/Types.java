package edu.upenn.library.elements.api.resources.category;

public class Types extends CategoryResource {

  public Types(Category category) {
    super(category);
  }

  public String getPath() {
    return "/" + getCategory().singular + "/types";
  }

  public String getAtomEntryElement() {
    return "type";
  }

}

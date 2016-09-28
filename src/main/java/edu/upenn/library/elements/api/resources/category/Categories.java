package edu.upenn.library.elements.api.resources.category;

public class Categories extends CategoryResource {

  public Categories(Category category) {
    super(category);
  }

  public String getPath() {
    return "/" + getCategory().plural;
  }

  public String getAtomEntryElement() {
    return "object";
  }

}

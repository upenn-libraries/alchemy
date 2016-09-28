package edu.upenn.library.elements.api.resources.category;

public class CategoriesTypes extends CategoryResource {

  public CategoriesTypes(Category category) {
    super(category);
  }

  public String getPath() {
    return "/" + getCategory().singular + "/types";
  }

  public String getAtomEntryElement() {
    return "type";
  }

}

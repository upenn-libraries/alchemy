package edu.upenn.library.elements.api.resources.category;

public class Sources extends CategoryResource {

  public Sources(Category category) {
    super(category);
  }

  public String getPath() {
    return "/" + getCategory().singular + "/sources";
  }

  public String getAtomEntryElement() {
    return "data-source";
  }

}

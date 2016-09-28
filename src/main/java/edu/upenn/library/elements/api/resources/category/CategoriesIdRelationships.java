package edu.upenn.library.elements.api.resources.category;

public class CategoriesIdRelationships extends CategoryResource {

  private final String id;

  public CategoriesIdRelationships(Category category, String id) {
    super(category);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return "/" + getCategory().plural + "/" + getId() + "/relationships";
  }

  public String getAtomEntryElement() {
    return "relationship";
  }

}

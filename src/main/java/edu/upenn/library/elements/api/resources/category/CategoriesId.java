package edu.upenn.library.elements.api.resources.category;

public class CategoriesId extends CategoryResource {

  private final String id;

  public CategoriesId(Category category, String id) {
    super(category);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return "/" + getCategory().plural + "/" + getId();
  }

  public String getAtomEntryElement() {
    return "object";
  }

}

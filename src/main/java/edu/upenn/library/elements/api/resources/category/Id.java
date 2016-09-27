package edu.upenn.library.elements.api.resources.category;

public class Id extends CategoryResource {

  private final String id;

  public Id(Category category, String id) {
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

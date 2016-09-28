package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

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

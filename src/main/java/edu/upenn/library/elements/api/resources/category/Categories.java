package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

/**
 * Resource: /{cats}
 */
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

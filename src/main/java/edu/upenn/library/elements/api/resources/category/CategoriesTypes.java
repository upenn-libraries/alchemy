package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

/**
 * Resource: /{cats}/types
 */
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

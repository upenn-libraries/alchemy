package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

public class CategoriesSources extends CategoryResource {

  public CategoriesSources(Category category) {
    super(category);
  }

  public String getPath() {
    return "/" + getCategory().singular + "/sources";
  }

  public String getAtomEntryElement() {
    return "data-source";
  }

}

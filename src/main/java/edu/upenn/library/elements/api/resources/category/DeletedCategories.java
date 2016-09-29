package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

/**
 * Resource: /deleted/{cats}
 */
public class DeletedCategories extends CategoryResource {

  public DeletedCategories(Category category) {
    super(category);
  }

  @Override
  public String getPath() {
    return "/deleted/" + getCategory().plural;
  }

  @Override
  public String getAtomEntryElement() {
    return "deleted-object";
  }
}

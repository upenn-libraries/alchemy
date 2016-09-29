package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

/**
 * Resource: /deleted/{cats}/{id}
 */
public class DeletedCategoriesId extends CategoryResource {

  private final String id;

  public DeletedCategoriesId(Category category, String id) {
    super(category);
    this.id = id;
  }

  @Override
  public String getPath() {
    return "/deleted/" + getCategory().plural + "/" + id;
  }

  @Override
  public String getAtomEntryElement() {
    return "deleted-object";
  }
}

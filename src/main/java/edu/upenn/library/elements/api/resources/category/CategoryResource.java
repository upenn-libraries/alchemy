package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.resources.Resource;

/**
 * Abstract base class for all API resources involving categories
 */
public abstract class CategoryResource extends Resource {

  private Category category;

  public CategoryResource(Category category) {
    this.category = category;
  }

  public Category getCategory() {
    return category;
  }

  public abstract String getPath();

}

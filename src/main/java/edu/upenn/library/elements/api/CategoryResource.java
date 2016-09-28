package edu.upenn.library.elements.api;

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

package edu.upenn.library.elements.api.resources.category;

public class CategoriesIdCategories extends CategoryResource {

  private final String id;
  private final Category category2;

  public CategoriesIdCategories(Category category, String id, Category category2) {
    super(category);
    this.id = id;
    this.category2 = category2;
  }

  public String getId() {
    return id;
  }

  public Category getCategory2() {
    return category2;
  }

  public String getPath() {
    return "/" + getCategory().plural + "/" + getId() + "/" + getCategory2().plural;
  }

  public String getAtomEntryElement() {
    return "relationship";
  }

}

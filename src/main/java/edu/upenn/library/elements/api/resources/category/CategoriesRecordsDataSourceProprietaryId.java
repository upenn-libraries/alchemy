package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;

/**
 * Resource: /{cat}/records/{data-source}/{proprietary-id}
 */
public class CategoriesRecordsDataSourceProprietaryId extends CategoryResource {

  private String dataSource;
  private String proprietaryId;

  public CategoriesRecordsDataSourceProprietaryId(Category category, String dataSource, String proprietaryId) {
    super(category);
    this.dataSource = dataSource;
    this.proprietaryId = proprietaryId;
  }

  @Override
  public String getPath() {
    return "/" + getCategory().singular + "/records/" + dataSource + "/" + proprietaryId;
  }

  @Override
  public String getAtomEntryElement() {
    return "object";
  }
}

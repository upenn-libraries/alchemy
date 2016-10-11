package edu.upenn.library.elements.api.resources.category;

import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.CategoryResource;
import edu.upenn.library.elements.api.Param;

/**
 * Resource: /{cats}
 */
public class Categories extends CategoryResource {

  public static final Param PARAM_QUERY = new Param("query");
  public static final Param PARAM_AUTHORITY = new Param("authority");
  public static final Param PARAM_USERNAME = new Param("username");
  public static final Param PARAM_PROPRIETARY_ID = new Param("proprietary-id");
  public static final Param PARAM_RA_UNIT_ID = new Param("ra-unit-id");
  public static final Param PARAM_GROUPS = new Param("groups");
  public static final Param PARAM_EVER_APPROVED = new Param("ever-approved");
  public static final Param PARAM_CREATED_SINCE = new Param("created-since");
  public static final Param PARAM_MODIFIED_SINCE = new Param("modified-since");

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

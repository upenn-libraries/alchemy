package edu.upenn.library.elements.api;

import java.util.Arrays;

public enum Category {

  ACTIVITY("activity", "activities"),
  EQUIPMENT("equipment", "equipment"),
  GRANT,
  ORG_STRUCTURE("org-structure"),
  PROJECT,
  PUBLICATION,
  USER("user", "Users"), // per API docs, plural is capitalized for some reason
  TEACHING_ACTIVITY("teaching-activity", "teaching-activities");

  public final String singular;
  public final String plural;

  /**
   * Constructor where singular and plurals are based on Enum name.
   */
  Category() {
    this.singular = name().toLowerCase();
    this.plural = singular + "s";
  }

  /**
   * Constructor where plural is just the singular + s
   */
  Category(String singular) {
    this(singular, singular + "s");
  }

  /**
   * Constructor for explicit singular and plural
   */
  Category(String singular, String plural) {
    this.singular = singular;
    this.plural = plural;
  }

  /**
   * Find the category by name that matches either singular or plural, ignoring case.
   * @param name
   * @return
   */
  public static Category getByName(String name) {
    String nameLowered = name.toLowerCase();
    return Arrays.stream(values()).filter(c -> {
      return nameLowered.equals(c.singular.toLowerCase())
        || nameLowered.equals(c.plural.toLowerCase());
    }).findFirst().orElse(null);
  }

}

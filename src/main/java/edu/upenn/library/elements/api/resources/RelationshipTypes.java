package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /relationship/types
 */
public class RelationshipTypes extends Resource {

  @Override
  public String getPath() {
    // note that 4.6 API documentation is wrong, it's not "relationship-types"
    return "/relationship/types";
  }

  @Override
  public String getAtomEntryElement() {
    return "relationship-type";
  }
}

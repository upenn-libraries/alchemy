package edu.upenn.library.elements.api.resources.other;

import edu.upenn.library.elements.api.resources.Resource;

/**
 * TODO: this doesn't seem to be a valid endpoint?!
 */
public class RelationshipTypes extends Resource {

  @Override
  public String getPath() {
    return "/relationship-types";
  }

  @Override
  public String getAtomEntryElement() {
    return "relationship-type";
  }
}

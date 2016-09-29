package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /relationships
 */
public class Relationships extends Resource {

  @Override
  public String getPath() {
    return "/relationships";
  }

  @Override
  public String getAtomEntryElement() {
    return "relationship";
  }

}

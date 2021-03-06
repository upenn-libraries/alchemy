package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /groups
 */
public class Groups extends Resource {

  @Override
  public String getPath() {
    return "/groups";
  }

  @Override
  public String getAtomEntryElement() {
    return "user-group";
  }

}

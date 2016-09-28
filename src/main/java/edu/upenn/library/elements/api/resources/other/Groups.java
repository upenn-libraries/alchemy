package edu.upenn.library.elements.api.resources.other;

import edu.upenn.library.elements.api.resources.Resource;

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

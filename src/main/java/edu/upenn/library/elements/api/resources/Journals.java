package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

public class Journals extends Resource {

  @Override
  public String getPath() {
    return "/journals";
  }

  @Override
  public String getAtomEntryElement() {
    return "journal";
  }

}

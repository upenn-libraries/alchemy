package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

public class UserFeed extends Resource {

  @Override
  public String getPath() {
    return "/user-feed";
  }

  @Override
  public String getAtomEntryElement() {
    return "user-feed-entry";
  }

}

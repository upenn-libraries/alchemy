package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /my-account
 */
public class MyAccount extends Resource {

  @Override
  public String getPath() {
    return "/my-account";
  }

  @Override
  public String getAtomEntryElement() {
    return "api-account";
  }

}

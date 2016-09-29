package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /relationships/{id}
 */
public class RelationshipsId extends Resource {

  private final String id;

  public RelationshipsId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return "/relationships/" + getId();
  }

  public String getAtomEntryElement() {
    return "relationship";
  }

}

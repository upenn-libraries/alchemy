package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /journal/sources
 */
public class JournalSources extends Resource {

  @Override
  public String getPath() {
    return "/journal/sources";
  }

  @Override
  public String getAtomEntryElement() {
    return "journal-source";
  }
}

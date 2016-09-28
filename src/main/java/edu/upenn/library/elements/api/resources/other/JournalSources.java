package edu.upenn.library.elements.api.resources.other;

import edu.upenn.library.elements.api.resources.Resource;

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

package edu.upenn.library.elements.api.resources;

import edu.upenn.library.elements.api.Resource;

/**
 * Resource: /journals/{issn}
 */
public class JournalIssn extends Resource {

  private final String issn;

  public JournalIssn(String issn) {
    this.issn = issn;
  }

  public String getIssn() {
    return issn;
  }

  public String getPath() {
    return "/journals/" + getIssn();
  }

  public String getAtomEntryElement() {
    return "journal";
  }

}

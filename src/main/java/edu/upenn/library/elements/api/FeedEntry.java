package edu.upenn.library.elements.api;

import com.rometools.rome.feed.synd.SyndEntry;
import org.jdom2.Element;

public class FeedEntry {

  private SyndEntry entry;
  private Element elementsContent;

  public FeedEntry(SyndEntry entry, Element elementsContent) {
    this.entry = entry;
    this.elementsContent = elementsContent;
  }

  /**
   * Returns the standard ATOM entry element
   * @return
   */
  public SyndEntry getEntry() {
    return entry;
  }

  /**
   * Returns the element, embedded within ATOM entry,
   * that contains the Elements data.
   * @return
   */
  public Element getElementsContent() {
    return elementsContent;
  }
}

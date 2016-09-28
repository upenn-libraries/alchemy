package edu.upenn.library.elements.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 * Wrapper around SyndFeed to make extracting data easier
 */
public class Feed {

  private SyndFeed syndFeed;
  private Resource resource;

  public Feed(SyndFeed syndFeed, Resource resource) {
    this.syndFeed = syndFeed;
    this.resource = resource;
  }

  public List<FeedEntry> getEntries() {
    ArrayList<FeedEntry> results = new ArrayList<>();
    for(SyndEntry entry : syndFeed.getEntries()) {
      results.addAll(entry.getForeignMarkup().stream()
              .filter(fe -> fe.getName().equals(resource.getAtomEntryElement()))
              .map(fe -> new FeedEntry(entry, fe))
              .collect(Collectors.toList()));
    }
    return results;
  }

}

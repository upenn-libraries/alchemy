package edu.upenn.library.elements.tasks;

import java.util.Iterator;
import edu.upenn.library.elements.Task;
import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.Feed;
import edu.upenn.library.elements.api.FeedEntry;
import edu.upenn.library.elements.api.Pagination;
import edu.upenn.library.elements.api.resources.category.Categories;

public class SamplePublicationSearch extends Task {

  @Override
  public String getDescription() {
    return "Sample task illustrating iteration over multiple feeds";
  }

  @Override
  public String getHelp() {
    return "Usage: SamplePublicationSearch SEARCH_TERM\n" +
      "\n" +
      getDescription() + "\n" +
      "\n" +
      "This tasks exists only to illustrate what the code looks like to\n" +
      "iterate over a set of publication search results over several pages\n" +
      "of feeds\n";
  }

  @Override
  public void execute() throws Exception {
    if (getArgs().size() < 1) {
      getLogger().error("You must specify a search term.");
      return;
    }
    String searchTerm = getArgs().get(0);

    Categories categories = new Categories(Category.PUBLICATION);
    categories.setParam(Categories.PARAM_QUERY, searchTerm);

    Feed feed = getApi().getFeed(categories);
    Iterator<FeedEntry> feedEntries = getApi().getFeedEntriesIterator(feed);

    Pagination pagination = feed.getPagination();
    if(pagination != null) {
      System.out.println("Iterating through " + pagination.getResultsCount() + " results, over " + pagination.getLastPageNum() + " pages of feeds");
    }

    while(feedEntries.hasNext()) {
      FeedEntry feedEntry = feedEntries.next();
      System.out.println(feedEntry.getElementsContent().getAttribute("id").getValue()
        + " - " + feedEntry.getEntry().getTitle());
    }
  }
}

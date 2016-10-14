package edu.upenn.library.elements.api;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the pagination information found in the "pagination" element subtree
 * in API responses
 */
public class Pagination {

  private final Logger logger = LoggerFactory.getLogger(Pagination.class);

  private long resultsCount;
  private long itemsPerPage;

  private long thisPageNum;
  private String thisPageURL;
  private long firstPageNum;
  private String firstPageURL;
  private long nextPageNum;
  private String nextPageURL;
  private long lastPageNum;
  private String lastPageURL;

  public Pagination(Element pagination) {
    try {
      resultsCount = pagination.getAttribute("results-count").getLongValue();
      itemsPerPage = pagination.getAttribute("items-per-page").getLongValue();
      for(Element page : pagination.getChildren()) {
        String pos = page.getAttribute("position").getValue();
        long number = page.getAttribute("number").getLongValue();
        String href = page.getAttribute("href").getValue();
        switch(pos) {
          case "this":
            thisPageNum = number;
            thisPageURL = href;
            break;
          case "first":
            firstPageNum = number;
            firstPageURL = href;
            break;
          case "next":
            nextPageNum = number;
            nextPageURL = href;
            break;
          case "last":
            lastPageNum = number;
            lastPageURL = href;
            break;
        }
      }
    } catch(DataConversionException e) {
      logger.error("Conversion error when parsing pagination info from XML: " + e.toString());
    }
  }

  public long getResultsCount() {
    return resultsCount;
  }

  public long getItemsPerPage() {
    return itemsPerPage;
  }

  public long getThisPageNum() {
    return thisPageNum;
  }

  public String getThisPageURL() {
    return thisPageURL;
  }

  public long getFirstPageNum() {
    return firstPageNum;
  }

  public String getFirstPageURL() {
    return firstPageURL;
  }

  public long getNextPageNum() {
    return nextPageNum;
  }

  public String getNextPageURL() {
    return nextPageURL;
  }

  public long getLastPageNum() {
    return lastPageNum;
  }

  public String getLastPageURL() {
    return lastPageURL;
  }
}

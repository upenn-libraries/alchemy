package edu.upenn.library.elements.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import edu.upenn.library.elements.api.xml.XMLDocument;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles executing API operations on various resources.
 */
public class Api {

  public static final String CONTENT_TYPE_TEXT_XML = "text/xml";

  private final Logger logger = LoggerFactory.getLogger(Api.class);

  private String baseUrl;
  private CloseableHttpClient httpClient;

  public Api(String baseUrl, String username, String password)
    throws MalformedURLException {
    this(baseUrl, username, password, false);
  }

  /**
   *
   * @param baseUrl the API endpoint as defined in Elements
   * @param ignoreCertMismatch set to true when running over ssh tunnel
   */
  public Api(String baseUrl, String username, String password, boolean ignoreCertMismatch)
    throws MalformedURLException {

    this.baseUrl = baseUrl;

    URL url = new URL(baseUrl);

    if ("https".equals(url.getProtocol())) {
      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(
              new AuthScope(url.getHost(), AuthScope.ANY_PORT),
              new UsernamePasswordCredentials(username, password));

      HttpClientBuilder builder = HttpClients.custom()
        .setDefaultCredentialsProvider(credsProvider);

      if(ignoreCertMismatch) {
        builder = builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
      }

      this.httpClient = builder.build();
    } else {
      this.httpClient = HttpClients.createDefault();
    }

  }

  public String constructURL(Resource resource) {
    StringBuilder url = new StringBuilder();
    url.append(baseUrl);
    url.append(resource.getPath());

    StringBuilder params = new StringBuilder();
    for(Map.Entry<String, List<String>> paramEntry : resource.getParams().entrySet()) {
      if(params.length() != 0) {
        params.append("&");
      }
      for(String value : paramEntry.getValue()) {
        params.append(paramEntry.getKey());
        params.append("=");
        params.append(value);
      }
    }

    if(params.length() > 0) {
      url.append("?");
      url.append(params);
    }

    return url.toString();
  }

  public String constructURL(String path) {
    StringBuilder url = new StringBuilder();
    url.append(baseUrl);
    url.append(path);
    return url.toString();
  }

  protected Feed constructFeed(Resource resource, CloseableHttpResponse response)
    throws FeedException, IOException {
    InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());

    SyndFeedInput input = new SyndFeedInput();
    SyndFeed syndFeed = input.build(isr);

    Feed feed = new Feed(syndFeed, resource);

    isr.close();
    response.close();

    return feed;
  }

  /**
   * Makes a GET request for a resource, which should return an Atom feed.
   * @param resource
   * @return
   * @throws Exception
   */
  public Feed getFeed(Resource resource) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making GET request: " + url);

    CloseableHttpResponse response = doGet(url);

    if(!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
      throw new Exception("Error making HTTP request: " + response.getStatusLine().toString());
    }

    return constructFeed(resource, response);
  }

  class FeedIterator implements Iterator<Feed> {
    private boolean initialFeed = false;
    private Feed feed;
    private Resource resource;

    public FeedIterator(Resource resource) {
      this.resource = resource;
    }

    /**
     * Constructor that uses passed-in Feed as first one to iterate through
     * @param feed
     */
    public FeedIterator(Feed feed) {
      this.feed = feed;
      this.initialFeed = true;
    }

    @Override
    public boolean hasNext() {
      if (!initialFeed && feed != null) {
        Pagination pagination = feed.getPagination();
        return pagination.getThisPageNum() < pagination.getNextPageNum();
      }
      return true;
    }

    @Override
    public Feed next() {
      if(initialFeed) {
        // just return it
        initialFeed = false;
      } else if(feed == null) {
        try {
          feed = getFeed(resource);
        } catch (Exception e) {
          logger.error("error in getFeed() in FeedIterator.hasNext(): " + e.toString());
        }
      } else {
        try {
          feed = getNextPage(feed);
        } catch(Exception e) {
          logger.error("error in getNextPage() in FeedIterator.hasNext(): " + e.toString());
        }
      }
      return feed;
    }
  }

  class FeedEntriesIterator implements Iterator<FeedEntry> {
    private Iterator<Feed> feedIter;
    private Iterator<FeedEntry> feedEntryIter = Collections.emptyIterator();

    /**
     * Constructor that sets up iterator to use passed-in Resource
     * @param resource
     */
    public FeedEntriesIterator(Resource resource) {
      this.feedIter = getFeedIterator(resource);
    }

    /**
     * Constructor that uses passed-in Feed as first one to iterate through
     * @param feed
     */
    public FeedEntriesIterator(Feed feed) {
      this.feedIter = getFeedIterator(feed);
    }

    @Override
    public boolean hasNext() {
      return feedEntryIter.hasNext() || feedIter.hasNext();
    }

    @Override
    public FeedEntry next() {
      if (!feedEntryIter.hasNext()) {
        if(feedIter.hasNext()) {
          Feed nextFeed = feedIter.next();
          feedEntryIter = nextFeed.getEntries().iterator();
        }
      }
      if(feedEntryIter.hasNext()) {
        return feedEntryIter.next();
      }
      throw new NoSuchElementException();
    }
  }

  /**
   * Returns an iterator of Feeds for a given Resource.
   * This is useful when paging through a set of results that spans across multiple Feeds,
   * and you are interested in data at the Feed level. If you only care about entries,
   * see getEntriesIterator.
   * @param resource
   * @return
   */
  public Iterator<Feed> getFeedIterator(Resource resource) {
    return new FeedIterator(resource);
  }

  public Iterator<Feed> getFeedIterator(Feed feed) {
    return new FeedIterator(feed);
  }

  /**
   * Returns an iterator of FeedEntry objects for a given Resource.
   * This is useful when paging through a set of results that spans across multiple Feeds,
   * and you are interested in data at the Entry level.
   * @param resource
   * @return
   */
  public Iterator<FeedEntry> getFeedEntriesIterator(Resource resource) {
    return new FeedEntriesIterator(resource);
  }

  /**
   * Returns an iterator of FeedEntry objects, using Feed as the initial feed.
   * @param feed
   * @return
   */
  public Iterator<FeedEntry> getFeedEntriesIterator(Feed feed) {
    return new FeedEntriesIterator(feed);
  }

  /**
   * Gets the next page of a feed
   * @param feed
   * @return
   * @throws Exception
   */
  public Feed getNextPage(Feed feed) throws Exception {
    Resource resourceCopy = (Resource) feed.getResource().clone();
    Pagination pagination = feed.getPagination();
    long page = 0;
    if(pagination != null) {
      page = feed.getPagination().getNextPageNum();
    }
    if(page == 0) {
      return null;
    }
    resourceCopy.setParam("page", String.valueOf(page));
    return getFeed(resourceCopy);
  }

  public Feed postDocument(Resource resource, XMLDocument document) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making POST request: " + url);

    String payload = document.toString();

    CloseableHttpResponse response = doPost(url, CONTENT_TYPE_TEXT_XML, payload);

    if(!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
      throw new Exception("Error making HTTP request: " + response.getStatusLine().toString());
    }

    return constructFeed(resource, response);
  }

  public Feed putDocument(Resource resource, XMLDocument document) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making PUT request: " + url);

    String payload = document.toString();

    CloseableHttpResponse response = doPut(url, CONTENT_TYPE_TEXT_XML, payload);

    if(!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
      throw new Exception("Error making HTTP request: " + response.getStatusLine().toString());
    }

    return constructFeed(resource, response);
  }

  public Feed patchDocument(Resource resource, XMLDocument document) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making PATCH request: " + url);

    String payload = document.toString();

    CloseableHttpResponse response = doPatch(url, CONTENT_TYPE_TEXT_XML, payload);

    if(!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
      throw new Exception("Error making HTTP request: " + response.getStatusLine().toString());
    }

    return constructFeed(resource, response);
  }

  public void delete(Resource resource) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making DELETE request: " + url);

    CloseableHttpResponse response = doDelete(url);

    if(!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
      throw new Exception("Error making HTTP request: " + response.getStatusLine().toString());
    }
    // DELETE requests never return anything in Elements API.
  }

  public CloseableHttpResponse doGet(String url) throws IOException {
    HttpGet httpget = new HttpGet(url);
    return httpClient.execute(httpget);
  }

  public CloseableHttpResponse doPut(String url, String contentType, String payload) throws IOException {
    HttpPut httpPut = new HttpPut(url);
    httpPut.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
    HttpEntity entity = new ByteArrayEntity(payload.getBytes("UTF-8"));
    httpPut.setEntity(entity);
    return httpClient.execute(httpPut);
  }

  public CloseableHttpResponse doPatch(String url, String contentType, String payload) throws IOException {
    HttpPatch httpPatch = new HttpPatch(url);
    httpPatch.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
    HttpEntity entity = new ByteArrayEntity(payload.getBytes("UTF-8"));
    httpPatch.setEntity(entity);
    return httpClient.execute(httpPatch);
  }

  public CloseableHttpResponse doPost(String url, String contentType, String payload) throws IOException {
    HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
    HttpEntity entity = new ByteArrayEntity(payload.getBytes("UTF-8"));
    httpPost.setEntity(entity);
    return httpClient.execute(httpPost);
  }

  public CloseableHttpResponse doDelete(String url) throws IOException {
    HttpDelete httpDelete = new HttpDelete(url);
    return httpClient.execute(httpDelete);
  }

}

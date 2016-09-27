package edu.upenn.library.elements.api;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import edu.upenn.library.elements.api.resources.Feed;
import edu.upenn.library.elements.api.resources.Resource;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles executing API operations on various resources.
 */
public class Api {

  private final Logger logger = LoggerFactory.getLogger(Api.class);

  private String baseUrl;
  private CloseableHttpClient httpClient;

  public Api(String baseUrl, String username, String password) {
    this(baseUrl, username, password, false);
  }

  /**
   *
   * @param baseUrl the API endpoint as defined in Elements
   * @param ignoreCertMismatch set to true when running over ssh tunnel
   */
  public Api(String baseUrl, String username, String password, boolean ignoreCertMismatch) {

    this.baseUrl = baseUrl;

    if (ignoreCertMismatch) {
      URL url = null;
      try {
        url = new URL(baseUrl);
      } catch(MalformedURLException e) {
        logger.error("Invalid URL, couldn't extract hostname from it: " + baseUrl);
      }

      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(
              new AuthScope(url.getHost(), AuthScope.ANY_PORT),
              new UsernamePasswordCredentials(username, password));

      this.httpClient = HttpClients.custom()
              .setSSLHostnameVerifier(new NoopHostnameVerifier())
              .setDefaultCredentialsProvider(credsProvider)
              .build();
    } else {
      this.httpClient = HttpClients.createDefault();
    }

  }

  private String constructURL(Resource resource) {
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

  /**
   * Makes a GET request for a resource, which should return an Atom feed.
   * @param resource
   * @return
   * @throws Exception
   */
  public Feed getFeed(Resource resource) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making request: " + url);

    HttpGet httpget = new HttpGet(url);

    CloseableHttpResponse response = httpClient.execute(httpget);
    InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());

    SyndFeedInput input = new SyndFeedInput();
    SyndFeed syndFeed = input.build(isr);

    Feed feed = new Feed(syndFeed, resource);

    isr.close();
    response.close();

    return feed;
  }

  // TODO: create methods for handling destructive operations: POST, PUT, DELETE

}

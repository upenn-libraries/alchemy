package edu.upenn.library.elements.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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

  public void delete(Resource resource) throws Exception {
    String url = constructURL(resource);

    logger.debug("Making DELETE request: " + url);

    CloseableHttpResponse response = doDelete(url);

    if(!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
      throw new Exception("Error making HTTP request: " + response.getStatusLine().toString());
    }
    // DELETE requests never return anything in this API.
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

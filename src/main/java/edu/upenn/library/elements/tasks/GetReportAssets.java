package edu.upenn.library.elements.tasks;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import edu.upenn.library.elements.Task;
import edu.upenn.library.elements.Util;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetReportAssets extends Task {

  public static final String KEY_REPORTMANAGER_URL = "reportmanager.url";
  public static final String KEY_REPORTMANAGER_USERNAME = "reportmanager.username";
  public static final String KEY_REPORTMANAGER_PASSWORD = "reportmanager.password";
  public static final String KEY_REPORTMANAGER_PASSWORD_PROMPT = "reportmanager.password.prompt";

  @Override
  public String getDescription() {
    return "Fetch all reporting assets by scraping SQL Server Report Manager";
  }

  @Override
  public String getHelp() {
    return "Usage: GetReportAssets TARGET_DIR\n" +
      "\n" +
      getDescription() + "\n" +
      "\n" +
      "This task is useful for backups, and to facilitate committing report\n" +
      "assets into a version control system.\n";
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 1) {
      getLogger().error("You must specify a target directory.");
      return;
    }
    String targetDir = getArgs().get(0);

    String url = getTaskConfigKey(KEY_REPORTMANAGER_URL);
    String username = getTaskConfigKey(KEY_REPORTMANAGER_USERNAME);
    String password = getTaskConfigKey(KEY_REPORTMANAGER_PASSWORD);
    String passwordPrompt = getTaskConfigKey(KEY_REPORTMANAGER_PASSWORD_PROMPT, "false");

    if(Boolean.valueOf(passwordPrompt)) {
      password = Util.readPassword("Report Manager password: ");
    }

    CloseableHttpClient httpClient =
      createClient(url, username, password, false);

    scrape(url, url, httpClient, targetDir);
  }

  public void scrape(String url, String reportManagerUrl, CloseableHttpClient httpClient, String targetDir) throws Exception {
    HttpGet httpget = new HttpGet(url);
    CloseableHttpResponse response = httpClient.execute(httpget);

    String baseUrl = getBaseUrl(new URL(url));

    Document doc = Jsoup.parse(response.getEntity().getContent(), "UTF-8", url);
    List<Element> links = extractContentLinks(doc);
    for(Element link : filterFileLinks(links)) {
      String gotoUrl = baseUrl + link.attr("href");
      String itemPath = extractItemPath(new URL(gotoUrl));
      getLogger().info("Fetching " + itemPath);
      fetchFile(targetDir, httpClient, reportManagerUrl, link);
    }
    for(Element link : filterFolderLinks(links)) {
      String gotoUrl = baseUrl + link.attr("href");
      String itemPath = extractItemPath(new URL(gotoUrl));

      File targetFile = new File(targetDir, itemPath);
      if(!targetFile.getParentFile().exists()) {
        getLogger().info("Creating directory " + itemPath);
        targetFile.getParentFile().mkdirs();
      }
      scrape(gotoUrl, reportManagerUrl, httpClient, targetDir);
    }
  }

  public static String getBaseUrl(URL url) {
    String baseUrl = url.getProtocol() + "://" + url.getHost();
    if(url.getPort() != -1 && url.getPort() != 80) {
      baseUrl += ":" + url.getPort();
    }
    return baseUrl;
  }

  public static CloseableHttpClient createClient(String baseUrl, String username, String password, boolean ignoreCertMismatch)
    throws MalformedURLException {

    CloseableHttpClient httpClient;
    URL url = new URL(baseUrl);

    if ("https".equals(url.getProtocol())) {
      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(AuthScope.ANY,
        new NTCredentials(username, password, "", "LIBRARY"));

      RequestConfig config = RequestConfig.custom().setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM)).build();

      HttpClientBuilder builder = HttpClients.custom()
        .setDefaultCredentialsProvider(credsProvider)
        .setDefaultRequestConfig(config);

      if(ignoreCertMismatch) {
        builder = builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
      }

      httpClient = builder.build();
    } else {
      httpClient = HttpClients.createDefault();
    }
    return httpClient;
  }

  public static List<Element> extractContentLinks(Document doc) throws Exception {
    Elements elements = doc.select("a");

    Element start = elements.stream().filter(e -> "Details View".equals(e.text()))
      .findFirst().orElse(null);
    Element end = elements.stream().filter(e -> "Move".equals(e.text()))
      .findFirst().orElse(null);

    if(start == null) {
      throw new Exception("Couldn't find 'Details View' link on the page");
    }

    int startIdx = elements.indexOf(start) + 1;
    int endIdx;
    if(end != null) {
      endIdx = elements.indexOf(end);
    } else {
      endIdx = elements.size();
    }

    return elements.subList(startIdx, endIdx).stream()
      .filter(e -> e.text() != null && e.text().length() > 0)
      .collect(Collectors.toList());
  }

  public static List<Element> filterFileLinks(List<Element> elements) {
    return elements.stream().filter(e -> {
      String href = e.attr("href");
      return href.contains("/DataSet.aspx") || href.contains("/Report.aspx") || href.contains("/Resource.aspx");
    }).collect(Collectors.toList());
  }

  public static List<Element> filterFolderLinks(List<Element> elements) {
    return elements.stream().filter(e -> {
      String href = e.attr("href");
      return href.contains("/Folder.aspx");
    }).collect(Collectors.toList());
  }

  public static String extractItemPath(URL url) {
    List<NameValuePair> pairs = URLEncodedUtils.parse(url.getQuery(), Charset.forName("UTF-8"));
    return pairs.stream()
      .filter(pair -> "ItemPath".equals(pair.getName()))
      .map(pair -> pair.getValue())
      .findFirst().orElse(null);
  }

  /**
   * Fetches the file (can be a report, dataset) represented by the link object, and writes it out to disk
   */
  public static void fetchFile(String targetDir, CloseableHttpClient httpClient, String reportManagerUrl, Element link) throws Exception {
    String url = getBaseUrl(new URL(reportManagerUrl)) + link.attr("href");
    URL urlObj = new URL(url);
    String itemPath = extractItemPath(urlObj);

    String downloadUrl = null;
    File targetFile = null;

    if(url.contains("Report.aspx")) {
      downloadUrl = String.format(
        "%s/Pages/Report.aspx?ItemPath=%s&SelectedTabId=PropertiesTab&SelectedSubTabId=GenericPropertiesTab&Export=true",
        reportManagerUrl,
        URLEncoder.encode(itemPath, "UTF-8"));
      targetFile = new File(targetDir, itemPath + ".rdl");
    } else if(url.contains("DataSet.aspx")) {
      downloadUrl = String.format(
        "%s/Pages/DataSet.aspx?ItemPath=%s&Export=true",
        reportManagerUrl,
        URLEncoder.encode(itemPath, "UTF-8"));
      targetFile = new File(targetDir, itemPath + ".rsd");
    } else if(url.contains("Resource.aspx")) {
      downloadUrl = String.format(
        "%s/Pages/Resource.aspx?ItemPath=%s&Export=true&ViewMode=Detail",
        reportManagerUrl,
        URLEncoder.encode(itemPath, "UTF-8"));
      targetFile = new File(targetDir, itemPath);
    }

    if(downloadUrl != null) {
      HttpGet httpget = new HttpGet(downloadUrl);
      CloseableHttpResponse response = httpClient.execute(httpget);

      if(!targetFile.getParentFile().exists()) {
        targetFile.getParentFile().mkdirs();
      }

      Files.copy(response.getEntity().getContent(), targetFile.toPath(), REPLACE_EXISTING);
    }
  }

}

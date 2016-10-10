package edu.upenn.library.elements.tasks;

import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import edu.upenn.library.elements.Task;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Basically curl.
 */
public class Dump extends Task {

  private static final int BUF_SIZE = 10240;

  @Override
  public String getDescription() {
    return "Pretty-print raw XML response from API endpoint (like curl)";
  }

  @Override
  public String getHelp() {
    return "Usage: Dump PATH [output file]\n" +
      "\n" +
      "Make an API request at the specified URL path (e.g. '/publication/sources')\n" +
      "and pretty-print the raw response to stdout or output file (optional)\n";
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 1) {
      getLogger().error("You must specify a url path, and optionally, an output file");
      return;
    }
    String urlPath = getArgs().get(0);
    String filePath = null;
    if(getArgs().size() == 2) {
      filePath = getArgs().get(1);
    }

    CloseableHttpResponse response = getApi().doGet(getApi().constructURL(urlPath));
    InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());

    PrintStream out = null;
    if(filePath != null) {
      FileOutputStream os = new FileOutputStream(filePath);
      out = new PrintStream(os);
    } else {
      out = System.out;
    }

    SAXBuilder sax = new SAXBuilder();
    Document doc = sax.build(isr);

    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    outputter.output(doc, out);

    isr.close();
    out.flush();
  }
}

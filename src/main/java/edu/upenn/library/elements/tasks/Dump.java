package edu.upenn.library.elements.tasks;

import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basically curl.
 */
public class Dump extends Task {

  private final Logger logger = LoggerFactory.getLogger(Dump.class);

  @Override
  public String getDescription() {
    return "Usage: Dump PATH [output file]\n" +
      "\n" +
      "Make an API request at the specified URL path (e.g. '/publication/sources')\n" +
      "and write to stdout or output file (optional)\n";
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 2) {
      logger.error("You must specify a url path, and optionally, an output file");
      return;
    }
    String urlPath = getArgs().get(1);
    String filePath = null;
    if(getArgs().size() == 3) {
      filePath = getArgs().get(2);
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

    char[] buf = new char[10240];
    int read = 0;
    while(read != -1) {
      read = isr.read(buf);
      if(read != -1) {
        out.print(Arrays.copyOfRange(buf, 0, read));
      }
    }
    isr.close();
    out.flush();
  }
}

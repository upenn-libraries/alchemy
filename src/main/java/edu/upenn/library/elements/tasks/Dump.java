package edu.upenn.library.elements.tasks;

import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import edu.upenn.library.elements.Task;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Basically curl.
 */
public class Dump extends Task {

  private static final int BUF_SIZE = 10240;

  @Override
  public String getDescription() {
    return "Get raw response from API endpoint (like curl)";
  }

  @Override
  public String getHelp() {
    return "Usage: Dump PATH [output file]\n" +
      "\n" +
      "Make an API request at the specified URL path (e.g. '/publication/sources')\n" +
      "and write the raw response to stdout or output file (optional)\n";
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

    char[] buf = new char[BUF_SIZE];
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

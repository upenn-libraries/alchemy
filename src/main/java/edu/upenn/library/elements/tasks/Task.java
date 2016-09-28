package edu.upenn.library.elements.tasks;

import java.util.List;
import java.util.Map;
import edu.upenn.library.elements.Config;
import edu.upenn.library.elements.api.Api;

/**
 * Base class for writing your own custom Tasks to interact
 * with the Elements API. The app is responsible for initializing
 * your Task, populating it with some data (config, arguments supplied),
 * and creating an Api object for you to use. You do the rest.
 *
 * TODO: provide a JDBC connection object?
 */
public abstract class Task {

  private Config config;
  private Map<String, List<String>> options;
  private List<String> args;
  private Api api;

  public abstract String getDescription();

  public abstract String getHelp();

  public void init(Config config, Map<String, List<String>> options, List<String> args) throws Exception {
    this.config = config;
    this.options = options;
    this.args = args;
  }

  protected Api getApi() throws Exception {
    if(this.api == null) {
      String url = config.getProperty(Config.KEY_API_URL);
      Boolean ignoreCertMismatch = new Boolean(config.getProperty(Config.KEY_API_IGNORE_CERT_MISMATCH, "false"));
      this.api = new Api(url,
        config.getProperty(Config.KEY_API_USERNAME),
        config.getProperty(Config.KEY_API_PASSWORD),
        ignoreCertMismatch);
    }
    return api;
  }

  protected Config getConfig() {
    return config;
  }

  protected Map<String, List<String>> getOptions() {
    return options;
  }

  protected List<String> getArgs() {
    return args;
  }

  public abstract void execute() throws Exception;

}

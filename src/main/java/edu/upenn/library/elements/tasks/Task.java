package edu.upenn.library.elements.tasks;

import edu.upenn.library.elements.Config;
import edu.upenn.library.elements.api.Api;

/**
 * Base class for writing your own custom Tasks to interact
 * with the Elements API. The app is responsible for initializing
 * your Task, populating it with some data (config, arguments supplied),
 * and creating an Api object for you to use. You do the rest.
 */
public abstract class Task {

  private Config config;
  private String[] args;
  private Api api;

  public void init(Config config, String[] args) {
    this.config = config;
    this.args = args;
    createApi();
  }

  protected void createApi() {
    if(this.api == null) {
      String url = config.getProperty(Config.KEY_API_URL);
      Boolean ignoreCertMismatch = new Boolean(config.getProperty(Config.KEY_API_IGNORE_CERT_MISMATCH, "false"));
      this.api = new Api(url,
        config.getProperty(Config.KEY_API_USERNAME),
        config.getProperty(Config.KEY_API_PASSWORD),
        ignoreCertMismatch);
    }
  }

  protected Api getApi() {
    return api;
  }

  protected Config getConfig() {
    return config;
  }

  protected String[] getArgs() {
    return args;
  }

  public abstract void execute() throws Exception;

}

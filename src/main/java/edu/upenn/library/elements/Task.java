package edu.upenn.library.elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import edu.upenn.library.elements.api.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for writing your own custom Tasks to interact
 * with the Elements API. The app is responsible for initializing
 * your Task and populating it with some data (config,
 * arguments supplied). Your implementation of execute() can then
 * use the methods for accessing the API and the reporting database.
 */
public abstract class Task {

  private Config config;
  private Map<String, List<String>> options;
  private List<String> args;
  private Api api;
  private Connection connection;
  private Logger logger;

  public abstract String getDescription();

  public abstract String getHelp();

  public void init(Config config, Map<String, List<String>> options, List<String> args) throws Exception {
    this.config = config;
    this.options = options;
    this.args = args;
  }

  protected Logger getLogger() {
    if(logger == null) {
      logger = LoggerFactory.getLogger(this.getClass());
    }
    return logger;
  }

  protected Api getApi() throws Exception {
    if(this.api == null) {
      String password = config.getProperty(Config.KEY_API_PASSWORD);
      if(config.promptForApiPassword()) {
        password = Util.readPassword("API password: ");
      }

      String url = config.getProperty(Config.KEY_API_URL);
      this.api = new Api(url,
        config.getProperty(Config.KEY_API_USERNAME),
        password,
        config.ignoreCertMismatch());
    }
    return api;
  }

  protected Connection getDatabaseConnection() throws Exception {
    if(connection == null) {
      String password = config.getProperty(Config.KEY_DATABASE_REPORTING_PASSWORD);
      if(config.promptForDatabasePassword()) {
        password = Util.readPassword("Database password: ");
      }

      connection = DriverManager.getConnection(
        config.getProperty(Config.KEY_DATABASE_REPORTING_URL),
        config.getProperty(Config.KEY_DATABASE_REPORTING_USERNAME),
        password);
    }
    return connection;
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

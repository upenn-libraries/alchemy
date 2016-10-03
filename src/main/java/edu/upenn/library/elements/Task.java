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

  /**
   * @return the API environment specified in "e" option; if none, return "dev" by default
   */
  protected String getApiEnvironment() {
    List<String> e = getOptions().get("e");
    if(e != null && e.size() > 0) {
      return e.get(0);
    }
    return "dev";
  }

  /**
   * @return an API object
   * @throws Exception
   */
  protected Api getApi() throws Exception {
    return getApi(getApiEnvironment());
  }

  /**
   * @return an API object for the given name
   * @throws Exception
   */
  protected Api getApi(String apiName) throws Exception {
    if(this.api == null) {
      String password = config.getPropertyForApi(Config.KEY_API_PASSWORD, apiName);
      if(config.promptForApiPassword(apiName)) {
        password = Util.readPassword("API password: ");
      }

      String url = config.getPropertyForApi(Config.KEY_API_URL, apiName);
      this.api = new Api(url,
        config.getPropertyForApi(Config.KEY_API_USERNAME, apiName),
        password,
        config.ignoreCertMismatch(apiName));
    }
    return api;
  }

  /**
   * @return the database name specified in "d" option; if none, return "reporting" by default
   */
  protected String getDatabase() {
    List<String> e = getOptions().get("d");
    if(e != null && e.size() > 0) {
      return e.get(0);
    }
    return "reporting";
  }

  protected Connection getDatabaseConnection() throws Exception {
    return getDatabaseConnection(getDatabase());
  }

  protected Connection getDatabaseConnection(String databaseName) throws Exception {
    if(connection == null) {
      String password = config.getPropertyForDatabase(Config.KEY_DATABASE_PASSWORD, databaseName);
      if(config.promptForDatabasePassword(databaseName)) {
        password = Util.readPassword("Database password: ");
      }

      connection = DriverManager.getConnection(
        config.getPropertyForDatabase(Config.KEY_DATABASE_URL, databaseName),
        config.getPropertyForDatabase(Config.KEY_DATABASE_USERNAME, databaseName),
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

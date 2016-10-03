package edu.upenn.library.elements;

import java.util.Properties;

public class Config extends Properties {

  public static final String KEY_LOGLEVEL = "alchemy.loglevel";

  public static final String KEY_API_URL = "alchemy.api.%s.url";
  public static final String KEY_API_IGNORE_CERT_MISMATCH = "alchemy.api.%s.ignore_cert_mismatch";
  public static final String KEY_API_USERNAME = "alchemy.api.%s.username";
  public static final String KEY_API_PASSWORD = "alchemy.api.%s.password";
  public static final String KEY_API_PASSWORD_PROMPT = "alchemy.api.%s.password.prompt";

  public static final String KEY_DATABASE_URL = "alchemy.database.%s.url";
  public static final String KEY_DATABASE_USERNAME = "alchemy.database.%s.username";
  public static final String KEY_DATABASE_PASSWORD = "alchemy.database.%s.password";
  public static final String KEY_DATABASE_PROMPT = "alchemy.database.%s.password.prompt";

  /**
   * @param key one of the KEY_API constants defined above
   * @param apiName
   */
  public static final String getKeyForApi(String key, String apiName) {
    return String.format(key, apiName);
  }

  public String getPropertyForApi(String key, String apiName) {
    return getProperty(getKeyForApi(key, apiName));
  }

  public String getPropertyForApi(String key, String apiName, String defaultValue) {
    return getProperty(getKeyForApi(key, apiName), defaultValue);
  }

  public boolean ignoreCertMismatch(String apiName) {
    return new Boolean(getPropertyForApi(KEY_API_IGNORE_CERT_MISMATCH, apiName, "false"));
  }

  public boolean promptForApiPassword(String apiName) {
    return new Boolean(getProperty(getKeyForApi(KEY_API_PASSWORD_PROMPT, apiName), "false"));
  }

  /**
   * @param key one of the KEY_DATABASE constants defined above
   * @param databaseName
   */
  public static final String getKeyForDatabase(String key, String databaseName) {
    return String.format(key, databaseName);
  }

  public String getPropertyForDatabase(String key, String databaseName) {
    return getProperty(getKeyForDatabase(key, databaseName));
  }

  public String getPropertyForDatabase(String key, String databaseName, String defaultValue) {
    return getProperty(getKeyForDatabase(key, databaseName), defaultValue);
  }

  public boolean promptForDatabasePassword(String databaseName) {
    return new Boolean(getPropertyForDatabase(KEY_DATABASE_PROMPT, databaseName, "false"));
  }

}

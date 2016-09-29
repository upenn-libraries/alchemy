package edu.upenn.library.elements;

import java.util.Properties;

public class Config extends Properties {

  public static final String KEY_LOGLEVEL = "elements_tools.loglevel";

  public static final String KEY_API_URL = "elements_tools.api.url";
  public static final String KEY_API_IGNORE_CERT_MISMATCH = "elements_tools.api.ignore_cert_mismatch";
  public static final String KEY_API_USERNAME = "elements_tools.api.username";
  public static final String KEY_API_PASSWORD = "elements_tools.api.password";
  public static final String KEY_API_PASSWORD_PROMPT = "elements_tools.api.password.prompt";

  public static final String KEY_DATABASE_REPORTING_URL = "elements_tools.database.reporting.url";
  public static final String KEY_DATABASE_REPORTING_USERNAME = "elements_tools.database.reporting.username";
  public static final String KEY_DATABASE_REPORTING_PASSWORD = "elements_tools.database.reporting.password";
  public static final String KEY_DATABASE_REPORTING_PROMPT = "elements_tools.database.reporting.password.prompt";

  public boolean ignoreCertMismatch() {
    return new Boolean(getProperty(KEY_API_IGNORE_CERT_MISMATCH, "false"));
  }

  public boolean promptForApiPassword() {
    return new Boolean(getProperty(KEY_API_PASSWORD_PROMPT, "false"));
  }

  public boolean promptForDatabasePassword() {
    return new Boolean(getProperty(KEY_DATABASE_REPORTING_PROMPT, "false"));
  }

}

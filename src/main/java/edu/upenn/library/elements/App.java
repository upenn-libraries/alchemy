package edu.upenn.library.elements;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

public class App {

  public static CommandLine parseArgs(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("c", true, "config (.properties) file");

    CommandLineParser parser = new DefaultParser();
    return parser.parse(options, args);
  }

  protected Logger logger;

  public App(String[] args) {
    if(args.length > 0) {

      initLogger();

      CommandLine cmd = null;
      try {
        cmd = parseArgs(args);
      } catch(ParseException e) {
        logger.error("Problem parsing CLI arguments: " + e.getMessage());
        System.exit(-1);
      }
      String path = cmd.getOptionValue("c", TaskRunner.CONFIG_FILENAME);

      String[] cliArgs = cmd.getArgs();
      String taskName = cliArgs[0];

      String[] taskArgs = new String[cliArgs.length - 1];
      System.arraycopy(cliArgs, 1, taskArgs, 0, taskArgs.length);

      run(path, taskName, taskArgs);
    } else {
      System.out.println("Usage: elements-tools TASK [params]");
    }
  }

  public void initLogger() {
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
    System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
    System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
    logger = LoggerFactory.getLogger(TaskRunner.class);
  }

  public void run(String path, String taskName, String[] taskArgs) {
    TaskRunner runner = null;
    try {
      runner = new TaskRunner(path);
    } catch(IOException ioe) {
      logger.error("Error instantiating TaskRunner: " + ioe.getMessage());
    }

    if(runner != null) {
      runner.run(taskName, taskArgs);
    }
  }

  public static void main(String[] args) {
    new App(args);
  }

}

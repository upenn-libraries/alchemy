package edu.upenn.library.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

public class App {

  public static CommandLine parseArgs(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("c", true, "config (.properties) file");
    options.addOption("h", false, "show help");

    CommandLineParser parser = new DefaultParser();
    return parser.parse(options, args);
  }

  /**
   * extract options and args from CommandLine object so they can be passed to Tasks
   * in a way that's decoupled from the Apache Commons CLI library.
   * @param commandLine
   * @return
   */
  public static Map<String, List<String>> extractOptions(CommandLine commandLine) {
    Map<String, List<String>> options = new HashMap<>();
    for(Option o : commandLine.getOptions()) {
      options.put(o.getOpt(), o.getValuesList());
    }
    return options;
  }

  protected Logger logger;

  public App(String[] args) {
    if(args.length > 0) {

      initLogger();

      CommandLine commandLine = null;
      try {
        commandLine = parseArgs(args);
      } catch(ParseException e) {
        logger.error("Problem parsing CLI arguments: " + e.getMessage());
        System.exit(-1);
      }
      String configPath = commandLine.getOptionValue("c", TaskRunner.CONFIG_FILENAME);

      String[] cliArgs = commandLine.getArgs();
      String taskName = cliArgs[0];

      run(configPath, taskName, commandLine);
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

  public void run(String configPath, String taskName, CommandLine commandLine) {
    TaskRunner runner = null;
    try {
      runner = new TaskRunner(configPath);
    } catch(IOException ioe) {
      logger.error("Error instantiating TaskRunner: " + ioe.getMessage());
    }

    if(runner != null) {
      runner.run(taskName, extractOptions(commandLine), commandLine.getArgList());
    }
  }

  public static void main(String[] args) {
    new App(args);
  }

}

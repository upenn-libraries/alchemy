package edu.upenn.library.elements;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import edu.upenn.library.elements.tasks.CategoryTypesReport;
import edu.upenn.library.elements.tasks.Dump;
import edu.upenn.library.elements.tasks.GetCVStoredProcedures;
import edu.upenn.library.elements.tasks.GetReportAssets;
import edu.upenn.library.elements.tasks.GetUserId;
import edu.upenn.library.elements.tasks.GroupsReport;
import edu.upenn.library.elements.tasks.SamplePublicationSearch;
import edu.upenn.library.elements.tasks.SampleImportActivityAndCreateRelationship;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.impl.SimpleLogger;

public class App {

  private Options options = new Options();

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

  public static TaskResolver createDefaultTaskResolver() {
    TaskResolver taskResolver = new TaskResolver();
    taskResolver.addTask(new Dump());
    taskResolver.addTask(new CategoryTypesReport());
    taskResolver.addTask(new GetCVStoredProcedures());
    taskResolver.addTask(new GetReportAssets());
    taskResolver.addTask(new GetUserId());
    taskResolver.addTask(new GroupsReport());
    taskResolver.addTask(new SamplePublicationSearch());
    taskResolver.addTask(new SampleImportActivityAndCreateRelationship());
    return taskResolver;
  }

  protected TaskResolver taskResolver = null;
  protected TaskRunner taskRunner = null;

  public App(String[] args) {

    CommandLine commandLine = null;
    try {
      commandLine = parseArgs(args);
    } catch(ParseException e) {
      System.err.println("Problem parsing CLI arguments: " + e.getMessage());
      System.exit(-1);
    }
    String configPath = commandLine.getOptionValue("c", TaskRunner.CONFIG_FILENAME);

    Config config = new Config();
    File configFile = new File(configPath);
    if(configFile.exists()) {
      try {
        config.load(new FileReader(configFile));
      } catch(Exception e) {
        System.err.println("Couldn't load config file " + configPath + ": " + e.getMessage());
        System.exit(-1);
      }
    }

    initLogger(config.getProperty(Config.KEY_LOGLEVEL, "info"));

    this.taskResolver = createDefaultTaskResolver();
    try {
      this.taskRunner = new TaskRunner(taskResolver, config);
    } catch(IOException ioe) {
      System.err.println("Error instantiating TaskRunner: " + ioe.getMessage());
    }

    String[] cliArgs = commandLine.getArgs();
    if(cliArgs.length > 0) {
      String taskName = cliArgs[0];
      run(taskName, commandLine);
    } else {
      StringBuilder footer = new StringBuilder();
      footer.append("\nAvailable tasks (run with -h to get help for a specific task):\n\n");
      footer.append(
        taskResolver.getTasks().stream()
        .sorted((a,b) -> a.getClass().getSimpleName().compareTo(b.getClass().getSimpleName()))
        .map(task -> "  " + task.getClass().getSimpleName() + " - " + task.getDescription() + "\n")
        .collect(Collectors.joining()));
      footer.append("\n");

      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(100, "alchemy TASK [params]", "", options, footer.toString(), true);
    }
  }

  public CommandLine parseArgs(String[] args) throws ParseException {
    options.addOption("c", true, "config (.properties) file");
    options.addOption("e", true, "API environment (default is 'dev')");
    options.addOption("d", true, "database name (default is 'reporting')");
    options.addOption("h", false, "show help");

    CommandLineParser parser = new DefaultParser();
    return parser.parse(options, args);
  }

  public void initLogger(String logLevel) {
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel);
    System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
    System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
  }

  public void run(String taskName, CommandLine commandLine) {
    if(taskRunner != null) {
      taskRunner.run(taskName, extractOptions(commandLine), commandLine.getArgList());
    }
  }

  public static void main(String[] args) {
    new App(args);
  }

}

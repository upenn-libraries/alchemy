package edu.upenn.library.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.upenn.library.elements.tasks.CategoryTypesReport;
import edu.upenn.library.elements.tasks.Dump;
import edu.upenn.library.elements.tasks.GetUserId;
import edu.upenn.library.elements.tasks.Task;
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

  public static TaskResolver createDefaultTaskResolver() {
    TaskResolver taskResolver = new TaskResolver();
    taskResolver.addTask(new Dump());
    taskResolver.addTask(new CategoryTypesReport());
    taskResolver.addTask(new GetUserId());
    return taskResolver;
  }

  protected Logger logger;
  protected TaskResolver taskResolver = null;
  protected TaskRunner taskRunner = null;

  public App(String[] args) {
    initLogger();

    CommandLine commandLine = null;
    try {
      commandLine = parseArgs(args);
    } catch(ParseException e) {
      logger.error("Problem parsing CLI arguments: " + e.getMessage());
      System.exit(-1);
    }
    String configPath = commandLine.getOptionValue("c", TaskRunner.CONFIG_FILENAME);

    this.taskResolver = createDefaultTaskResolver();
    try {
      this.taskRunner = new TaskRunner(taskResolver, configPath);
    } catch(IOException ioe) {
      logger.error("Error instantiating TaskRunner: " + ioe.getMessage());
    }

    String[] cliArgs = commandLine.getArgs();
    if(cliArgs.length > 0) {
      String taskName = cliArgs[0];
      run(taskName, commandLine);
    } else {
      System.out.println("Usage: elements-tools TASK [params]");
      System.out.println();
      System.out.println("Available tasks (run with -h to get help for a specific task):");
      System.out.println();
      for (Task task : taskResolver.getTasks()) {
        System.out.println("  " + task.getClass().getSimpleName() + " - " + task.getDescription());
      }
      System.out.println();
    }
  }

  public void initLogger() {
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
    System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
    System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
    logger = LoggerFactory.getLogger(TaskRunner.class);
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

package edu.upenn.library.elements;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import edu.upenn.library.elements.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A TaskRunner is reusable and maintains configuration info
 * and a registry of built-in tasks. The CLI is basically just a
 * thin wrapper around this class.
 */
public class TaskRunner {

  public static final String CONFIG_FILENAME = "elements-tools.properties";

  private final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

  private TaskResolver taskResolver;
  private Config config = new Config();

  public TaskRunner(TaskResolver taskResolver) throws IOException {
    this(taskResolver, CONFIG_FILENAME);
  }

  public TaskRunner(TaskResolver taskResolver, String configPath) throws IOException {
    this.taskResolver = taskResolver;
    initConfig(configPath);
  }

  protected void initConfig(String filename) throws IOException {
    File configFile = new File(filename);
    if(configFile.exists()) {
      config.load(new FileReader(configFile));
    }
  }

  public void run(String taskName, Map<String, List<String>> options, List<String> args) {
    Task task = null;
    try {
      task = taskResolver.getTask(taskName);
    } catch(Exception e) {
      logger.error("Couldn't get task: " + e.getMessage());
    }
    if(task != null) {
      if(options.containsKey("h")) {
        System.out.println(task.getDescription());
      } else {
        run(task, options, args);
      }
    } else {
      logger.error("Task not found: " + taskName);
    }
  }

  public void run(Task task, Map<String, List<String>> options, List<String> args) {
    try {
      task.init(config, options, args.subList(1, args.size()));
    } catch(Exception e) {
      logger.error("Problem occurred in task.init(): " + e.getMessage());
      logger.debug("Stack trace: ");
      for (StackTraceElement ste : e.getStackTrace()) {
        logger.debug(ste.toString());
      }
      return;
    }
    try {
      task.execute();
    } catch(Exception e) {
      logger.error("Problem occurred in task.execute(): " + e.getMessage());
      logger.debug("Stack trace: ");
      for (StackTraceElement ste : e.getStackTrace()) {
        logger.debug(ste.toString());
      }
    }
  }

}

package edu.upenn.library.elements;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import edu.upenn.library.elements.tasks.CategoryTypesReport;
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

  private Config config = new Config();
  private List<Class> tasks = new ArrayList<>();

  public TaskRunner() throws IOException {
    this(CONFIG_FILENAME);
  }

  public TaskRunner(String configPath) throws IOException {
    initConfig(configPath);
    initTasks();
  }

  protected void initConfig(String filename) throws IOException {
    File configFile = new File(filename);
    if(configFile.exists()) {
      config.load(new FileReader(configFile));
    }
  }

  protected void initTasks() {
    tasks.add(CategoryTypesReport.class);
  }

  public List<Class> getTasks() { return tasks; }

  public void run(String taskName, Map<String, List<String>> options, List<String> args) {
    Task task = null;
    Optional<Class> taskOpt = tasks.stream().filter(c -> taskName.equals(c.getSimpleName())).findFirst();
    if (taskOpt.isPresent()) {
      // try loading Task via our list of registered tasks
      try {
        task = (Task) taskOpt.get().newInstance();
      } catch (Exception e) {
        logger.error("Couldn't instantiate task: " + e);
      }
    } else {
      // try loading Task via fully qualified Task name
      Class clazz = null;
      try {
        clazz = Class.forName(taskName);
      } catch (ClassNotFoundException e) {
        // handle below
      }
      if (clazz != null) {
        try {
          task = (Task) clazz.newInstance();
        } catch (Exception e) {
          logger.error("Couldn't instantiate task: " + e);
        }
      }
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
      task.init(config, options, args);
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

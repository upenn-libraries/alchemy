package edu.upenn.library.elements;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

public class App {

  public static void main(String[] args) {

    if(args.length > 0) {
      System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
      System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
      System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");

      Logger logger = LoggerFactory.getLogger(TaskRunner.class);

      String taskName = args[0];

      String[] taskArgs = new String[args.length - 1];
      System.arraycopy(args, 1, taskArgs, 0, taskArgs.length);

      TaskRunner runner = null;
      try {
        runner = new TaskRunner();
      } catch(IOException ioe) {
        logger.error("Error instantiating TaskRunner: " + ioe.getMessage());
      }

      if(runner != null) {
        runner.run(taskName, taskArgs);
      }

    } else {
      System.out.println("Usage: elements-tools TASK [params]");
    }
  }

}

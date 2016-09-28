package edu.upenn.library.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This really does two things: 1) maintains a registry of Tasks
 * that can be looked up by name, 2) tries to load tasks by
 * fully qualified name via the Java class loader.
 */
public class TaskResolver {

  private final Logger logger = LoggerFactory.getLogger(TaskResolver.class);

  private List<Task> tasks = new ArrayList<>();

  public void addTask(Task task) {
    tasks.add(task);
  }

  public List<Task> getTasks() {
    return tasks;
  }

  /**
   * @param taskName can be simple class name or a fully qualified name
   * @return
   * @throws Exception
   */
  public Task getTask(String taskName) throws Exception {
    Task task = null;
    // try finding Task in our list of registered tasks
    Optional<Task> taskOpt =
      tasks.stream().filter(t -> {
        return taskName.equals(t.getClass().getSimpleName()) ||
          taskName.equals(t.getClass().getName());
      }).findFirst();
    if (taskOpt.isPresent()) {
      task = taskOpt.get();
    } else {
      // try loading Task via fully qualified Task name
      Class clazz = Class.forName(taskName);
      if (clazz != null) {
        task = (Task) clazz.newInstance();
      }
      // TODO: should this task really be added?
      addTask(task);
    }
    return task;
  }

}

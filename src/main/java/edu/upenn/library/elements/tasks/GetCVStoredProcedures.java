package edu.upenn.library.elements.tasks;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import edu.upenn.library.elements.Task;

public class GetCVStoredProcedures extends Task {

  @Override
  public String getDescription() {
    return "Write CV-related stored procedures to .sql files";
  }

  @Override
  public String getHelp() {
    return "Usage: GetCVStoredProcedures TARGET_DIR\n" +
      "\n" +
      getDescription() + "\n" +
      "\n" +
      "A stored proc is considered CV-related if its name has the string 'CV'\n" +
      "in it. This task is useful for backups, and to facilitate committing stored\n" +
      "proc definitions into a version control system.\n";
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 1) {
      getLogger().error("You must specify a target directory");
      return;
    }

    String dir = getArgs().get(0);
    if(!new File(dir).exists()) {
      getLogger().error("Target directory " + dir + "  doesn't exist.");
      return;
    }

    Connection c = getDatabaseConnection();
    PreparedStatement ps = c.prepareStatement("select OBJECT_NAME(OBJECT_ID), definition from sys.sql_modules where objectproperty(OBJECT_ID, 'IsProcedure')=1 and definition like '%CV%';");
    ResultSet rs = ps.executeQuery();
    while(rs.next()) {
      String storedProcName = rs.getString(1);
      String definition = rs.getString(2);
      FileWriter f = new FileWriter(Paths.get(dir, storedProcName + ".sql").toString());
      f.write(definition);
      f.close();
    }
  }
}

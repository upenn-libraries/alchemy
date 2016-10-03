package edu.upenn.library.elements.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import edu.upenn.library.elements.Task;

public class GetUserId extends Task {

  @Override
  public String getDescription() {
    return "find the user ID for a given username";
  }

  @Override
  public String getHelp() {
    return "Usage: GetUserId USERNAME\n" +
      "\n" +
      "Look up the user id for the given username.\n" +
      "\n" +
      "This is a silly task, it exists mostly to demonstrate how\n" +
      "to do database access.";
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 1) {
      getLogger().error("You must specify a username");
      return;
    }

    String username = getArgs().get(0);

    Connection c = getDatabaseConnection();
    PreparedStatement ps = c.prepareStatement("select [ID] from [User] where [Username] = ?");
    ps.setString(1, username);
    ResultSet rs = ps.executeQuery();
    if(rs.next()) {
      System.out.println("ID for " + username + " = " + rs.getInt(1));
    } else {
      System.out.println("Couldn't find user " + username);
    }
  }
}

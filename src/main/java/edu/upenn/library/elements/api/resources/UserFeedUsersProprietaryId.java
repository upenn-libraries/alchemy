package edu.upenn.library.elements.api.resources;

/**
 * Resource: /user-feed/users/{proprietary-id}
 */
public class UserFeedUsersProprietaryId {

  private final String id;

  public UserFeedUsersProprietaryId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return "/user-feed/users/" + getId();
  }

  public String getAtomEntryElement() {
    return "user-feed-entry";
  }

}

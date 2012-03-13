package at.peppol.webgui.app.model;

import at.peppol.commons.utils.UsernamePWCredentials;

/**
 * @author Jerouris
 * @see UsernamePWCredentials
 */
public class User {

  private String username;
  private String password;

  public String getPassword () {
    return password;
  }

  public void setPassword (final String password) {
    this.password = password;
  }

  public String getUsername () {
    return username;
  }

  public void setUsername (final String username) {
    this.username = username;
  }

}

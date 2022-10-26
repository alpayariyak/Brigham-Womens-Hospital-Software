package edu.wpi.DapperDaemons.controllers.helpers;

/** This Class will help the program parse the security information */
public class SecuritySettingsHelper {
  static String settingsFile;

  public static void setUserSettings(String SettingsFile) {
    settingsFile = settingsFile;
  }

  // Returns the CSS styling file
  public static String getCSSstyling() {
    // TODO : Return the correct CSS styling file, currently not set up
    return settingsFile;
  }

  public static String getSecurityAuthentication() {
    // TODO : Return the correct security level based on what a user has (RFID, Just password)
    return "Two Factor Authentication not setup yet.";
  }
}

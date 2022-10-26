package edu.wpi.DapperDaemons.backend;

public class FireBaseCoder {

  private FireBaseCoder() {}

  public static String encodeForFirebaseKey(String s) {
    return s.replace("_", "____")
        .replace(".", "___P")
        .replace("$", "___D")
        .replace("#", "___H")
        .replace("[", "___O")
        .replace("]", "___C")
        .replace("/", "___S");
  }

  public static String decodeFirebaseKey(String s) {
    return s.replace("____", "_")
        .replace("___P", ".")
        .replace("___D", "$")
        .replace("___H", "#")
        .replace("___O", "[")
        .replace("___C", "]")
        .replace("___S", "/");
  }
}

package edu.wpi.DapperDaemons.controllers.homePage;

import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class ThemeHandler {

  private static Theme theme;
  private static HBox mainBox;

  public enum Theme {
    Light,
    Dark,
    Blue,
    Red
  }

  public ThemeHandler(HBox topElement) {
    mainBox = topElement;
  }

  @FXML
  public static void toggleTheme(Theme newTheme) {
    theme = newTheme;
    setTheme();
  }

  public static void setTheme() {
    Set<Node> backs = mainBox.lookupAll("#background");
    Set<Node> fields = mainBox.lookupAll("#field");
    Set<Node> fores = mainBox.lookupAll("#foreground");
    Set<Node> defs = mainBox.lookupAll("#default");
    Set<Node> jButtons = mainBox.lookupAll("#jButton");
    Set<Node> specialFields = mainBox.lookupAll("#specialField");
    Set<Node> texts = mainBox.lookupAll("#label");
    Set<Node> tableCols = mainBox.lookupAll("#col");

    for (Node back : backs) {
      back.getStyleClass().clear();
    }

    for (Node field : fields) {
      field.getStyleClass().clear();
    }

    for (Node fore : fores) {
      fore.getStyleClass().clear();
    }

    for (Node def : defs) {
      def.getStyleClass().clear();
    }

    for (Node jButton : jButtons) {
      jButton.getStyleClass().clear();
    }

    for (Node specialField : specialFields) {
      specialField.getStyleClass().clear();
    }

    for (Node text : texts) {
      text.getStyleClass().clear();
    }

    for (Node col : tableCols) {
      col.getStyleClass().clear();
    }

    if (theme != Theme.Light && theme != null) {

      for (Node back : backs) {
        back.getStyleClass().add("background" + theme.toString());
      }

      for (Node field : fields) {
        field.getStyleClass().add("field" + theme.toString());
      }

      for (Node fore : fores) {
        fore.getStyleClass().add("foreground" + theme.toString());
      }

      for (Node def : defs) {
        def.getStyleClass().add("foreground" + theme.toString());
      }

      for (Node jButton : jButtons) {
        jButton.getStyleClass().add("field" + theme.toString());
      }

      for (Node specialField : specialFields) {
        specialField.getStyleClass().add("specialField" + theme.toString());
      }

      for (Node text : texts) {
        text.getStyleClass().add("text" + theme.toString());
      }

      for (Node col : tableCols) {
        col.getStyleClass().add("table" + theme.toString());
      }
    }
  }
}

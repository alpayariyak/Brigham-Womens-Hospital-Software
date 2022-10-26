package edu.wpi.DapperDaemons.tables;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RowFactory {

  private RowFactory() {}

  public static List<Node> createRow(List<Object> attributes, int padding) {
    HBox row;
    FXMLLoader loader;
    loader =
        new FXMLLoader(Objects.requireNonNull(App.class.getResource("views/" + "row" + ".fxml")));
    HBox loaded;
    try {
      loaded = loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    row = (HBox) loader.getNamespace().get("contents");
    for (Object attr : attributes) {
      VBox item = new VBox();
      item.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(item, Priority.ALWAYS);
      item.setPrefHeight(50);
      item.setMinHeight(Control.USE_PREF_SIZE);
      item.setMaxHeight(Control.USE_PREF_SIZE);
      item.setPadding(new Insets(0, 0, 0, padding));
      if (attr instanceof Node) row.getChildren().add((Node) attr);
      if (attr instanceof Enum) {
        Enum<?> e = (Enum<?>) attr;
        List<String> allAttrs = TableHelper.convertEnum(e.getClass());
        ComboBox<String> box = new ComboBox<>(FXCollections.observableArrayList(allAttrs));
        box.setValue(attr.toString());
        box.setBackground(Background.EMPTY);
        box.setMaxWidth(100);
        box.setMinWidth(100);
        //                box.getEditor()
        //                    .setFont(
        //                        Font.font(
        //                            box.getEditor().getFont().getFamily(),
        //                            FontWeight.BOLD,
        //                            box.getEditor().getFont().getSize()));
        item.getChildren().add(box);
      } else {
        Text text = new Text(attr.toString());
        //        text.setFont(
        //            Font.font(text.getFont().getFamily(), FontWeight.BOLD,
        // text.getFont().getSize()));
        item.getChildren().add(text);
      }
      row.getChildren().add(item);
    }
    // FB6962 - RED
    // F5EC42 - YELLOW
    // 79DE79 - GREEN
    List<Node> ret = new ArrayList<>(row.getChildren());
    VBox priority = (VBox) loaded.getChildren().get(1);
    try {
      switch (Request.Priority.valueOf(
          ((ComboBox) ((VBox) ret.get(ret.size() - 1)).getChildren().get(0))
              .getValue()
              .toString())) {
        case LOW:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(.47, .87, .47, .8),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          ret.add(priority);
          break;
        case MEDIUM:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(.96, .93, .26, .8),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          ret.add(priority);
          break;
        case HIGH:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(.98, .41, .38, .8),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          ret.add(priority);
          break;
        default:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(0, 0, 0, 0.5),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          ret.add(priority);
          break;
      }
    } catch (ClassCastException | IllegalArgumentException | IndexOutOfBoundsException ignored) {
    }
    return ret;
  }
}

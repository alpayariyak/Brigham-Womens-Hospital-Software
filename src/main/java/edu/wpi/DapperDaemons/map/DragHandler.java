package edu.wpi.DapperDaemons.map;

import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class DragHandler {

  private final AnchorPane dragPane;
  private final ImageView node;
  private final StackPane window;
  private GlyphHandler glyphs;

  private ImageView displayed;

  public DragHandler(
      AnchorPane dragPane, StackPane window, ImageView toDragFrom, GlyphHandler glyphs) {
    this.dragPane = dragPane;
    this.node = toDragFrom;
    this.window = window;
    this.glyphs = glyphs;
  }

  public void enable() {
    if (displayed != null) dragPane.getChildren().remove(displayed);
    displayed = new ImageView(node.getImage());
    displayed.setPickOnBounds(true);
    dragPane.getChildren().add(displayed);

    displayed.setOnMouseDragged(
        e -> {
          displayed.setVisible(true);
          PositionInfo snapped = glyphs.getNearestPos((int) e.getX(), (int) e.getY());
          if (snapped != null) {
            displayed.setX(snapped.getX() + 48);
            displayed.setY(snapped.getY());
          } else {
            displayed.setX(e.getX());
            displayed.setY(e.getY());
          }
        });
    displayed.setOnMouseReleased(
        e -> {
          MedicalEquipment equip =
              new MedicalEquipment(
                  getImageType(),
                  MedicalEquipment.EquipmentType.valueOf(getImageType()),
                  "ID19824",
                  null);
          // boolean worked =
          //  glyphs.addEquipment((int) displayed.getX() - 16, (int) displayed.getY() - 16, equip);
          // if (worked) enable();
        });
  }

  private String getImageType() {
    switch (node.getId()) {
      case "infusionDragImage":
        return "INFUSIONPUMP";
      case "bedDragImage":
        return "BED";
      default:
        return "ERROR";
    }
  }

  public void disable() {
    dragPane.getChildren().clear();
    displayed = null;
  }
}

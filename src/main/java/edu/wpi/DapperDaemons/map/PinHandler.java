package edu.wpi.DapperDaemons.map;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class PinHandler {

  private ImageView pin;
  private AnchorPane pinPane;

  public PinHandler(ImageView pin, AnchorPane pinPane) {
    this.pin = pin;
    this.pinPane = pinPane;
    pinPane.getChildren().add(pin);
    pin.setVisible(false);
  }

  public void move(int x, int y) {
    pin.setX(x - 16);
    pin.setY(y - 32);
    pin.setVisible(true);
  }

  public void clear() {
    pin.setVisible(false);
  }

  public int getX() {
    return (int) pin.getX();
  }

  public int getY() {
    return (int) pin.getY();
  }
}

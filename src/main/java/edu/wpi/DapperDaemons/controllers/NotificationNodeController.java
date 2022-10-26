package edu.wpi.DapperDaemons.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javax.swing.text.html.ImageView;

public class NotificationNodeController {
  @FXML private VBox priorityColorBar;
  @FXML private ImageView equipmentImage;
  @FXML private VBox fromLabel;
  @FXML private VBox descriptionLabel;
  @FXML private Label startLocationLabel;
  @FXML private Label destinationLocationLabel;
  @FXML private Circle readCircle;

  @FXML
  private void clearNotification() {}
}

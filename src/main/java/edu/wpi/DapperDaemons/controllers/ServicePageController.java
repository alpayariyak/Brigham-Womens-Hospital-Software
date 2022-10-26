package edu.wpi.DapperDaemons.controllers;

import java.net.URL;
import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/*
Manages Default Page Navigation
 */
public class ServicePageController extends ParentController {
  /* Menu Button images */
  @FXML private Pane labPageContainer;
  @FXML private ImageView labPageImage;
  @FXML private Pane equipmentPageContainer;
  @FXML private ImageView equipmentPageImage;
  @FXML private Pane sanitationPageContainer;
  @FXML private ImageView sanitationPageImage;
  @FXML private Pane medicinePageContainer;
  @FXML private ImageView medicinePageImage;
  @FXML private Pane mealPageContainer;
  @FXML private ImageView mealPageImage;
  @FXML private Pane patientPageContainer;
  @FXML private ImageView patientPageImage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initGraphics();
  }

  private void initGraphics() {
    bindImage(labPageImage, labPageContainer);
    bindImage(equipmentPageImage, equipmentPageContainer);
    bindImage(sanitationPageImage, sanitationPageContainer);
    bindImage(medicinePageImage, medicinePageContainer);
    bindImage(mealPageImage, mealPageContainer);
    bindImage(patientPageImage, patientPageContainer);
  }
}

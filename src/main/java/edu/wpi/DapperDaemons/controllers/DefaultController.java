package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import java.awt.*;
import java.net.URL;
import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/*
Manages Default Page Navigation
 */
public class DefaultController extends ParentController {

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
  @FXML private Pane mapPageContainer;
  @FXML private ImageView mapPageImage;
  @FXML private Pane patientPageContainer;
  @FXML private ImageView patientPageImage;
  @FXML private Pane backendPageContainer;
  @FXML private ImageView apiPageImage;
  @FXML private Pane equipSaniPageContainer;
  @FXML private ImageView equipSaniPageImage;
  @FXML private Pane languagePageContainer;
  @FXML private ImageView languagePageImage;
  @FXML private Pane securityPageContainer;
  @FXML private ImageView securityPageImage;
  @FXML private Pane extTransportPageContainer;
  @FXML private ImageView extTransportPageImage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initGraphics();
  }

  private void initGraphics() {
    //    bindImage(labPageImage, labPageContainer);
    //    bindImage(equipmentPageImage, equipmentPageContainer);
    //    bindImage(sanitationPageImage, sanitationPageContainer);
    //    bindImage(medicinePageImage, medicinePageContainer);
    //    bindImage(mealPageImage, mealPageContainer);
    //    bindImage(mapPageImage, mapPageContainer);
    //    bindImage(patientPageImage, patientPageContainer);
    //    bindImage(apiPageImage, backendPageContainer);
    //    bindImage(equipSaniPageImage, equipSaniPageContainer);
    //    bindImage(languagePageImage, languagePageContainer);
    //    bindImage(securityPageImage, securityPageContainer);
    //    bindImage(extTransportPageImage, extTransportPageContainer);
  }

  /* Hover Animations for buttons */
  @FXML
  private void hoveredButton(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(8, 67, 154, 255);
    Color backgroundStart = new Color(255, 255, 255, 255);
    Color backgroundEnd = new Color(243, 243, 243, 192);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  private void unhoveredButton(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(8, 67, 154, 255);
    Color textEnd = new Color(255, 255, 255, 255);
    Color backgroundEnd = new Color(255, 255, 255, 255);
    Color backgroundStart = new Color(243, 243, 243, 192);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }
}

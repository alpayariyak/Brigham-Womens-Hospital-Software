package edu.wpi.DapperDaemons.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class loadingScreenController extends AppController {

  @FXML ImageView loadingIcon;
  @FXML VBox sceneBox;
  @FXML Label loadingLabel;

  /* Background */
  @FXML private ImageView BGImage;
  @FXML private Pane BGContainer;

  private static Timer loading;
  private static Timer backgroundImages;

  private static int ind = 0;

  public static double map(
      double value, double start, double stop, double targetStart, double targetStop) {
    return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //    bindImage(BGImage, BGContainer);
    //    if (loading != null) loading.cancel();
    //    loading = new Timer();
    //    loading.schedule(
    //        new TimerTask() {
    //          @Override
    //          public void run() {
    //            Platform.runLater(
    //                () -> {
    //                  loadingLabel.setText("Loading...".substring(0, ind));
    //                  ind = (ind + 1) % ("Loading...".length() + 1);
    //                });
    //          }
    //        },
    //        0,
    //        250);
    //    if (backgroundImages != null) backgroundImages.cancel();
    //    backgroundImages = new Timer();
    //    backgroundImages.schedule(
    //        new TimerTask() {
    //          @Override
    //          public void run() {
    //            Platform.runLater(
    //                () -> {
    //                  int currImg = (int) (Math.random() * 9) + 1;
    //                  sceneBox.setBackground(
    //                      new Background(
    //                          new BackgroundImage(
    //                              new Image(
    //                                  Objects.requireNonNull(
    //                                      loadingScreenController
    //                                          .class
    //                                          .getClassLoader()
    //                                          .getResourceAsStream(
    //                                              "edu/wpi/DapperDaemons/loadingScreen/"
    //                                                  + currImg
    //                                                  + ".png"))),
    //                              BackgroundRepeat.SPACE,
    //                              BackgroundRepeat.SPACE,
    //                              BackgroundPosition.CENTER,
    //                              new BackgroundSize(100, 100, true, true, true, true))));
    //                });
    //          }
    //        },
    //        0,
    //        5000);
  }

  public static void stop() {
    //    loading.cancel();
    //    backgroundImages.cancel();
  }
}

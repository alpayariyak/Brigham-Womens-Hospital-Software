package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.CSVLoader;
import edu.wpi.DapperDaemons.backend.CSVSaver;
import edu.wpi.DapperDaemons.backend.LogSaver;
import edu.wpi.DapperDaemons.controllers.helpers.TableListeners;
import edu.wpi.DapperDaemons.entities.TableObject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppController implements Initializable {

  @FXML private StackPane windowContents;
  @FXML protected VBox sceneBox;

  private static VBox error;
  private static VBox confirmation;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    /* Sets up the error message*/
    try {
      error =
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource("views/" + "errorMessage.fxml")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    error.setVisible(false);
    error.setPickOnBounds(false);
    HBox errorContainer = new HBox();
    errorContainer.setPickOnBounds(false);
    windowContents.getChildren().add(errorContainer);
    errorContainer.getChildren().add(error);
    errorContainer.setAlignment(Pos.CENTER);
    errorContainer.setPadding(new Insets(20, 20, 20, 20));

    /* Sets up the confirmation message*/
    try {
      confirmation =
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource("views/" + "confirmationMessage.fxml")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    confirmation.setVisible(false);
    confirmation.setPickOnBounds(false);
    HBox confirmationContainer = new HBox();
    confirmationContainer.setPickOnBounds(false);
    windowContents.getChildren().add(confirmationContainer);
    confirmationContainer.getChildren().add(confirmation);
    confirmationContainer.setAlignment(Pos.CENTER);
    confirmationContainer.setPadding(new Insets(20, 20, 20, 20));
  }

  /** Creates an error box pop-up on the screen */
  public static void showError(String errorMessage) {
    App.LOG.warn("Caught error: " + errorMessage);
    error.setVisible(true);
    Node nodeOut = error.getChildren().get(1);
    if (nodeOut instanceof VBox) {
      for (Node nodeIn : ((VBox) nodeOut).getChildren()) {
        if (nodeIn instanceof Label) {
          ((Label) nodeIn).setText(errorMessage);
        }
      }
    }
  }

  /** Creates an error box pop-up based on a specific location */
  public static void showError(String errorMessage, Pos pos) {
    ((HBox) error.getParent()).setAlignment(pos);
    showError(errorMessage);
  }

  protected void switchScene(String fileName, int minWidth, int minHeight) throws IOException {
    Stage window = (Stage) sceneBox.getScene().getWindow();
    if (EasterEggController.player != null) EasterEggController.player.stop();
    switchScene(fileName, minWidth, minHeight, window);
  }

  protected void switchScene(String fileName, int minWidth, int minHeight, Stage window)
      throws IOException {
    TableListeners.removeAllListeners();
    App.LOG.info("Switching to page: <" + fileName + ">");
    Parent root =
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("views/" + fileName)));
    window.setMinWidth(minWidth);
    window.setMinHeight(minHeight);
    window.setOnCloseRequest(e -> quitProgram());
    window.getScene().setRoot(root);
  }

  @FXML
  protected void quitProgram() {
    App.LOG.info("Closing program");
    LogSaver.saveAll();
    CSVSaver.saveAll();
    App.LOG.info("Successfully saved all files!");
    if (sceneBox != null && sceneBox.getScene() != null) {
      Stage window = (Stage) sceneBox.getScene().getWindow();
      if (window != null) window.close();
    }
    Platform.exit();
    System.exit(0);
  }

  public static void bindImage(ImageView pageImage, Pane parent) {
    pageImage.fitHeightProperty().bind(parent.heightProperty());
    pageImage.fitWidthProperty().bind(parent.widthProperty());
  }

  public static void bindChild(HBox child) {
    HBox.setHgrow(child, Priority.ALWAYS);
  }

  protected void saveToCSV(TableObject type, Stage window) {
    FileChooser fileSys = new FileChooser();
    //    Stage window = (Stage) sceneBox.getScene().getWindow();
    fileSys.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
    File csv = fileSys.showSaveDialog(window);
    try {
      CSVSaver.save(type, csv.getAbsolutePath());
    } catch (Exception e) {
      App.LOG.error("Unable to Save CSV of type: " + type);
    }
  }

  /** Creates an error box pop-up on the screen */
  public static void showConfirmation(String confirmationMessage) {
    confirmation.setVisible(true);
    Node nodeOut = confirmation.getChildren().get(1);
    if (nodeOut instanceof VBox) {
      for (Node nodeIn : ((VBox) nodeOut).getChildren()) {
        if (nodeIn instanceof Label) {
          ((Label) nodeIn).setText(confirmationMessage);
        }
      }
    }
  }

  /** Creates an error box pop-up based on a specific location */
  public static void showConfirmation(String confirmationMessage, Pos pos) {
    ((HBox) confirmation.getParent()).setAlignment(pos);
    showConfirmation(confirmationMessage);
  }

  protected void saveToCSV(Stage window) {
    DirectoryChooser fileSys = new DirectoryChooser();
    //    Stage window = (Stage) sceneBox.getScene().getWindow();
    fileSys.setTitle("Saving to CSVs");
    File selectedDirectory = fileSys.showDialog(window);
    try {
      CSVSaver.saveAll(selectedDirectory.getAbsolutePath());
    } catch (Exception e) {
      App.LOG.error("Unable to Save CSV");
    }
  }

  protected void loadFromCSV(Stage window) {
    FileChooser fileSys = new FileChooser(); // TODO filenotfoundexception
    //    Stage window = (Stage) sceneBox.getScene().getWindow();
    fileSys.getExtensionFilters().add(new FileChooser.ExtensionFilter("Load From CSV", "*.csv"));
    List<File> csvs = fileSys.showOpenMultipleDialog(window);
    try {
      for (File f : csvs) {
        if (CSVLoader.filenames.get(f.getName().replace(".csv", "")) != null) {
          CSVLoader.loadPCToFirebase(
              CSVLoader.filenames.get(f.getName().replace(".csv", "")), f.getAbsolutePath());
        }
      }
    } catch (Exception e) {
      App.LOG.error("Unable to Load CSVs");
    }
  }
}

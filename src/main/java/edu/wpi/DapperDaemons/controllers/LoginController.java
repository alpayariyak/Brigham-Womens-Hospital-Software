package edu.wpi.DapperDaemons.controllers;

import arduino.Arduino;
import edu.wpi.DapperDaemons.APIConverters.InternalReqConverter;
import edu.wpi.DapperDaemons.backend.*;
import edu.wpi.DapperDaemons.backend.loadingScreen.LoadingScreen;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.entities.Account;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.UnableToConnectException;
import edu.wpi.DapperDaemons.map.serial.SerialCOM;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController extends AppController {

  @FXML private TextField username;
  @FXML private PasswordField password;
  @FXML private VBox tfaBox;
  @FXML private TextField code;

  /* Background */
  @FXML private ImageView BGImage;
  @FXML private Pane BGContainer;

  private final DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();
  private final DAO<Account> accountDAO = DAOPouch.getAccountDAO();

  private SoundPlayer player;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);
    //    bindImage(BGImage, BGContainer);

    code.setOnKeyPressed(
        e -> {
          try {
            keyPressedAuth(e);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
  }

  /** Loads RFID with loading screen */
  public void loadRFID() {
    Stage window = (Stage) username.getScene().getWindow();
    LoadingScreen ls = new LoadingScreen(window);
    try {
      ls.display(
          () -> {
            if (!System.getProperty("os.name").trim().toLowerCase().contains("windows")) {
              RFIDPageController.errorOS = System.getProperty("os.name").trim();
            } else {
              RFIDPageController.errorOS = null;
              Arduino arduino;
              SerialCOM serialCOM = new SerialCOM();
              try {
                arduino = serialCOM.setupArduino();
                RFIDPageController.COM = arduino.getPortDescription();
              } catch (UnableToConnectException e) {
                RFIDPageController.COM = null;
              }
            }
          },
          () -> {
            try {
              switchScene("RFIDScanPage.fxml", 635, 510, window);
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The login method invoked when the user attempts to log into the program
   *
   * @throws Exception if there is an issue :p (This should not happen)
   */
  @FXML
  void login() throws Exception {
    // Easter egg
    if (username.getText().trim().equals("Rick") && password.getText().trim().equals("Astley")) {
      player = new SoundPlayer("edu/wpi/DapperDaemons/assets/unsuspectingWavFile.wav");
      player.play();
    }

    // Get account
    Account acc = accountDAO.get(username.getText());

    // Ensure valid account
    if (acc == null || !acc.checkPassword(password.getText())) {
      showError("Either your username or password is incorrect.");
      return;
    }
    InternalReqConverter.employeeID = acc.getAttribute(1);

    // Get valid Employee (throws error if system has a problem)
    switch (acc.getAttribute(6)) {
      case "SMS":
        tfaBox.setVisible(true);
        Authentication.sendAuthCode(acc);
        break;
      case "rfid":
        RFIDPageController.user = DAOFacade.getEmployee(username.getText());
        loadRFID();
        break;
      default:
        // No 2fa Specified
        Employee user = DAOFacade.getEmployee(username.getText());
        SecurityController.setUser(user);
        switchScene("parentHeader.fxml", 635, 510);
    }
  }

  @FXML
  void hidePopup() {
    tfaBox.setVisible(false);
  }

  @FXML
  void authenticate() throws Exception {
    try {
      int authCode = Integer.parseInt(code.getText());
      if (Authentication.authenticate(authCode)) {
        Employee user = DAOFacade.getEmployee(username.getText());
        SecurityController.setUser(user);
        switchScene("parentHeader.fxml", 635, 510);
        return;
      }
    } catch (NumberFormatException skipCode) {
    }
    showError("Invalid code");
  }

  @FXML
  public void keyPressed(KeyEvent event) throws Exception {
    if (event.getCode().equals(KeyCode.ENTER)) {
      login();
    }
  }

  @FXML
  public void keyPressedAuth(KeyEvent event) throws Exception {
    if (event.getCode().equals(KeyCode.ENTER)) {
      authenticate();
    }
  }

  /* Animations */
  @FXML
  void hoveredSubmit(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(5, 47, 146, 255);
    Color textEnd = new Color(255, 255, 255, 255);
    Color backgroundStart = new Color(5, 47, 146, 0);
    Color backgroundEnd = new Color(5, 47, 146, 255);
    AnimationHelper.fadeNodeWithText(node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredSubmit(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(5, 47, 146, 255);
    Color textEnd = new Color(255, 255, 255, 255);
    Color backgroundStart = new Color(5, 47, 146, 0);
    Color backgroundEnd = new Color(5, 47, 146, 255);
    AnimationHelper.fadeNodeWithText(node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }
}

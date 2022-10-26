package edu.wpi.DapperDaemons.controllers;

import static edu.wpi.DapperDaemons.backend.ConnectionHandler.*;

import com.jfoenix.controls.JFXHamburger;
import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.*;
import edu.wpi.DapperDaemons.backend.preload.Images;
import edu.wpi.DapperDaemons.controllers.helpers.*;
import edu.wpi.DapperDaemons.controllers.homePage.*;
import edu.wpi.DapperDaemons.wongSweeper.MinesweeperZN;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ParentController extends AppController {

  /* Time, Weather, and Database */
  @FXML private Label time;
  @FXML private ImageView weatherIcon;
  @FXML private Label tempLabel;
  @FXML private ImageView serverIcon;
  @FXML private ToggleButton serverToggle;
  @FXML private ImageView serverSlotOne;
  @FXML private ImageView serverSlotTwo;
  @FXML private Text serverSlotOneText;
  @FXML private Text serverSlotTwoText;
  @FXML private VBox serverDropdown;
  @FXML private Button serverButtonOne;
  @FXML private Button serverButtonTwo;

  /* Account */
  @FXML private Text accountName;
  @FXML private Circle profilePic;
  @FXML private VBox userDropdown;
  @FXML private ToggleButton userSettingsToggle;

  /* Common UI */
  @FXML protected ImageView homeIcon;
  @FXML private JFXHamburger burg;
  @FXML private JFXHamburger burgBack;
  @FXML private VBox slider;
  @FXML private HBox childContainer;
  @FXML private Text headerNameField;

  /* Static across all home pages */
  private static HBox mainBox;
  private static Text headerName;
  private static WeatherHandler weather;
  private static DBSwitchHandler dbSwitch;
  private static NotificationReceiver notifs;
  @FXML private ToggleButton alertButton;
  @FXML private VBox notifications;
  @FXML private ScrollPane notificationsScroller;
  @FXML private ImageView notifBell;
  @FXML private ImageView autoSaveIcon;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);
    notificationsScroller.setOnMouseClicked(
        e -> {
          swapPage("notificationsPage", "Notifications");
        });
    NotificationSender.start();
    OverdueHandler.init();
    CleanEquipmentHandler.init();
    if (!AutoSave.started()) AutoSave.start(10, autoSaveIcon);
    menuSlider(slider, burg, burgBack);
    initSequence();
    sceneBox.setOnMouseClicked(
        e -> {
          if (burgBack.isVisible()) closeSlider();
          serverToggle.setSelected(false);
          openServerDropdown();
          userSettingsToggle.setSelected(false);
          openUserDropdown();
          alertButton.setSelected(false);
          notificationsScroller.setVisible(alertButton.isSelected());
        });
    if (childContainer != null) mainBox = childContainer;
    if (headerNameField != null) headerName = headerNameField;

    setServerToggleMenu();

    dbSwitch = new DBSwitchHandler(serverIcon);
    new DateHandler(time);
    new AccountHandler(accountName, profilePic);
    weather = new WeatherHandler(weatherIcon, tempLabel);
    notifs = new NotificationReceiver(notifications, notifBell);
    ThemeHandler themeHandler = new ThemeHandler(mainBox);

    updateWeather();
    swapPage("default", "Home");
  }

  public void swapPage(String page, String pageName) {
    SessionTimeout.reset();
    TableListeners.removeAllListeners();
    App.LOG.info("Switching to page: <" + page + ">");
    mainBox.getChildren().clear();
    mainBox.setOnMouseMoved(e -> SessionTimeout.reset());
    if (burgBack != null && burgBack.isVisible()) closeSlider();
    if (EasterEggController.player != null) EasterEggController.player.stop();

    try {

      HBox childPage =
          FXMLLoader.load(Objects.requireNonNull(App.class.getResource("views/" + page + ".fxml")));
      mainBox.getChildren().add(childPage);
      bindChild(childPage);
      headerName.setText(pageName);
      bindChild(childPage);

    } catch (IOException e) {
      e.printStackTrace();
    }
    ThemeHandler.setTheme();
  }

  @FXML
  void goHome() {
    if (headerName != null && headerName.getText().equals("Home")) {
      try {
        easterEgg();
      } catch (IOException e) {
        showError("Dr. Mario is not home ):");
      }
    } else {
      swapPage("default", "Home");
    }
  }

  @FXML
  void openUserDropdown() {
    userDropdown.setVisible(userSettingsToggle.isSelected());
    serverToggle.setSelected(false);
    serverDropdown.setVisible(false);
    alertButton.setSelected(false);
    notificationsScroller.setVisible(false);
  }

  @FXML
  void openServerDropdown() {
    serverDropdown.setVisible(serverToggle.isSelected());
    alertButton.setSelected(false);
    notificationsScroller.setVisible(false);
    userSettingsToggle.setSelected(false);
    userDropdown.setVisible(false);
  }

  @FXML
  void openNotifications() {
    notificationsScroller.setVisible(alertButton.isSelected());
    serverToggle.setSelected(false);
    serverDropdown.setVisible(false);
    userSettingsToggle.setSelected(false);
    userDropdown.setVisible(false);
  }

  private void setServerToggleMenu() {
    switch (ConnectionHandler.getType()) {
      case EMBEDDED:
        serverSlotOne.setImage(Images.CLOUD);
        serverSlotTwo.setImage(Images.SERVER);
        serverSlotOneText.setText("Firebase");
        serverSlotTwoText.setText("Client Server");
        serverButtonOne.setOnMouseClicked(
            event -> {
              dbSwitch.setLoad();
              new Thread(
                      () -> {
                        serverToggle.setSelected(false);
                        openServerDropdown();
                        if (switchToCloudServer()) {
                          Platform.runLater(
                              () -> {
                                setServerToggleMenu();
                                serverIcon.setImage(Images.CLOUD);
                              });
                        } else {
                          serverIcon.setImage(Images.EMBEDDED);
                        }
                      })
                  .start();
            });
        serverButtonTwo.setOnMouseClicked(
            event -> {
              dbSwitch.setLoad();
              new Thread(
                      () -> {
                        serverToggle.setSelected(false);
                        openServerDropdown();
                        if (switchToClientServer()) {
                          Platform.runLater(
                              () -> {
                                setServerToggleMenu();
                                serverIcon.setImage(Images.SERVER);
                              });
                        } else {
                          serverIcon.setImage(Images.EMBEDDED);
                        }
                      })
                  .start();
            });
        break;
      case CLIENTSERVER:
        serverSlotOne.setImage(Images.CLOUD);
        serverSlotTwo.setImage(Images.EMBEDDED);
        serverSlotOneText.setText("Firebase");
        serverSlotTwoText.setText("Embedded");
        serverButtonOne.setOnMouseClicked(
            event -> {
              dbSwitch.setLoad();
              new Thread(
                      () -> {
                        serverToggle.setSelected(false);
                        openServerDropdown();
                        if (switchToCloudServer()) {
                          Platform.runLater(
                              () -> {
                                setServerToggleMenu();
                                serverIcon.setImage(Images.CLOUD);
                              });
                        } else {
                          serverIcon.setImage(Images.SERVER);
                        }
                      })
                  .start();
            });
        serverButtonTwo.setOnMouseClicked(
            event -> {
              dbSwitch.setLoad();
              new Thread(
                      () -> {
                        serverToggle.setSelected(false);
                        openServerDropdown();
                        if (switchToEmbedded()) {
                          Platform.runLater(
                              () -> {
                                setServerToggleMenu();
                                serverIcon.setImage(Images.EMBEDDED);
                              });
                        } else {
                          serverIcon.setImage(Images.SERVER);
                        }
                      })
                  .start();
            });
        break;
      case CLOUD:
        serverSlotOne.setImage(Images.SERVER);
        serverSlotTwo.setImage(Images.EMBEDDED);
        serverSlotOneText.setText("Client Server");
        serverSlotTwoText.setText("Embedded");
        serverButtonOne.setOnMouseClicked(
            event -> {
              dbSwitch.setLoad();
              new Thread(
                      () -> {
                        serverToggle.setSelected(false);
                        openServerDropdown();
                        if (switchToClientServer()) {
                          Platform.runLater(
                              () -> {
                                setServerToggleMenu();
                                serverIcon.setImage(Images.SERVER);
                              });
                        } else {
                          serverIcon.setImage(Images.CLOUD);
                        }
                      })
                  .start();
            });
        serverButtonTwo.setOnMouseClicked(
            event -> {
              dbSwitch.setLoad();
              new Thread(
                      () -> {
                        serverToggle.setSelected(false);
                        openServerDropdown();
                        if (switchToEmbedded()) {
                          Platform.runLater(
                              () -> {
                                setServerToggleMenu();
                                serverIcon.setImage(Images.EMBEDDED);
                              });
                        } else {
                          serverIcon.setImage(Images.CLOUD);
                        }
                      })
                  .start();
            });
        break;
    }
  }

  @FXML
  public void openUserSettings() {
    userSettingsToggle.setSelected(false);
    openUserDropdown();
    swapPage("NewUserHome", "User Home");
  }

  @FXML
  public void logout() throws IOException {
    NotificationSender.stop();
    NotificationReceiver.removeListener();
    switchScene("login.fxml", 575, 575);
    SecurityController.setUser(null);
  }

  public static void logoutUser() {
    App.LOG.info("Session timeout, user logged out.");
    FireBase.getReference().child("NOTIFICATIONS").removeEventListener(notifs.getListener());
    try {
      mainBox
          .getScene()
          .setRoot(
              FXMLLoader.load(Objects.requireNonNull(App.class.getResource("views/login.fxml"))));
      AppController.showError("Session Timed Out");
    } catch (Exception e) {
    }
    SecurityController.setUser(null);
  }

  @FXML
  void switchToExtPatientTransport() {
    swapPage("externalPatientTransport", "External Patient Transport");
  }

  @FXML
  void switchToEmployeeManager() {
    swapPage("employees", "Employee Manager");
  }

  @FXML
  void switchToAboutUs() {
    swapPage("aboutUs", "About Us");
  }

  @FXML
  void switchToAPI() {
    swapPage("apiLandingPage", "API Landing Page");
  }

  @FXML
  void switchToEquipment() {
    swapPage("equipment", "Equipment Delivery");
  }

  @FXML
  void switchToLabRequest() {
    swapPage("labRequest", "Lab Request");
  }

  @FXML
  void switchToMap() {
    swapPage("locationMap", "Interactive Map");
  }

  @FXML
  void switchToMapDashboard() {
    swapPage("mapDashboard", "Map Dashboard");
  }

  @FXML
  void switchToMeal() {
    swapPage("meal", "Patient Meal Delivery Portal");
  }

  @FXML
  void switchToMedicine() {
    swapPage("medicine", "Medication Delivery");
  }

  @FXML
  void switchToPatientTransport() {
    swapPage("patientTransport", "Internal Patient Transportation");
  }

  @FXML
  void switchToSanitation() {
    swapPage("sanitation", "Sanitation Services");
  }

  @FXML
  void switchToSecurity() {
    swapPage("security", "Security Services");
  }

  @FXML
  void switchToLanguage() {
    swapPage("language", "Interpreter Request");
  }

  @FXML
  void switchToEquipSani() {
    swapPage("cleanEquipment", "Equipment Sanitation");
  }

  @FXML
  void switchToCredits() {
    swapPage("credits", "Credits");
  }

  @FXML
  void switchToDB() {
    swapPage("backendInfoDisp", "Backend Information Display");
  }

  @FXML
  void goToServicePage() {
    swapPage("serviceRequestPage", "Service Page");
  }

  @FXML
  void switchToUserRequests() {
    swapPage("UserRequests", "User Requests");
  }

  @FXML
  private void updateWeather() {
    weather.update();
  }

  private void menuSlider(VBox slider, JFXHamburger burg, JFXHamburger burgBack) {
    slider.setTranslateX(-225);
    burg.setOnMouseClicked(
        event -> {
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToX(0);
          slide.play();

          slider.setTranslateX(-225);

          slide.setOnFinished(
              (ActionEvent e) -> {
                burg.setVisible(false);
                burgBack.setVisible(true);
              });
        });

    burgBack.setOnMouseClicked(e -> closeSlider());
  }

  public void closeSlider() {
    TranslateTransition slide = new TranslateTransition();
    slide.setDuration(Duration.seconds(0.4));
    slide.setNode(slider);
    slide.setToX(-225);
    slide.play();

    slider.setTranslateX(0);

    slide.setOnFinished(
        (ActionEvent e) -> {
          burg.setVisible(true);
          burgBack.setVisible(false);
        });
  }

  // TODO: Remove this or move it
  protected List<String> getAllLongNames() {
    return new ArrayList<>();
  }

  private long startTime;
  private int count = 0;

  public void easterEgg() throws IOException {
    if (count == 0) {
      startTime = System.currentTimeMillis();
    }
    count++;
    if ((System.currentTimeMillis() - startTime) > 10000) {
      count = 0;
    }
    if (count == 10 & (System.currentTimeMillis() - startTime) < 10000) {
      count = 0;
      swapPage("easterEgg", "Dr. Mario");
    }
  }

  private final List<KeyCode> easterEggSequence = new ArrayList<>();
  private int easterEggInd = 0;

  private void initSequence() {
    easterEggSequence.add(KeyCode.UP);
    easterEggSequence.add(KeyCode.UP);
    easterEggSequence.add(KeyCode.DOWN);
    easterEggSequence.add(KeyCode.DOWN);
    easterEggSequence.add(KeyCode.LEFT);
    easterEggSequence.add(KeyCode.RIGHT);
    easterEggSequence.add(KeyCode.LEFT);
    easterEggSequence.add(KeyCode.RIGHT);
    easterEggSequence.add(KeyCode.B);
    easterEggSequence.add(KeyCode.A);
    easterEggSequence.add(KeyCode.ENTER);
  }

  @FXML
  public void konami(KeyEvent e) {
    if (e.getCode().equals(easterEggSequence.get(easterEggInd))) {
      System.out.println(e.getCode() + " : " + easterEggSequence.get(easterEggInd));
      easterEggInd++;
      if (easterEggInd == easterEggSequence.size()) {
        easterEggInd = 0;
        try {
          //          switchScene("konami.fxml", 700, 500);
          MinesweeperZN ms = new MinesweeperZN();
          ms.begin(new Stage());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    } else {
      easterEggInd = 0;
    }
  }

  /* Hover Animations for buttons */
  @FXML
  private void hoveredBurger(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(8, 67, 154, 255);
    Color backgroundStart = new Color(255, 255, 255, 0);
    Color backgroundEnd = new Color(255, 255, 255, 192);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  private void unhoveredBurger(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(8, 67, 154, 255);
    Color textEnd = new Color(255, 255, 255, 255);
    Color backgroundEnd = new Color(255, 255, 255, 0);
    Color backgroundStart = new Color(255, 255, 255, 192);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }
}

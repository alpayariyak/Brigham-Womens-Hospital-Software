package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.controllers.homePage.AccountHandler;
import edu.wpi.DapperDaemons.entities.Notification;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class UserHomeController extends ParentController {

  // account profile
  @FXML private Circle profilePic;
  @FXML private Text accountName;
  @FXML private Text accountUserName;
  @FXML private Label email;
  @FXML private Label employeeID;
  @FXML private Circle home;
  @FXML private Circle accountSettings;
  @FXML private Circle security;
  @FXML private Circle about;

  // buttons
  @FXML private Pane reqContainer;
  @FXML private ImageView reqImage;
  @FXML private Pane notContainer;
  @FXML private ImageView notImage;
  @FXML private Pane settingContainer;
  @FXML private ImageView settingImage;
  @FXML private Label requestNum;
  @FXML private Label notificationNum;
  @FXML private Circle requestNot;
  @FXML private Circle notificationNot;

  // switch pages
  @FXML
  public void openAboutUs() {
    swapPage("aboutUs", "About Us");
  }

  @FXML
  public void goUserHome() {
    swapPage("NewUserHome", "User Home");
  }

  @FXML
  public void openAccountSettings() {
    swapPage("NewUserSettings", "User Account Settings");
  }

  @FXML
  public void openUserSecurity() {
    swapPage("NewUserSecurity", "User Security");
  }

  @FXML // TODO: make notifications page?
  public void openNotifications() {
    swapPage("notificationsPage", "Notifications");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //    super.initialize(location, resources);

    initButtons();

    // set employee name and username
    String employeeName =
        SecurityController.getUser().getFirstName()
            + " "
            + SecurityController.getUser().getLastName();
    accountName.setText(employeeName);

    accountUserName.setText(DAOFacade.getUserAccount().getAttribute(1));

    // set profile picture
    new AccountHandler(accountName, profilePic);

    //    profilePic.setFill(
    //        new ImagePattern(
    //            new Image(
    //                Objects.requireNonNull(
    //                    getClass()
    //                        .getClassLoader()
    //                        .getResourceAsStream(
    //                            "edu/wpi/DapperDaemons/profilepictures/"
    //                                + SecurityController.getUser().getNodeID()
    //                                + ".png")))));

    // set email
    email.setText(DAOFacade.getUserAccount().getAttribute(7));

    // set notifications
    String empID = DAOFacade.getUserAccount().getAttribute(2);
    int reqNum = getNumRequests(empID);
    int notNum = getNumNotifications(empID);
    if (reqNum != 0) {
      requestNum.setVisible(true);
      requestNot.setVisible(true);
      requestNum.setText("" + reqNum);
    } else {
      requestNum.setVisible(false);
      requestNot.setVisible(false);
    }
    if (notNum != 0) {
      notificationNum.setVisible(true);
      notificationNot.setVisible(true);
      notificationNum.setText("" + notNum);
    } else {
      notificationNum.setVisible(false);
      notificationNot.setVisible(false);
    }

    // set employeeID
    employeeID.setText(empID);
  }

  private int getNumRequests(String employeeID) {
    int numReq = 0;
    List<Request> requests = DAOFacade.getAllRequests();
    for (Request req : requests) {
      if (req.getAssigneeID().equals(employeeID)) {
        numReq++;
      }
    }
    return numReq;
  }

  private int getNumNotifications(String employeeID) {
    Map<String, Notification> notifications = DAOPouch.getNotificationDAO().filter(2, employeeID);
    return notifications.size();
  }

  private void initButtons() {
    bindImage(reqImage, reqContainer);
    bindImage(notImage, notContainer);
    bindImage(settingImage, settingContainer);
  }

  /* Hover Animations for buttons */
  /* Animations */
  @FXML
  void hoveredCenterButton(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(0, 0, 0, 255);
    Color textEnd = new Color(0, 0, 0, 255);
    Color backgroundStart = new Color(255, 255, 255, 255);
    Color backgroundEnd = new Color(255, 255, 255, 192);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredCenterButton(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(0, 0, 0, 255);
    Color textEnd = new Color(0, 0, 0, 255);
    Color backgroundStart = new Color(255, 255, 255, 255);
    Color backgroundEnd = new Color(255, 255, 255, 192);
    AnimationHelper.slideNodeWithText(
        node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }

  @FXML
  void hoveredSideButton1(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
    AnimationHelper.slideNodeWithText(
        home, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredSideButton1(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
    AnimationHelper.slideNodeWithText(
        home, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }

  @FXML
  void hoveredSideButton2(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
    AnimationHelper.slideNodeWithText(
        accountSettings, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredSideButton2(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
    AnimationHelper.slideNodeWithText(
        accountSettings, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }

  @FXML
  void hoveredSideButton3(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
    AnimationHelper.slideNodeWithText(
        security, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredSideButton3(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
    AnimationHelper.slideNodeWithText(
        security, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }

  @FXML
  void hoveredSideButton4(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
    AnimationHelper.slideNodeWithText(
        about, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredSideButton4(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(255, 255, 255, 255);
    Color textEnd = new Color(5, 157, 167, 255);
    Color backgroundStart = new Color(5, 157, 167, 255);
    Color backgroundEnd = new Color(5, 157, 167, 191);
    AnimationHelper.slideNodeWithText(
        node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
    AnimationHelper.slideNodeWithText(
        about, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }
}

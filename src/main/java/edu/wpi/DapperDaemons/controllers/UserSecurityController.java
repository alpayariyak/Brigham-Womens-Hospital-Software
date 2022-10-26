package edu.wpi.DapperDaemons.controllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.controllers.homePage.AccountHandler;
import edu.wpi.DapperDaemons.entities.Account;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class UserSecurityController extends ParentController {

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

  // security page
  @FXML private Label securityLevel;
  @FXML private TextField oldPasswordBox;
  @FXML private TextField newPasswordBox;
  @FXML private TextField numberBox;
  @FXML private JFXComboBox<String> type2FABox;

  // switch pages
  @FXML
  public void openAboutUs(ActionEvent event) {
    swapPage("aboutUs", "About Us");
  }

  @FXML
  public void goUserHome(ActionEvent event) {
    swapPage("NewUserHome", "User Home");
  }

  @FXML
  public void openAccountSettings(ActionEvent event) {
    swapPage("NewUserSettings", "User Account Settings");
  }

  @FXML
  public void openUserSecurity(ActionEvent event) {
    swapPage("NewUserSecurity", "User Security");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //    super.initialize(location, resources);

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

    // set security access level
    securityLevel.setText(Integer.toString(SecurityController.getUser().getSecurityClearance()));

    // set phone number
    String number = DAOFacade.getUserAccount().getAttribute(4);
    numberBox.setText(
        "+1 "
            + number.substring(0, 2)
            + "-"
            + number.substring(3, 5)
            + "-"
            + number.substring(6, 9));

    // set types of 2FA types
    type2FABox.setItems(FXCollections.observableArrayList("SMS", "rfid", "none"));
    type2FABox.setValue(DAOFacade.getUserAccount().getAttribute(6));
  }

  public void onRequestNewAccess() {
    // TODO: come back maybe
  }

  // TODO: get password to work?
  public void onSaveChanges() {
    // set new password
    /*String newPassword = newPasswordBox.getText();
    String oldPassword = oldPasswordBox.getText();
    try {
      Account toChange = DAOPouch.getAccountDAO().get(SecurityController.getUser().getNodeID());
      if (toChange.checkPassword(oldPassword)) {
        toChange.setAttribute(3, newPassword);
        DAOPouch.getAccountDAO().update(toChange);
        System.out.println("Password Reset");
      } else System.out.println("Could not reset password");
    } catch (SQLException e) {
      throw new RuntimeException();
    } */

    // set new phone number
    String newNumber = numberBox.getText();
    Account toChange = DAOFacade.getUserAccount();
    toChange.setAttribute(4, newNumber);
    DAOPouch.getAccountDAO().update(toChange);
    System.out.println("New Phone Number Reset");

    // set 2FA type
    if (type2FABox.getValue() != null && !type2FABox.getValue().equals("")) {
      String newTwoFactor = type2FABox.getValue();
      toChange.setAttribute(6, newTwoFactor);
      DAOPouch.getAccountDAO().update(toChange);
    }
  }

  public void onReset() {
    // set password
    /*oldPasswordBox.setText("");
    newPasswordBox.setText("");*/

    // reset phone number
    String number = DAOFacade.getUserAccount().getAttribute(4);
    numberBox.setText(
        "+1 "
            + number.substring(0, 2)
            + "-"
            + number.substring(3, 5)
            + "-"
            + number.substring(6, 9));

    // reset type of 2FA
    type2FABox.setValue("");
  }

  /* Animations */
  @FXML
  void hoveredCenterButton(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(0, 0, 0, 255);
    Color textEnd = new Color(0, 0, 0, 255);
    Color backgroundStart = new Color(5, 47, 146, 0);
    Color backgroundEnd = new Color(5, 47, 146, 128);
    AnimationHelper.slideNodeWithText(
        node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredCenterButton(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(0, 0, 0, 255);
    Color textEnd = new Color(0, 0, 0, 255);
    Color backgroundStart = new Color(5, 47, 146, 0);
    Color backgroundEnd = new Color(5, 47, 146, 128);
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

  @FXML
  void hoveredCancel(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(129, 160, 207, 255);
    Color textEnd = new Color(255, 255, 255, 255);
    Color backgroundStart = new Color(129, 160, 207, 0);
    Color backgroundEnd = new Color(129, 160, 207, 255);
    AnimationHelper.fadeNodeWithText(node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredCancel(MouseEvent event) {
    Node node = (Node) event.getSource();
    Color textStart = new Color(129, 160, 207, 255);
    Color textEnd = new Color(255, 255, 255, 255);
    Color backgroundStart = new Color(129, 160, 207, 0);
    Color backgroundEnd = new Color(129, 160, 207, 255);
    AnimationHelper.fadeNodeWithText(node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }
}

package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.APIConverters.ExternalReqConverter;
import edu.wpi.DapperDaemons.APIConverters.InternalReqConverter;
import edu.wpi.DapperDaemons.APIConverters.SanitationReqConverter;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.Patient;
import edu.wpi.DapperDaemons.entities.requests.PatientTransportRequest;
import edu.wpi.DapperDaemons.entities.requests.SanitationRequest;
import edu.wpi.cs3733.D22.teamB.api.DatabaseController;
import edu.wpi.cs3733.D22.teamB.api.Request;
import edu.wpi.cs3733.D22.teamD.API.*;
import edu.wpi.cs3733.D22.teamD.entities.EmployeeObj;
import edu.wpi.cs3733.D22.teamD.entities.LocationObj;
import edu.wpi.cs3733.D22.teamD.request.SanitationIRequest;
import edu.wpi.cs3733.D22.teamZ.api.API;
import edu.wpi.cs3733.D22.teamZ.api.entity.ExternalTransportRequest;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class APILandingController implements Initializable {

  // Team D JFX
  @FXML private TextField teamDLoc;
  @FXML private Label errorLabel;
  @FXML private Button dSave;
  private static String destIDTeamD;

  // Team Z JFX
  @FXML private TextField zDest;
  @FXML private Label zErrorLabel;
  @FXML private TextField zOrigin;
  @FXML private Button zSave;
  private static String teamZOriginID;
  private static String teamZDestinationID;

  // Team  JFX
  @FXML private Button bSave;
  @FXML private Label bErrorLabel;
  @FXML private TextField bPatient;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // Global init
    saveToDatabase();

    // Team D API Init
    teamDLoc.setText("");
    errorLabel.setText("");
    destIDTeamD = "null";
    dSave.setVisible(false);

    // Team Z API Init
    zDest.setText("");
    zOrigin.setText("");
    zErrorLabel.setText("");
    teamZOriginID = "null";
    teamZDestinationID = "null";
    zSave.setVisible(false);

    // Team B API Init
    bSave.setVisible(false);
    bErrorLabel.setText("");
    bPatient.setText("");

    EmployeeAPI employeeAPI = new EmployeeAPI();
    for (EmployeeObj employeeObj : employeeAPI.getAllEmployees()) {
      employeeAPI.removeEmployee(employeeObj);
    }
    /*
    SanitationReqAPI sanitationReqAPI = new SanitationReqAPI();
    for (SanitationIRequest iReq : sanitationReqAPI.getAllRequests()) {
      sanitationReqAPI.deleteRequest(iReq);
    }
    */

    // EmployeeAPI employeeAPI = new EmployeeAPI();
    for (Employee employee : DAOPouch.getEmployeeDAO().getAll().values()) {
      EmployeeObj employeeObj =
          new EmployeeObj(
              employee.getFirstName(),
              employee.getLastName(),
              employee.getDateOfBirth(),
              EmployeeObj.EmployeeType.valueOf(employee.getEmployeeType().toString()),
              employee.getSecurityClearance());
      employeeObj.setNodeID(employee.getNodeID());
      employeeAPI.addEmployee(employeeObj);
    }
  }

  /** Allows for requests submitted by the API to be saved to our database */
  public void saveToDatabase() {
    dSave.setVisible(false);
    zSave.setVisible(false);
    bSave.setVisible(false);
    // databaseSaverTeamD();
    // databaseSaverTeamZ();
    // databaseSaverTeamB();
  }

  /**
   * Checks team D's location database for a given location ID
   *
   * @return true if the location is present in the API database
   */
  public boolean isInLocationDatabase(String locID) {
    LocationAPI locationAPI = new LocationAPI();
    for (LocationObj loc : locationAPI.getAllLocations()) {
      if (loc.getNodeID().equals(locID)) return true;
    }
    return false;
  }

  /**
   * Checks if a given Sanitation Request is in the database
   *
   * @param req request to check
   * @return true if it is in the database
   */
  public boolean checkIfSanitationReqExists(SanitationRequest req) {
    for (SanitationRequest dbReq : DAOPouch.getSanitationRequestDAO().getAll().values()) {
      if (req.getRoomID().equals(dbReq.getRoomID())
          && req.getPriority().equals(dbReq.getPriority())
          && req.getNotes().equals(dbReq.getNotes())
          && req.getAssigneeID().equals(dbReq.getAssigneeID())
          && req.getRequesterID().equals(dbReq.getRequesterID())) return true;
    }
    return false;
  }

  /** Starts Team D Sanitation Request API (ours) */
  public void startTeamDApi() {
    if (!isInLocationDatabase(teamDLoc.getText().trim())) {
      errorLabel.setText("Error Invalid Location ID");
      errorLabel.setTextFill(Paint.valueOf("EF5353"));
      return;
    }
    destIDTeamD = teamDLoc.getText();
    teamDLoc.setText("");
    errorLabel.setText("");
    StartAPI api = new StartAPI();
    try {
      api.run(
          (int) zOrigin.getScene().getWindow().getX(),
          (int) zOrigin.getScene().getWindow().getY(),
          (int) zOrigin.getScene().getWindow().getHeight(),
          (int) zOrigin.getScene().getWindow().getWidth(),
          "edu/wpi/DapperDaemons/assets/buttons.css",
          destIDTeamD);
    } catch (Exception e) {
      errorLabel.setText("Team-D API Broke");
      return;
    }
    errorLabel.setText("You may have unsaved requests!");
    errorLabel.setTextFill(Paint.valueOf("EF5353"));
    dSave.setVisible(true);
  }

  /** Saves all requests (that do not already exist) from the API to the program database */
  public void databaseSaverTeamD() {
    dSave.setVisible(false);
    SanitationReqAPI sanitationReqAPI = new SanitationReqAPI();
    for (SanitationIRequest iReq : sanitationReqAPI.getAllRequests()) {
      if (!checkIfSanitationReqExists(SanitationReqConverter.convert(iReq)))
        DAOPouch.getSanitationRequestDAO().add(SanitationReqConverter.convert(iReq));
    }
    errorLabel.setText("Changes Saved!");
    errorLabel.setTextFill(Paint.valueOf("00FF00"));
  }

  /** Starts Team-Z's External Patient Request */
  public void startTeamZApi() {
    if (!isInLocationDatabase(zDest.getText().trim())
        || !isInLocationDatabase(zOrigin.getText().trim())) {
      zErrorLabel.setText("Error Invalid Location ID");
      zErrorLabel.setTextFill(Paint.valueOf("EF5353"));
      return;
    }
    teamZOriginID = zOrigin.getText();
    teamZDestinationID = zDest.getText();
    zOrigin.setText("");
    zDest.setText("");

    API api = new API();
    try {
      api.run(
          (int) zOrigin.getScene().getWindow().getX(),
          (int) zOrigin.getScene().getWindow().getY(),
          (int) zOrigin.getScene().getWindow().getWidth(),
          (int) zOrigin.getScene().getWindow().getHeight(),
          "edu/wpi/DapperDaemons/assets/teamZAPI.css",
          teamZDestinationID,
          teamZOriginID);
    } catch (Exception e) {
      System.err.println("Team Z's API Broke");
      return;
    }
    zErrorLabel.setText("You may have unsaved requests!");
    zErrorLabel.setTextFill(Paint.valueOf("EF5353"));
    zSave.setVisible(true);
  }

  /** Saves the external transport request from team Z's API */
  public void databaseSaverTeamZ() {
    zSave.setVisible(false);
    API teamZAPI = new API();
    for (ExternalTransportRequest extReq : teamZAPI.getAllExternalTransportRequests()) {
      if (!checkIfPatitentReqExists(
          ExternalReqConverter.convert(extReq, teamZOriginID, teamZDestinationID)))
        DAOPouch.getPatientTransportRequestDAO()
            .add(ExternalReqConverter.convert(extReq, teamZOriginID, teamZDestinationID));
    }
    zErrorLabel.setText("Changes Saved!");
    zErrorLabel.setTextFill(Paint.valueOf("00FF00"));
  }

  /**
   * Checks if a patient transport req already exists
   *
   * @param req request to check
   * @return true if it already exists
   */
  public boolean checkIfPatitentReqExists(PatientTransportRequest req) {
    for (PatientTransportRequest dbReq :
        DAOPouch.getPatientTransportRequestDAO().getAll().values()) {
      if (req.getRoomID().equals(dbReq.getRoomID())
          && req.getNextRoomID().equals(dbReq.getNextRoomID())
          && req.getPatientID().equals(dbReq.getPatientID())) return true;
    }
    return false;
  }

  /** Start team 's Request API */
  public void startTeamBAPI() {
    String patientID = bPatient.getText().trim();
    if (patientID.equals("") || !patInDatabase(patientID)) {
      bErrorLabel.setText("Error Please Input a Valid patient ID");
      bErrorLabel.setTextFill(Paint.valueOf("EF5353"));
      return;
    }
    InternalReqConverter.patientID = patientID;
    bPatient.setText("");

    edu.wpi.cs3733.D22.teamB.api.API teamBAPI = new edu.wpi.cs3733.D22.teamB.api.API();
    try {
      teamBAPI.run(
          (int) zOrigin.getScene().getWindow().getX(),
          (int) zOrigin.getScene().getWindow().getY(),
          (int) zOrigin.getScene().getWindow().getWidth(),
          (int) zOrigin.getScene().getWindow().getHeight(),
          "edu/wpi/DapperDaemons/assets/buttons.css",
          "",
          "");
    } catch (Exception e) {
      System.err.println("Team-B API Broke");
      return;
    }
    bErrorLabel.setText("You may have unsaved requests!");
    bErrorLabel.setTextFill(Paint.valueOf("EF5353"));
    bSave.setVisible(true);
  }

  /** Saves team B's Internal Patient Transport to our patient transport DAO */
  public void databaseSaverTeamB() {
    bSave.setVisible(false);
    DatabaseController databaseController = new DatabaseController();
    for (Request request : databaseController.listRequests()) {
      if (!checkIfPatitentReqExists(InternalReqConverter.convert(request))) {
        DAOPouch.getPatientTransportRequestDAO().add(InternalReqConverter.convert(request));
      }
    }
    bErrorLabel.setText("Changes Saved!");
    bErrorLabel.setTextFill(Paint.valueOf("00FF00"));
  }

  /**
   * Checks if a patient is in the database
   *
   * @param patID patient ID
   * @return true if in the database
   */
  public boolean patInDatabase(String patID) {
    for (Patient patient : DAOPouch.getPatientDAO().getAll().values()) {
      if (patient.getNodeID().equals(patID)) return true;
    }
    return false;
  }
}

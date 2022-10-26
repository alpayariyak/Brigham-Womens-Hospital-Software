package edu.wpi.DapperDaemons.controllers.requestControllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.controllers.ParentController;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.controllers.helpers.AutoCompleteFuzzy;
import edu.wpi.DapperDaemons.controllers.helpers.FuzzySearchComparatorMethod;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.Patient;
import edu.wpi.DapperDaemons.entities.requests.PatientTransportRequest;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.tables.Table;
import edu.wpi.DapperDaemons.tables.TableHelper;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/** Patient Transport Controller UPDATED 4/5/22 12:42 PM */
public class ExternalPatientTransportController extends ParentController {

  /* Table Columns */
  @FXML private TableColumn<PatientTransportRequest, String> ReqID;
  @FXML private TableColumn<PatientTransportRequest, String> Priority;
  @FXML private TableColumn<PatientTransportRequest, String> RoomID;
  @FXML private TableColumn<PatientTransportRequest, String> Requester;
  @FXML private TableColumn<PatientTransportRequest, String> Assignee;
  @FXML private TableColumn<PatientTransportRequest, String> Patient;
  @FXML private TableColumn<PatientTransportRequest, String> Destination;
  @FXML private TableColumn<PatientTransportRequest, String> Status;

  /* Dropdown boxes */
  @FXML private JFXComboBox<String> roomBox;
  @FXML private JFXComboBox<String> priorityIn;

  /* Text Boxes */
  @FXML private TextField patientFirstName;
  @FXML private TextField patientLastName;
  @FXML private DatePicker patientDOB;
  @FXML private TextField notes;
  @FXML private DatePicker dateNeeded;
  @FXML private GridPane table;
  @FXML private HBox header;
  private Table<PatientTransportRequest> t;

  List<String> names;
  // PatientTransportRequestHandler handler = new PatientTransportRequestHandler();

  DAO<PatientTransportRequest> patientTransportRequestDAO =
      DAOPouch.getPatientTransportRequestDAO();
  DAO<edu.wpi.DapperDaemons.entities.Patient> patientDAO = DAOPouch.getPatientDAO();
  DAO<Location> locationDAO = DAOPouch.getLocationDAO();

  /** Initializes the controller objects (After runtime, before graphics creation) */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeInputs();

    t = new Table<>(PatientTransportRequest.class, table, 0, 0);
    createTable();
  }

  private void createTable() {
    //    t.setHeader(header, new ArrayList<>(List.of(new String[] {"Test", "Test", "Test"})));
    List<PatientTransportRequest> reqs =
        new ArrayList<>(DAOPouch.getPatientTransportRequestDAO().getAll().values());
    for (int i = 0; i < reqs.size(); i++) {
      PatientTransportRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }
    t.setRows(reqs);
    t.setListeners(new PatientTransportRequest());
  }

  @FXML
  public void onClearClicked() {
    roomBox.setValue("");
    priorityIn.setValue("");
    patientFirstName.setText("");
    patientLastName.setText("");
    patientDOB.setValue(null);
    notes.setText("");
    dateNeeded.setValue(null);
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(roomBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(priorityIn, new FuzzySearchComparatorMethod());
  }

  @FXML
  public void onSubmitClicked() {

    if (fieldsNonEmpty()) {
      Request.Priority priority = Request.Priority.valueOf(priorityIn.getValue());
      String roomID;
      String requesterID = SecurityController.getUser().getNodeID();
      String assigneeID = "none";
      String patientID;
      String nextRoomID = "";
      Request.RequestStatus status = Request.RequestStatus.REQUESTED;

      String dateStr =
          ""
              + dateNeeded.getValue().getMonthValue()
              + dateNeeded.getValue().getDayOfMonth()
              + dateNeeded.getValue().getYear();

      // Determine if the next Location exists
      ArrayList<Location> locations = new ArrayList<>();
      boolean nextLocationExists = false;
      locations = new ArrayList(locationDAO.getAll().values());
      for (Location l : locations) {
        if (l.getAttribute(7).equals(roomBox.getValue())) {
          nextRoomID = l.getNodeID();
          nextLocationExists = true;
        }
      }
      if (nextLocationExists) {

        // Now Check of the patient exists
        boolean isAPatient = false;
        Patient patient = new Patient();
        patientID =
            patientFirstName.getText()
                + patientLastName.getText()
                + patientDOB.getValue().getMonthValue()
                + patientDOB.getValue().getDayOfMonth()
                + patientDOB.getValue().getYear();

        patient = patientDAO.get(patientID);
        try {
          isAPatient = patient.getFirstName().equals(patientFirstName.getText());
        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        if (isAPatient) {

          // now send request and get back whether it went through.

          roomID = patient.getLocationID();
          boolean hadPermission =
              addItem(
                  new PatientTransportRequest(
                      priority,
                      roomID,
                      requesterID,
                      assigneeID,
                      notes.getText(),
                      patientID,
                      nextRoomID,
                      dateStr));
          if (!hadPermission) {
            // display error that employee does not have permission

            showError("You do not have permission to do this.");
          }
        } else {
          // display error that patient does not exist
          showError("Could not find a patient that matches.");
        }
      } else {
        // display error that location does not exist
        showError("A room with that name does not exist.");
      }
    } else {
      // display error message not all fields filled
      showError("All fields must be filled.");
    }
    onClearClicked();
  }

  public boolean fieldsNonEmpty() {

    return !(roomBox.getValue().equals("")
        || priorityIn.getValue().equals("")
        || patientFirstName.getText().equals("")
        || patientLastName.getText().equals("")
        || patientDOB.getValue() == null
        || dateNeeded.getValue() == null);
  }

  private void initializeInputs() {

    priorityIn.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));
    roomBox.setItems(FXCollections.observableArrayList(DAOFacade.getAllLocationLongNamesExit()));

    // TODO FIGURE OUT WHY THE FUCK THIS SEARCH SHIT DOESNT WORK
    // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHH
    // roomBox.getEditor().setOnKeyPressed(E -> searchRoomsDropDown());

  }

  private boolean addItem(PatientTransportRequest request) {
    boolean hasClearance = false;

    hasClearance = patientTransportRequestDAO.add(request);

    return hasClearance;
  }

  private void searchRoomsDropDown() {

    List<Location> locations = new ArrayList<>();
    ArrayList<String> locationNames = new ArrayList<>();
    String value = roomBox.getValue() + "";

    locations = new ArrayList(locationDAO.search(locationDAO.getAll(), 7, value).values());
    for (Location l : locations) {
      locationNames.add(l.getAttribute(7));
    }
    System.out.println(locationNames);
    roomBox.setValue(String.valueOf(locationNames));
  }

  public void saveToCSV() {
    super.saveToCSV(new PatientTransportRequest(), (Stage) roomBox.getScene().getWindow());
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

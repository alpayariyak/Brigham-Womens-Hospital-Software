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
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.Patient;
import edu.wpi.DapperDaemons.entities.requests.LabRequest;
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
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LabRequestController extends ParentController {

  /* UI Fields */
  @FXML private TextField patientName;
  @FXML private TextField patientLastName;
  @FXML private DatePicker patientDOB;
  @FXML private JFXComboBox<String> priorityChoiceBox;
  @FXML private JFXComboBox<String> procedureComboBox;
  @FXML private TextField notes;
  @FXML private DatePicker dateNeeded;
  @FXML private JFXComboBox<String> assigneeBox;

  /* Lab request DAO */
  private final DAO<LabRequest> labRequestDAO = DAOPouch.getLabRequestDAO();
  private final DAO<Patient> patientDAO = DAOPouch.getPatientDAO();
  private final DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();

  @FXML private GridPane table;
  private Table<LabRequest> t;

  /* Labels */
  @FXML private Label errorLabel;

  /** Initializes the controller objects (After runtime, before graphics creation) */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    onClearClicked();
    LabRequestInitializer init = new LabRequestInitializer();

    init.initializeInputs();
    createTable();
  }

  private void createTable() {
    t = new Table<>(LabRequest.class, table, 0, 0);
    List<LabRequest> reqs = new ArrayList<>(DAOPouch.getLabRequestDAO().getAll().values());

    for (int i = 0; i < reqs.size(); i++) {
      LabRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }

    t.setRows(reqs);
    t.setHeader(
        List.of("Requester", "Assignee", "Procedure", "Room", "Patient", "Status", "Priority"));
    t.setListeners(new LabRequest());
    t.addEnumEditProperty(6, 2, Request.Priority.class);
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(
        priorityChoiceBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(
        procedureComboBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(assigneeBox, new FuzzySearchComparatorMethod());
  }

  @FXML
  public void onClearClicked() {
    procedureComboBox.setValue("");
    patientName.clear();
    patientLastName.clear();
    patientDOB.setValue(null);
    priorityChoiceBox.setValue("");
    notes.setText("");
    dateNeeded.setValue(null);
    assigneeBox.setValue("");
  }

  @FXML
  public void onSubmitClicked() {

    if (allItemsFilled()) {
      String dateStr = "";
      Request.Priority priority = Request.Priority.valueOf(priorityChoiceBox.getValue());
      String roomID = "";
      String requesterID = SecurityController.getUser().getNodeID();
      String patientID =
          patientName.getText()
              + patientLastName.getText()
              + patientDOB.getValue().getMonthValue()
              + patientDOB.getValue().getDayOfMonth()
              + patientDOB.getValue().getYear();
      LabRequest.LabType labType = LabRequest.LabType.valueOf(procedureComboBox.getValue());
      Request.RequestStatus status = Request.RequestStatus.REQUESTED;

      // Check if the patient info points to a real patient
      boolean isAPatient = false;
      Patient patient = new Patient();
      patient = patientDAO.get(patientID);
      try {
        isAPatient = patient.getFirstName().equals(patientName.getText());
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
      if (isAPatient) {
        roomID = patient.getLocationID();
        dateStr =
            ""
                + dateNeeded.getValue().getMonthValue()
                + dateNeeded.getValue().getDayOfMonth()
                + dateNeeded.getValue().getYear();

        if (assigneeBox.getValue().equals("")
            || assigneeBox
                .getValue()
                .equals("Auto Assign")) { // check if the user inputs an assignee
          boolean hadClearance =
              addItem(
                  new LabRequest(
                      priority, roomID, requesterID, notes.getText(), patientID, labType, dateStr));

          if (!hadClearance) {
            //  throw error saying that the user does not have clearance yada yada

            showError("You do not have permission to do this.");
          }
        } else {
          if (DAOPouch.getEmployeeDAO()
              .get(assigneeBox.getValue())
              .getEmployeeType()
              .equals(Employee.EmployeeType.DOCTOR)) {
            boolean hadClearance =
                addItem(
                    new LabRequest(
                        priority,
                        roomID,
                        requesterID,
                        assigneeBox.getValue(),
                        notes.getText(),
                        patientID,
                        labType,
                        dateStr));

            if (!hadClearance) {
              //  throw error saying that the user does not have clearance yada yada

              showError("You do not have permission to do this.");
            }
          } else {
            showError("Invalid Assignee ID!");
          }
        }

      } else {
        // throw error saying that the patient does not exist
        showError("Could not find a patient that matches.");
      }
    } else {
      // throws error saying that all fields must be filled
      showError("All fields must be filled.");
    }
    onClearClicked();
  }

  private boolean allItemsFilled() {
    System.out.println(patientDOB.getValue());
    return !(procedureComboBox.getValue().trim().equals("")
        || patientName.getText().trim().equals("")
        || patientLastName.getText().trim().equals("")
        || patientDOB.getValue() == null
        || priorityChoiceBox.getValue().trim().equals("")
        || dateNeeded.getValue() == null);
  }

  private boolean addItem(LabRequest request) {
    boolean hadClearance = false;
    hadClearance = labRequestDAO.add(request);
    if (hadClearance) {
      //      labReqTable.getItems().add(request);
    }

    return hadClearance;
  }

  /** Saves a given service request to a CSV by opening the CSV window */
  public void saveToCSV() {
    super.saveToCSV(new LabRequest(), (Stage) patientName.getScene().getWindow());
  }

  private class LabRequestInitializer {
    private void initializeInputs() {
      procedureComboBox.setItems(
          FXCollections.observableArrayList(TableHelper.convertEnum(LabRequest.LabType.class)));
      priorityChoiceBox.setItems(
          FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));

      List<Employee> employees = new ArrayList<>(employeeDAO.filter(5, "DOCTOR").values());
      List<String> employeeNames = new ArrayList<>();
      employeeNames.add("Auto Assign");
      for (Employee employee : employees) employeeNames.add(employee.getNodeID());
      assigneeBox.setItems(FXCollections.observableArrayList(employeeNames));
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

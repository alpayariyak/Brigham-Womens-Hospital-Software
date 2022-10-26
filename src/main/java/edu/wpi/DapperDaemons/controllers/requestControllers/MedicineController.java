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
import edu.wpi.DapperDaemons.entities.requests.MedicineRequest;
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
import javafx.stage.Stage;

public class MedicineController extends ParentController {
  @FXML private GridPane table;

  @FXML private JFXComboBox<String> medNameIn;
  @FXML private TextField quantityIn;
  @FXML private JFXComboBox<String> priorityIn;
  @FXML private TextField patientName;
  @FXML private TextField patientLastName;
  @FXML private DatePicker patientDOB;
  @FXML private TextField notes;
  @FXML private DatePicker dateNeeded;
  @FXML private JFXComboBox<String> assigneeBox;

  private final DAO<MedicineRequest> medicineRequestDAO = DAOPouch.getMedicineRequestDAO();
  private final DAO<Patient> patientDAO = DAOPouch.getPatientDAO();
  private Table<MedicineRequest> t;
  private final DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    medNameIn.setItems(FXCollections.observableArrayList("Morphine", "OxyCodine", "Lexapro"));
    priorityIn.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));

    List<Employee> employees = new ArrayList<>(employeeDAO.filter(5, "NURSE").values());
    List<String> employeeNames = new ArrayList<>();
    employeeNames.add("Auto Assign");
    for (Employee employee : employees) employeeNames.add(employee.getNodeID());
    assigneeBox.setItems(FXCollections.observableArrayList(employeeNames));

    createTable();
    onClearClicked();
  }

  private void createTable() {
    List<MedicineRequest> reqs =
        new ArrayList<>(DAOPouch.getMedicineRequestDAO().getAll().values());

    for (int i = 0; i < reqs.size(); i++) {
      MedicineRequest req = reqs.get(i);
      //      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }

    t = new Table<>(MedicineRequest.class, table, 0, 0);
    t.setRows(reqs);
    t.setHeader(
        List.of(
            "Requester",
            "Assignee",
            "Room",
            "Patient",
            "Name",
            "Quantity",
            "Date needed",
            "Priority"));
    t.setListeners(new MedicineRequest());
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
    t.addEnumEditProperty(7, 2, Request.Priority.class);
  }

  /** Clears the fields when clicked */
  @FXML
  public void onClearClicked() {
    medNameIn.setValue("");
    quantityIn.clear();
    priorityIn.setValue("");
    patientName.clear();
    patientLastName.clear();
    patientDOB.setValue(null);
    notes.setText("");
    dateNeeded.setValue(null);
    assigneeBox.setValue("");
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(priorityIn, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(medNameIn, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(assigneeBox, new FuzzySearchComparatorMethod());
  }

  @FXML
  void onEditPriority(TableColumn.CellEditEvent<MedicineRequest, String> event) {
    event.getRowValue().setPriority(Request.Priority.valueOf(event.getNewValue()));
    boolean worked = false;
    try {
      medicineRequestDAO.update(event.getRowValue());
    } catch (Exception e) {

    }
  }

  /**
   * first checks if the request is formed correctly, then checks for user clearance, then sends the
   * request
   */
  @FXML
  public void onSubmitClicked() {

    // declare all request fields

    // Check if all fields have a value if so, proceed
    if (!(medNameIn.getValue().trim().equals("")
        || quantityIn.getText().trim().equals("")
        || priorityIn.getValue().equals("")
        || patientName.getText().equals("")
        || patientLastName.getText().equals("")
        || patientDOB.getValue() == null
        || dateNeeded.getValue() == null)) {

      Request.Priority priority;
      int quantity = 0;
      String medName;
      String patientID;
      String requesterID;
      String assigneeID;
      String roomID;

      String dateStr =
          ""
              + dateNeeded.getValue().getMonthValue()
              + dateNeeded.getValue().getDayOfMonth()
              + dateNeeded.getValue().getYear();

      // check if quantity is an int and not letters
      boolean isAnInt = true;
      try {
        quantity = Integer.parseInt(quantityIn.getText());
      } catch (Exception e) {
        e.printStackTrace();
        isAnInt = false;
      }
      if (isAnInt) {

        // check if the patient info points to a real patient
        boolean isAPatient = false;
        patientID =
            patientName.getText()
                + patientLastName.getText()
                + patientDOB.getValue().getMonthValue()
                + patientDOB.getValue().getDayOfMonth()
                + patientDOB.getValue().getYear();
        Patient patient = new Patient();
        patient = patientDAO.get(patientID);
        try {
          isAPatient = patient.getFirstName().equals(patientName.getText());
        } catch (NullPointerException e) {
          // dont need to print stacktrace here, if its null then nothing happens
        }
        if (isAPatient) {

          // now we can create the request and send it

          roomID = patient.getLocationID();
          requesterID = SecurityController.getUser().getNodeID();
          priority = Request.Priority.valueOf(priorityIn.getValue());
          medName = medNameIn.getValue();

          if (assigneeBox.getValue().equals("")
              || assigneeBox
                  .getValue()
                  .equals("Auto Assign")) { // check if the user inputs an assignee
            boolean wentThrough =
                addItem(
                    new MedicineRequest(
                        priority,
                        roomID,
                        requesterID,
                        notes.getText(),
                        patientID,
                        medName,
                        quantity,
                        dateStr));

            if (!wentThrough) {
              // throw error saying no clearance allowed
              showError("You do not have permission to do this.");
            }
          } else {
            if (DAOPouch.getEmployeeDAO()
                .get(assigneeBox.getValue())
                .getEmployeeType()
                .equals(Employee.EmployeeType.NURSE)) {
              boolean wentThrough =
                  addItem(
                      new MedicineRequest(
                          priority,
                          roomID,
                          requesterID,
                          assigneeBox.getValue(),
                          notes.getText(),
                          patientID,
                          medName,
                          quantity,
                          dateStr));

              if (!wentThrough) {

                // throw error saying no clearance allowed
                showError("You do not have permission to do this.");
              }
            } else {
              showError("Invalid Assignee ID!");
            }
          }

        } else {
          // throw an error message saying that the patient doesnt exist
          showError("Could not find a patient that matches.");
        }
      } else {
        // throw error message about quantity not being a number
        showError("Please enter a valid number.");
      }
    } else {
      //  throw error message about empty fields
      showError("All fields must be filled.");
    }

    onClearClicked();
  }
  /** Saves a given service request to a CSV by opening the CSV window */
  public void saveToCSV() {
    super.saveToCSV(new MedicineRequest(), (Stage) patientName.getScene().getWindow());
  }

  @FXML
  private boolean addItem(MedicineRequest request) {
    boolean hasClearance = false;
    hasClearance = medicineRequestDAO.add(request);

    return hasClearance;
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

package edu.wpi.DapperDaemons.controllers.requestControllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.controllers.ParentController;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.controllers.helpers.AutoCompleteFuzzy;
import edu.wpi.DapperDaemons.controllers.helpers.FuzzySearchComparatorMethod;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.requests.MedicalEquipmentRequest;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** Equipment Request UI Controller UPDATED 4/5/22 12:30AM */
public class EquipmentRequestController extends ParentController {
  /* Sexy MOTHERFUCKING  JFXComboBoxes */
  @FXML private JFXComboBox<String> priorityIn;
  @FXML private JFXComboBox<String> equipmentTypeBox;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private JFXComboBox<String> assigneeBox;
  @FXML private TextField notes;
  @FXML private DatePicker dateNeeded;

  /* DAO Object */
  private final DAO<MedicalEquipmentRequest> medicalEquipmentRequestDAO =
      DAOPouch.getMedicalEquipmentRequestDAO();
  private final DAO<Location> locationDAO = DAOPouch.getLocationDAO();
  private final DAO<MedicalEquipment> medicalEquipmentDAO = DAOPouch.getMedicalEquipmentDAO();
  private final DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();

  @FXML private GridPane table;
  private Table<MedicalEquipmentRequest> t;

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(priorityIn, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(equipmentTypeBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(locationBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(assigneeBox, new FuzzySearchComparatorMethod());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initBoxes();
    createTable();
    onClearClicked();
  }

  private void createTable() {
    t = new Table<>(MedicalEquipmentRequest.class, table, 0, 0);
    List<MedicalEquipmentRequest> reqs =
        new ArrayList<>(DAOPouch.getMedicalEquipmentRequestDAO().getAll().values());
    for (int i = 0; i < reqs.size(); i++) {
      MedicalEquipmentRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }
    t.setRows(reqs);
    t.setHeader(List.of("Requester", "Assignee", "Equip Type", "Room", "Priority"));
    t.setListeners(new MedicalEquipmentRequest());
    t.addEnumEditProperty(4, 2, Request.Priority.class);
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
  }

  public boolean addItem(MedicalEquipmentRequest request) {
    boolean hadClearance = false;

    hadClearance = medicalEquipmentRequestDAO.add(request);
    if (hadClearance) {
      t.addRow(request);
      App.LOG.info("Made a new medical equipment request using the pp!!");
    }
    return hadClearance;
  }

  @FXML
  public void onClearClicked() {
    priorityIn.setValue("");
    equipmentTypeBox.setValue("");
    locationBox.setValue("");
    assigneeBox.setValue("");
    dateNeeded.setValue(null);
    notes.setText("");
  }

  @FXML
  public void onSubmitClicked() {

    // make sure all fields are filled
    if (allFieldsFilled()) {
      // get all the variables ready to go
      Request.Priority priority = Request.Priority.valueOf(priorityIn.getValue());
      String roomID = "";
      String requesterID = SecurityController.getUser().getNodeID();
      MedicalEquipment.EquipmentType equipmentType =
          MedicalEquipment.EquipmentType.valueOf(equipmentTypeBox.getValue());
      MedicalEquipment.CleanStatus cleanStatus = MedicalEquipment.CleanStatus.UNCLEAN;

      String dateStr =
          ""
              + dateNeeded.getValue().getMonthValue()
              + dateNeeded.getValue().getDayOfMonth()
              + dateNeeded.getValue().getYear();

      ArrayList<MedicalEquipment> equipments = new ArrayList<>();
      MedicalEquipment equipment = new MedicalEquipment();
      // is there equipment with that Type?
      boolean equipmentExists = true;

      // get all equipment of that type.
      equipments =
          new ArrayList(
              medicalEquipmentDAO
                  .filter(medicalEquipmentDAO.getAll(), 3, equipmentTypeBox.getValue())
                  .values());

      if (medicalEquipmentDAO
              .filter(equipments, 5, MedicalEquipment.CleanStatus.CLEAN.toString())
              .size()
          != 0) {
        equipment =
            new ArrayList<MedicalEquipment>(
                    (medicalEquipmentDAO.filter(
                            equipments, 5, MedicalEquipment.CleanStatus.CLEAN.toString()))
                        .values())
                .get(0);
      } else if (medicalEquipmentDAO
              .filter(equipments, 5, MedicalEquipment.CleanStatus.INPROGRESS.toString())
              .size()
          != 0) {
        equipment =
            new ArrayList<MedicalEquipment>(
                    medicalEquipmentDAO
                        .filter(equipments, 5, MedicalEquipment.CleanStatus.INPROGRESS.toString())
                        .values())
                .get(0);

      } else if (medicalEquipmentDAO
              .filter(equipments, 5, MedicalEquipment.CleanStatus.UNCLEAN.toString())
              .size()
          != 0) {
        equipment =
            new ArrayList<MedicalEquipment>(
                    medicalEquipmentDAO
                        .filter(equipments, 5, MedicalEquipment.CleanStatus.UNCLEAN.toString())
                        .values())
                .get(0);
      } else {

        equipmentExists = false;
      }
      if (equipmentExists) {

        // check if room exists

        if (assigneeBox.getValue().equals("") || assigneeBox.getValue().equals("Auto Assign")) {
          // Auto assign
          cleanStatus = equipment.getCleanStatus();
          roomID = locationBox.getValue();
          int numCorrectLocations = 0;
          numCorrectLocations = locationDAO.filter(locationDAO.getAll(), 7, roomID).size();
          Location room =
              new ArrayList<Location>(locationDAO.filter(locationDAO.getAll(), 7, roomID).values())
                  .get(0);
          if (numCorrectLocations >= 1) {

            boolean hadClearance =
                addItem(
                    new MedicalEquipmentRequest(
                        priority,
                        room.getNodeID(),
                        requesterID,
                        notes.getText(),
                        equipment.getNodeID(),
                        equipmentType,
                        cleanStatus,
                        dateStr));
            // check if user has permission
            if (!hadClearance) {
              showError("You do not have permission to do this.");
            }

          } else {
            // throw error that room does not exist
            showError("A room with that name does not exist.");
          }
        } else { // Or Check if the input assigneeID exists
          if (DAOPouch.getEmployeeDAO()
              .get(assigneeBox.getValue())
              .getEmployeeType()
              .equals(Employee.EmployeeType.NURSE)) {
            cleanStatus = equipment.getCleanStatus();
            roomID = locationBox.getValue();
            Location room =
                new ArrayList<Location>(
                        locationDAO
                            .filter(new ArrayList<>(locationDAO.getAll().values()), 7, roomID)
                            .values())
                    .get(0);

            int numCorrectLocations = 0;
            numCorrectLocations = locationDAO.filter(locationDAO.getAll(), 7, roomID).size();
            if (numCorrectLocations >= 1) {

              boolean hadClearance =
                  addItem(
                      new MedicalEquipmentRequest(
                          priority,
                          room.getNodeID(),
                          requesterID,
                          assigneeBox.getValue(),
                          notes.getText(),
                          equipment.getNodeID(),
                          equipmentType,
                          cleanStatus,
                          dateStr));
              // check if user has permission
              if (!hadClearance) {
                showError("You do not have permission to do this.");
              }

            } else {
              // throw error that room does not exist
              showError("A room with that name does not exist.");
            }
          } else {
            showError("Invalid Assignee ID!");
          }
        }

      } else {
        // Throw error that no equipment of that type exist

        showError("No equipment of that type exists.");
      }
    } else {
      showError("All fields must be filled.");
    }
    onClearClicked();
  }

  private boolean allFieldsFilled() {
    return !(priorityIn.getValue().equals("")
        || equipmentTypeBox.getValue().equals("")
        || locationBox.getValue().equals("")
        || dateNeeded.getValue() == null);
  }

  public void initBoxes() {
    priorityIn.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));
    equipmentTypeBox.setItems(
        FXCollections.observableArrayList(
            TableHelper.convertEnum(MedicalEquipment.EquipmentType.class)));
    locationBox.setItems(FXCollections.observableArrayList(DAOFacade.getAllLocationLongNames()));

    List<Employee> employees = new ArrayList<>(employeeDAO.filter(5, "NURSE").values());
    List<String> employeeNames = new ArrayList<>();
    employeeNames.add("Auto Assign");
    for (Employee employee : employees) employeeNames.add(employee.getNodeID());
    assigneeBox.setItems(FXCollections.observableArrayList(employeeNames));
  }
  /** Saves a given service request to a CSV by opening the CSV window */
  public void saveToCSV() {
    super.saveToCSV(new MedicalEquipmentRequest(), (Stage) locationBox.getScene().getWindow());
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

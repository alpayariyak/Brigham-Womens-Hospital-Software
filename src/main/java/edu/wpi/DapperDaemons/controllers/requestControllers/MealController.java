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
import edu.wpi.DapperDaemons.entities.Patient;
import edu.wpi.DapperDaemons.entities.requests.MealDeliveryRequest;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.tables.Table;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/** Controller for Meal UI Page UPDATED 4/5/22 at 12:08 AM */
public class MealController extends ParentController {

  /* Table Columns for Request Table */
  @FXML private TableColumn<MealDeliveryRequest, String> reqID;
  @FXML private TableColumn<MealDeliveryRequest, String> priority;
  @FXML private TableColumn<MealDeliveryRequest, String> roomID;
  @FXML private TableColumn<MealDeliveryRequest, String> requester;
  @FXML private TableColumn<MealDeliveryRequest, String> assignee;
  @FXML private TableColumn<MealDeliveryRequest, String> patient;
  @FXML private TableColumn<MealDeliveryRequest, String> entree;
  @FXML private TableColumn<MealDeliveryRequest, String> side;
  @FXML private TableColumn<MealDeliveryRequest, String> drink;
  @FXML private TableColumn<MealDeliveryRequest, String> dessert;

  /* Text Fields */
  @FXML private TextField patientName;
  @FXML private TextField patientLastName;
  @FXML private DatePicker patientDOB;
  @FXML private TextField notes;
  @FXML private DatePicker dateNeeded;

  /* Buttons */
  @FXML private Button clearButton;
  @FXML private Button submitButton;

  /* Dropdown Boxes */
  @FXML private JFXComboBox<String> entreeBox;
  @FXML private JFXComboBox<String> sideBox;
  @FXML private JFXComboBox<String> drinkBox;
  @FXML private JFXComboBox<String> dessertBox;

  private DAO<MealDeliveryRequest> mealDeliveryRequestDAO = DAOPouch.getMealDeliveryRequestDAO();
  private DAO<Patient> patientDAO = DAOPouch.getPatientDAO();
  @FXML private GridPane table;
  @FXML private HBox header;
  private Table<MealDeliveryRequest> t;

  /**
   * Runs at compile time, specified from Initializable interface Sets up meal request table and
   * adds a test value
   *
   * @param location specified at runtime
   * @param resources specified at runtime
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    /* Init Request table */
    initBoxes();
    onClearClicked();
    createTable();
  }

  private void createTable() {
    t = new Table<>(MealDeliveryRequest.class, table, 0, 0);
    List<MealDeliveryRequest> reqs =
        new ArrayList<>(DAOPouch.getMealDeliveryRequestDAO().getAll().values());

    for (int i = 0; i < reqs.size(); i++) {
      MealDeliveryRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }

    t.setRows(reqs);
    t.setHeader(
        List.of(
            "Requester",
            "Assignee",
            "Patient",
            "Room",
            "Entree",
            "Side",
            "Drink",
            "Dessert",
            "Priority"));
    t.setListeners(new MealDeliveryRequest());
    t.addEnumEditProperty(8, 2, Request.Priority.class);
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(entreeBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(sideBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(drinkBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(dessertBox, new FuzzySearchComparatorMethod());
  }
  /** Creates service request, executes when submit button is pressed */
  public void onSubmitClicked() {

    // Check if all inputs are filled
    if (allFilled()) {
      Request.Priority priority = Request.Priority.LOW;
      String roomID;
      String requesterID;
      String patientID;
      String entree = entreeBox.getValue();
      String side = sideBox.getValue();
      String drink = drinkBox.getValue();
      String dessert = dessertBox.getValue();
      // Check if the patient exists
      patientID =
          patientName.getText()
              + patientLastName.getText()
              + patientDOB.getValue().getMonthValue()
              + patientDOB.getValue().getDayOfMonth()
              + patientDOB.getValue().getYear();

      String dateStr =
          ""
              + dateNeeded.getValue().getMonthValue()
              + dateNeeded.getValue().getDayOfMonth()
              + dateNeeded.getValue().getYear();
      Patient patient = new Patient();
      boolean isAPatient = false;
      patient = patientDAO.get(patientID);

      try {
        isAPatient = patient.getFirstName().equals(patientName.getText());
      } catch (NullPointerException e) {
        e.printStackTrace();
      }

      if (isAPatient) {

        // request is formed correctly and the patient exists send it and check for clearance
        roomID = patient.getLocationID();
        requesterID = SecurityController.getUser().getNodeID();

        boolean hadClearance =
            addMealRequest(
                new MealDeliveryRequest(
                    priority,
                    roomID,
                    requesterID,
                    notes.getText(),
                    patientID,
                    entree,
                    side,
                    drink,
                    dessert,
                    dateStr));

        if (!hadClearance) {
          // throw error that user aint got no clearance
          showError("You do not have permission to do this.");
        }

      } else {
        // throw error that patient aint real
        showError("Could not find a patient that matches.");
      }

    } else {
      // throw error that not all fields are filled in
      showError("All fields must be filled.");
    }
    onClearClicked();
  }

  /** clears all options for creating service request, executes when clear button is pressed */
  public void onClearClicked() {
    entreeBox.setValue("");
    sideBox.setValue("");
    drinkBox.setValue("");
    dessertBox.setValue("");
    patientName.clear();
    patientLastName.clear();
    patientDOB.setValue(null);
    dateNeeded.setValue(null);
    notes.setText("");
  }

  /**
   * Adds a meal request to the JFX table view
   *
   * @param request request object to be added
   */
  public boolean addMealRequest(MealDeliveryRequest request) {
    boolean hadClearance = false;

    hadClearance = mealDeliveryRequestDAO.add(request);

    return hadClearance;
  }

  /** Initializes the options for JFX boxes */
  public void initBoxes() {
    entreeBox.setItems(FXCollections.observableArrayList("Pasta", "Sandwich", "Salad", "Steak"));
    sideBox.setItems(FXCollections.observableArrayList("Fries", "Fruit", "Chips", "None"));
    drinkBox.setItems(FXCollections.observableArrayList("Water", "Soda", "Juice", "None"));
    dessertBox.setItems(FXCollections.observableArrayList("IceCream", "Cake", "Milkshake", "None"));
  }

  /**
   * checks if all needed boxes are filled Sam WUZ HERE :p
   *
   * @return true if all the input boxes are filled
   */
  public boolean allFilled() {
    return (!(entreeBox.getValue().equals("")
        || sideBox.getValue().equals("")
        || drinkBox.getValue().equals("")
        || dessertBox.getValue().equals("")
        || patientName.getText().equals("")
        || patientDOB.getValue() == null
        || patientLastName.getText().equals("")
        || dateNeeded.getValue() == null));
  }

  public void saveToCSV() {
    super.saveToCSV(new MealDeliveryRequest(), (Stage) patientName.getScene().getWindow());
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

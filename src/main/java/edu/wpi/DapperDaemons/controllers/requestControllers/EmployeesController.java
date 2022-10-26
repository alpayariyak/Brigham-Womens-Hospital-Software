package edu.wpi.DapperDaemons.controllers.requestControllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.ParentController;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.tables.Table;
import edu.wpi.DapperDaemons.tables.TableHelper;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** Patient Transport Controller UPDATED 4/5/22 12:42 PM */
public class EmployeesController extends ParentController implements Initializable {

  /* Table Object */
  @FXML private TableView<Employee> employees;
  @FXML private GridPane table;

  /*Table Helper */
  private TableHelper<Employee> tableHelper;

  /* Table Columns */
  @FXML private TableColumn<Employee, String> ID;
  @FXML private TableColumn<Employee, String> firstName;
  @FXML private TableColumn<Employee, String> lastName;
  @FXML private TableColumn<Employee, String> dateOfBirth;
  @FXML private TableColumn<Employee, String> type;
  @FXML private TableColumn<Employee, String> clearance;

  /* Dropdown boxes */
  @FXML private JFXComboBox<String> clearanceBox;
  @FXML private JFXComboBox<String> typeBox;

  /* Text Boxes */
  @FXML private TextField employeeFirstName;
  @FXML private TextField employeeLastName;
  @FXML private DatePicker employeeDOB;

  private Table<Employee> t;
  List<String> names;

  /* DAO */
  DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();

  /** Initializes the controller objects (After runtime, before graphics creation) */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeInputs();
    onClearClicked();
    t = new Table<Employee>(Employee.class, table, 0);
    createTable();
  }

  private void createTable() {
    List<Employee> emps = new ArrayList<>(DAOPouch.getEmployeeDAO().getAll().values());
    t.setRows(emps);
    t.setHeader(List.of("ID", "First Name", "Last Name", "D.O.B.", "Dept", "Clearance"));
    t.setListeners(new Employee());
  }

  @FXML
  public void onClearClicked() {
    clearanceBox.setValue("");
    typeBox.setValue("");
    employeeFirstName.setText("");
    employeeLastName.setText("");
    employeeDOB.setValue(null);
  }

  private boolean addItem(Employee request) {
    boolean hadClearance = false;

    hadClearance = employeeDAO.add(request);
    if (hadClearance) {
      employees.getItems().add(request);
    }
    return hadClearance;
  }

  @FXML
  public void onSubmitClicked() {
    if (fieldsNonEmpty()) {
      String firstName = employeeFirstName.getText();
      String lastName = employeeLastName.getText();
      String dob =
          ""
              + employeeDOB.getValue().getMonthValue()
              + employeeDOB.getValue().getDayOfMonth()
              + employeeDOB.getValue().getYear();

      if (!addItem(
          new Employee(
              firstName,
              lastName,
              dob,
              Employee.EmployeeType.valueOf(typeBox.getValue()),
              Integer.parseInt(clearanceBox.getValue())))) {

        showError("you do not have access to this function");
      }

    } else {
      showError("not all fields have been filled");
    }
  }

  public boolean fieldsNonEmpty() {
    return !(clearanceBox.getValue().equals("")
        || typeBox.getValue().equals("")
        || employeeFirstName.getText().equals("")
        || employeeLastName.getText().equals("")
        || employeeDOB.getValue() == null);
  }

  private void initializeTable() {
    tableHelper = new TableHelper<>(employees, 0);
    tableHelper.linkColumns(Employee.class);

    try {
      employees.getItems().addAll(new ArrayList<Employee>(employeeDAO.getAll().values()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void initializeInputs() {

    typeBox.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Employee.EmployeeType.class)));
    clearanceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));

    clearanceBox.setValue("");
    typeBox.setValue("");
    employeeFirstName.setText("");
    employeeLastName.setText("");
  }

  public void saveToCSV() {
    super.saveToCSV(new Employee(), (Stage) employees.getScene().getWindow());
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

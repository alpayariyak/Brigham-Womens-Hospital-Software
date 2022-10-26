package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.helpers.TableListeners;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.Patient;
import edu.wpi.DapperDaemons.tables.Table;
import edu.wpi.DapperDaemons.tables.TableHelper;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/** Controller for Backend Info Page, allows for the database tables to be displayed */
public class BackendInfoController extends ParentController {

  /* Patient Table and Columns */
  @FXML private TableView<Patient> patientsTable;
  private TableHelper<Patient> patientTableHelper;

  /* Location Table and Columns */
  @FXML private TableView<Location> locationsTable;
  private TableHelper<Location> locationTableHelper;

  /* Employee Table and Columns */
  @FXML private TableView<Employee> employeesTable;
  private TableHelper<Employee> employeeTableHelper;

  /* Equipment Table and Columns */
  @FXML private TableView<MedicalEquipment> equipmentTable;
  private TableHelper<MedicalEquipment> equipmentTableHelper;

  /* DAO Objects */
  private DAO<Location> locationDAO = DAOPouch.getLocationDAO();
  private DAO<Patient> patientDAO = DAOPouch.getPatientDAO();
  private DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();
  private DAO<MedicalEquipment> medicalEquipmentDAO = DAOPouch.getMedicalEquipmentDAO();
  @FXML private GridPane table;
  @FXML private GridPane table1;
  @FXML private GridPane table2;
  @FXML private GridPane table3;
  @FXML private HBox header;
  @FXML private HBox header1;
  @FXML private HBox header2;
  @FXML private HBox header3;
  private Table<Patient> t;
  private Table<Employee> t1;
  private Table<Location> t2;
  private Table<MedicalEquipment> t3;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO : The patient DAO is broken :(
    t = new Table<>(Patient.class, table, 0);
    t1 = new Table<>(Employee.class, table1, 0);
    t2 = new Table<>(Location.class, table2, 0);
    t3 = new Table<>(MedicalEquipment.class, table3, 0);
    createTables();
  }

  private void createTables() {
    t.setHeader(new ArrayList<>(List.of(new String[] {"Test", "Test", "Test"})));
    List<Patient> reqs = new ArrayList<>(DAOPouch.getPatientDAO().getAll().values());
    t.setRows(reqs);
    t.setListeners(new Patient());

    t1.setHeader(new ArrayList<>(List.of(new String[] {"Test", "Test", "Test"})));
    List<Employee> reqs1 = new ArrayList<>(DAOPouch.getEmployeeDAO().getAll().values());
    t1.setRows(reqs1);
    t1.setListeners(new Employee());

    t2.setHeader(new ArrayList<>(List.of(new String[] {"Test", "Test", "Test"})));
    List<Location> reqs2 = new ArrayList<>(DAOPouch.getLocationDAO().getAll().values());
    t2.setRows(reqs2);
    t2.setListeners(new Location());

    t3.setHeader(new ArrayList<>(List.of(new String[] {"Test", "Test", "Test"})));
    List<MedicalEquipment> reqs3 =
        new ArrayList<>(DAOPouch.getMedicalEquipmentDAO().getAll().values());
    t3.setRows(reqs3);
    t3.setListeners(new MedicalEquipment());
  }

  private void setListeners() {
    TableListeners.addListener(
        new Location().tableName(),
        TableListeners.eventListener(
            () -> {
              locationsTable.getItems().clear();
              locationsTable.getItems().addAll(new ArrayList<>(locationDAO.getAll().values()));
            }));
    TableListeners.addListener(
        new Employee().tableName(),
        TableListeners.eventListener(
            () -> {
              employeesTable.getItems().clear();
              employeesTable.getItems().addAll(new ArrayList<>(employeeDAO.getAll().values()));
            }));
    TableListeners.addListener(
        new MedicalEquipment().tableName(),
        TableListeners.eventListener(
            () -> {
              equipmentTable.getItems().clear();
              equipmentTable
                  .getItems()
                  .addAll(new ArrayList<>(medicalEquipmentDAO.getAll().values()));
            }));
    TableListeners.addListener(
        new Patient().tableName(),
        TableListeners.eventListener(
            () -> {
              patientsTable.getItems().clear();
              patientsTable.getItems().addAll(new ArrayList<>(patientDAO.getAll().values()));
            }));
  }
}

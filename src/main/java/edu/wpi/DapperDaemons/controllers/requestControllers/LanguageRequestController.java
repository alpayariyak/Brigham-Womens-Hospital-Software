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
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.requests.LanguageRequest;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** Equipment Request UI Controller UPDATED 4/5/22 12:30AM */
public class LanguageRequestController extends ParentController {

  /* Table Object */
  @FXML private TableView<LanguageRequest> languageRequestsTable;

  /* Table Helper */
  private TableHelper<LanguageRequest> tableHelper;

  /* Sexy MOTHERFUCKING  JFXComboBoxes */
  @FXML private JFXComboBox<String> languageBox;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private JFXComboBox<String> priorityIn;
  @FXML private DatePicker dateNeeded;
  @FXML private TextField notes;
  /* Table Columns */
  @FXML private TableColumn<LanguageRequest, String> reqID;
  @FXML private TableColumn<LanguageRequest, String> language;
  @FXML private TableColumn<LanguageRequest, String> roomID;
  @FXML private TableColumn<LanguageRequest, String> requester;
  @FXML private TableColumn<LanguageRequest, String> assignee;

  /* DAO Object */
  private final DAO<LanguageRequest> languageRequestDAO = DAOPouch.getLanguageRequestDAO();
  private final DAO<Location> locationDAO = DAOPouch.getLocationDAO();
  private final DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();
  @FXML private GridPane table;
  private Table<LanguageRequest> t;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initBoxes();
    onClearClicked();
    createTable();
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(languageBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(locationBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(priorityIn, new FuzzySearchComparatorMethod());
  }

  private void createTable() {
    t = new Table<>(LanguageRequest.class, table, 0, 0);
    List<LanguageRequest> reqs =
        new ArrayList<>(DAOPouch.getLanguageRequestDAO().getAll().values());

    for (int i = 0; i < reqs.size(); i++) {
      LanguageRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }

    t.setRows(reqs);
    t.setHeader(List.of("Requester", "Assignee", "Language", "Room", "Priority"));
    t.setListeners(new LanguageRequest());
    t.addEnumEditProperty(4, 2, Request.Priority.class);
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
  }

  @FXML
  public void onClearClicked() {
    languageBox.setValue("");
    locationBox.setValue("");
    priorityIn.setValue("");
    dateNeeded.setValue(null);
    notes.setText("");
  }

  private boolean addItem(LanguageRequest request) {
    boolean hadClearance = false;
    hadClearance = languageRequestDAO.add(request);

    return hadClearance;
  }

  @FXML
  public void onSubmitClicked() {

    // make sure all fields are filled
    if (allFieldsFilled()) {
      String dateRep =
          ""
              + dateNeeded.getValue().getMonthValue()
              + dateNeeded.getValue().getDayOfMonth()
              + dateNeeded.getValue().getYear();
      String requesterID = SecurityController.getUser().getNodeID();
      String roomID =
          (new ArrayList<Location>(
                  DAOPouch.getLocationDAO().filter(7, locationBox.getValue()).values())
              .get(0)
              .getNodeID());

      if (!addItem(
          new LanguageRequest(
              Request.Priority.valueOf(priorityIn.getValue()),
              roomID,
              requesterID,
              notes.getText(),
              LanguageRequest.Language.valueOf(languageBox.getValue()),
              dateRep))) {
        showError("you do not have access to do this");
      }

    } else {
      // TODO uncomment when fixed
      showError("All fields must be filled.");
    }
    onClearClicked();
  }

  private boolean allFieldsFilled() {
    return !(languageBox.getValue().equals("")
        || locationBox.getValue().equals("")
        || dateNeeded.getValue() == null);
  }

  public void initBoxes() {
    languageBox.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(LanguageRequest.Language.class)));
    locationBox.setItems(FXCollections.observableArrayList(DAOFacade.getAllLocationLongNames()));
    priorityIn.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));
  }
  /** Saves a given service request to a CSV by opening the CSV window */
  public void saveToCSV() {
    super.saveToCSV(new LanguageRequest(), (Stage) locationBox.getScene().getWindow());
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

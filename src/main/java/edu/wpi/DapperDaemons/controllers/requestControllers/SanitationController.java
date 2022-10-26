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
import edu.wpi.DapperDaemons.controllers.helpers.TableListeners;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.requests.PatientTransportRequest;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.entities.requests.SanitationRequest;
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

public class SanitationController extends ParentController {

  /* Table Object */
  @FXML private TableView<SanitationRequest> pendingRequests;

  /* Table Helper */
  private TableHelper<SanitationRequest> helper;

  /* Table Columns */
  @FXML private TableColumn<PatientTransportRequest, String> ReqID;
  @FXML private TableColumn<PatientTransportRequest, String> Priority;
  @FXML private TableColumn<PatientTransportRequest, String> RoomID;
  @FXML private TableColumn<PatientTransportRequest, String> Requester;
  @FXML private TableColumn<PatientTransportRequest, String> Assignee;
  @FXML private TableColumn<PatientTransportRequest, String> Service;
  @FXML private TableColumn<PatientTransportRequest, String> Status;

  /* Dropdown Boxes */
  @FXML private JFXComboBox<String> sanitationBox;
  @FXML private JFXComboBox<String> priorityIn;
  @FXML private JFXComboBox<String> locationBox;

  /* Text Field */
  @FXML private TextField notes;
  @FXML private DatePicker dateNeeded;

  private final DAO<SanitationRequest> sanitationRequestDAO = DAOPouch.getSanitationRequestDAO();
  private final DAO<Location> locationDAO = DAOPouch.getLocationDAO();
  @FXML private GridPane table;
  private Table<SanitationRequest> t;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    onClearClicked();
    initializeInputs();
    createTable();
  }

  private void createTable() {
    t = new Table<>(SanitationRequest.class, table, 0, 0);
    List<SanitationRequest> reqs =
        new ArrayList<>(DAOPouch.getSanitationRequestDAO().getAll().values());

    for (int i = 0; i < reqs.size(); i++) {
      SanitationRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }
    t.setRows(reqs);
    t.setHeader(List.of("Requester", "Assignee", "Type", "Room", "Priority"));
    t.setListeners(new SanitationRequest());
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
    t.addEnumEditProperty(4, 2, Request.Priority.class);
  }

  private void setListeners() {
    TableListeners.addListener(
        new SanitationRequest().tableName(),
        TableListeners.eventListener(
            () -> {
              pendingRequests.getItems().clear();
              pendingRequests
                  .getItems()
                  .addAll(new ArrayList<>(sanitationRequestDAO.getAll().values()));
            }));
  }

  /** clear the current information * */
  @FXML
  public void onClearClicked() {
    sanitationBox.setValue("");
    priorityIn.setValue("");
    locationBox.setValue("");
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(sanitationBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(locationBox, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(priorityIn, new FuzzySearchComparatorMethod());
  }
  /** What happens when the submit button is clicked * */
  @FXML
  public void onSubmitClicked() {

    if (allFieldsFilled()) {
      Request.Priority priority = Request.Priority.valueOf(priorityIn.getValue());
      String roomID = locationBox.getValue();
      String requesterID = SecurityController.getUser().getNodeID();
      String assigneeID = "none";
      String sanitationType = sanitationBox.getValue().toString();
      Request.RequestStatus status = Request.RequestStatus.REQUESTED;

      String dateStr =
          ""
              + dateNeeded.getValue().getMonthValue()
              + dateNeeded.getValue().getDayOfMonth()
              + dateNeeded.getValue().getYear();

      /*Make sure the room exists*/
      boolean isALocation = false;
      Location location = new Location();
      ArrayList<Location> locations = new ArrayList<>();
      locations = new ArrayList(locationDAO.getAll().values());

      location = new ArrayList<Location>(locationDAO.filter(locations, 7, roomID).values()).get(0);

      isALocation = location.getAttribute(7).equals(roomID);
      if (isALocation) {

        boolean hadClearance =
            addItem(
                new SanitationRequest(
                    priority,
                    location.getAttribute(1),
                    requesterID,
                    notes.getText(),
                    sanitationType,
                    dateStr));

        if (!hadClearance) {
          // throw error saying that the user does not have permission to make the request.
          showError("You do not have permission to do this.");
        }
      } else {
        // throw an error that the location does not exist
        showError("A room with that name does not exist.");
      }
    } else {
      //  throw error message that all fields need to be filled
      showError("All fields must be filled.");
    }
    // clear the fields
    onClearClicked();
  }

  private void initializeTable() {
    helper = new TableHelper<>(pendingRequests, 0);
    helper.linkColumns(SanitationRequest.class);
  }

  private void initializeInputs() {
    priorityIn.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));
    sanitationBox.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(SanitationTypes.class)));

    locationBox.setItems((FXCollections.observableArrayList(DAOFacade.getAllLocationLongNames())));

    // locationBox.getItems().removeAll();
  }

  private boolean allFieldsFilled() {
    return !((sanitationBox.getValue().equals(""))
        || priorityIn.getValue().equals("")
        || locationBox.getValue().equals("")
        || dateNeeded.getValue() == null);
  }

  /** Adds new sanitationRequest to table of pending requests * */
  private boolean addItem(SanitationRequest request) {
    boolean hasClearance = false;
    hasClearance = sanitationRequestDAO.add(request);

    return hasClearance;
  }

  public enum SanitationTypes {
    MoppingSweeping,
    Sterilize,
    Trash,
    BioHazard;
  }

  /** Saves a given service request to a CSV by opening the CSV window */
  public void saveToCSV() {
    super.saveToCSV(new SanitationRequest(), (Stage) locationBox.getScene().getWindow());
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

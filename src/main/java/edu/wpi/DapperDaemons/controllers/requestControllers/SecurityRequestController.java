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
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.entities.requests.SecurityRequest;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/** Equipment Request UI Controller UPDATED 4/5/22 12:30AM */
public class SecurityRequestController extends ParentController {

  /* Sexy MOTHERFUCKING  JFXComboBoxes */
  @FXML private JFXComboBox<String> priorityIn;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private DatePicker dateNeeded;
  @FXML private TextField notes;

  /* DAO Object */
  private DAO<SecurityRequest> securityRequestDAO = DAOPouch.getSecurityRequestDAO();
  private DAO<Location> locationDAO = DAOPouch.getLocationDAO();
  @FXML private GridPane table;
  @FXML private HBox header;
  private Table<SecurityRequest> t;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //        super.initialize(location, resources);
    initBoxes();
    //    bindImage(BGImage, BGContainer);

    onClearClicked();
    createTable();
  }

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(locationBox, new FuzzySearchComparatorMethod());
  }

  private void createTable() {
    t = new Table<>(SecurityRequest.class, table, 0, 0);
    List<SecurityRequest> reqs =
        new ArrayList<>(DAOPouch.getSecurityRequestDAO().getAll().values());

    for (int i = 0; i < reqs.size(); i++) {
      SecurityRequest req = reqs.get(i);
      System.out.println(req.getNodeID());
      if (req.getStatus().equals(Request.RequestStatus.COMPLETED)
          || req.getStatus().equals(Request.RequestStatus.CANCELLED)) {
        reqs.remove(i);
        i--;
      }
    }

    t.setRows(reqs);
    t.setHeader(List.of("Requester", "Assignee", "Room", "Priority"));
    t.setListeners(new SecurityRequest());
    t.addEnumEditProperty(3, 2, Request.Priority.class);
    t.addDropDownEditProperty(1, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
  }

  public boolean addItem(SecurityRequest request) {
    boolean hadClearance = true;

    hadClearance = securityRequestDAO.add(request);
    if (hadClearance) {
      t.addRow(request);
    }
    return hadClearance;
  }

  @FXML
  public void onClearClicked() {
    priorityIn.setValue("");
    locationBox.setValue("");
    dateNeeded.setValue(null);
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
      String assignee = "none";
      String roomID =
          (new ArrayList<Location>(
                  DAOPouch.getLocationDAO().filter(7, locationBox.getValue()).values())
              .get(0)
              .getNodeID());
      addItem(
          new SecurityRequest(
              Request.Priority.valueOf(priorityIn.getValue()),
              roomID,
              requesterID,
              notes.getText(),
              dateRep));
    } else {
      // TODO uncomment when fixed
      showError("All fields must be filled.");
    }
    onClearClicked();
  }

  private boolean allFieldsFilled() {
    return !(priorityIn.getValue().equals("") || locationBox.getValue().equals(""));
  }

  public void initBoxes() {
    priorityIn.setItems(
        FXCollections.observableArrayList(TableHelper.convertEnum(Request.Priority.class)));
    locationBox.setItems(FXCollections.observableArrayList(DAOFacade.getAllLocationLongNames()));
  }
  /** Saves a given service request to a CSV by opening the CSV window */
  public void saveToCSV() {
    super.saveToCSV(new SecurityRequest(), (Stage) priorityIn.getScene().getWindow());
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

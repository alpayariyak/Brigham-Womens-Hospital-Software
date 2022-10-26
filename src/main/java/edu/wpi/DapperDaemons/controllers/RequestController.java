package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.tables.Table;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class RequestController extends ParentController implements Initializable {

  @FXML private GridPane assignedTable;
  @FXML private GridPane createTable;
  @FXML private GridPane relevantTable;
  private Table<Request> createT;
  private Table<Request> assignedT;
  private Table<Request> relevantT;
  @FXML private VBox createBox;
  @FXML private VBox assignBox;
  @FXML private VBox relevantBox;

  @FXML private ToggleButton assignedRequests;
  @FXML private ToggleButton createdRequests;
  @FXML private ToggleButton relevantRequests;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tableinit();
    selectCreated();
  }

  @FXML
  void selectAssigned() {
    assignBox.setVisible(true);
    createBox.setVisible(false);
    relevantBox.setVisible(false);
    assignedTable.setPickOnBounds(true);
    createTable.setPickOnBounds(false);
    relevantTable.setPickOnBounds(false);
  }

  @FXML
  void selectCreated() {
    assignBox.setVisible(false);
    createBox.setVisible(true);
    relevantBox.setVisible(false);
    assignedTable.setPickOnBounds(false);
    createTable.setPickOnBounds(true);
    relevantTable.setPickOnBounds(false);
  }

  @FXML
  void selectRelevant() {
    assignBox.setVisible(false);
    createBox.setVisible(false);
    relevantBox.setVisible(true);
    assignedTable.setPickOnBounds(false);
    createTable.setPickOnBounds(false);
    relevantTable.setPickOnBounds(true);
  }

  public void tableinit() {
    createT = new Table<>(Request.class, createTable, 2);
    new Thread(
            () ->
                Platform.runLater(
                    () -> {
                      createT.setRows(DAOFacade.getAllRequests());
                      createT.setHeader(
                          List.of(
                              "Request Type",
                              "Room",
                              "Requester",
                              "Assignee",
                              "Status",
                              "Priority"));
                      createT.addFilter(4, SecurityController.getUser().getNodeID());
                      createT.filter();
                      createT.addDropDownEditProperty(4, 6, "CANCELLED");
                      createT.addEnumEditProperty(5, 2, Request.Priority.class);
                      createT.addDropDownEditProperty(
                          3, 5, DAOFacade.getAllPlebs().toArray(new String[] {}));
                      createT.setRequestListeners();
                    }))
        .start();

    assignedT = new Table<>(Request.class, assignedTable, 2);
    new Thread(
            () ->
                Platform.runLater(
                    () -> {
                      assignedT.setRows(DAOFacade.getAllRequests());
                      assignedT.setHeader(
                          List.of(
                              "Request Type",
                              "Room",
                              "Requester",
                              "Assignee",
                              "Status",
                              "Priority"));
                      assignedT.addFilter(5, SecurityController.getUser().getNodeID());
                      assignedT.filter();
                      assignedT.addEnumEditProperty(4, 6, Request.RequestStatus.class);
                      assignedT.setRequestListeners();
                    }))
        .start();

    relevantT = new Table<>(Request.class, relevantTable, 2);
    new Thread(
            () ->
                Platform.runLater(
                    () -> {
                      relevantT.setRows(DAOFacade.getAllRequests());
                      relevantT.setHeader(
                          List.of(
                              "Request Type",
                              "Room",
                              "Requester",
                              "Assignee",
                              "Status",
                              "Priority"));
                      relevantT.addFilter(6, "REQUESTED");
                      relevantT.filter();
                      relevantT.addDropDownEditProperty(
                          3, 5, SecurityController.getUser().getNodeID());
                      relevantT.setRequestListeners();
                    }))
        .start();
  }
}

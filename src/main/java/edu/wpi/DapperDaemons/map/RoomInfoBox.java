package edu.wpi.DapperDaemons.map;

import com.google.firebase.database.ValueEventListener;
import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.helpers.TableListeners;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.Patient;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.tables.TableHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RoomInfoBox {

  public enum TableDisplayType {
    EQUIPMENT,
    PATIENT,
    REQUEST
  }

  private VBox roomInfoBox;
  private VBox infoTables;
  private TextField nameTxt;
  private TextField floorTxt;
  private TextField typeTxt;
  private TextField buildingTxt;
  private TableView<MedicalEquipment> equipTable;
  private TableView<Patient> patientTable;
  private TableView<Request> requestTable;
  private Location loc;
  private PositionInfo pos;

  public RoomInfoBox(
      VBox roomInfoBox,
      VBox infoTables,
      TextField nameTxt,
      TextField floorTxt,
      TextField typeTxt,
      TextField buildingTxt) {
    this.roomInfoBox = roomInfoBox;
    this.infoTables = infoTables;
    this.nameTxt = nameTxt;
    this.floorTxt = floorTxt;
    this.typeTxt = typeTxt;
    this.buildingTxt = buildingTxt;

    this.equipTable =
        (TableView<MedicalEquipment>)
            ((StackPane) infoTables.getChildren().get(0)).getChildren().get(0);
    this.patientTable =
        (TableView<Patient>) ((StackPane) infoTables.getChildren().get(0)).getChildren().get(1);
    this.requestTable =
        (TableView<Request>) ((StackPane) infoTables.getChildren().get(0)).getChildren().get(2);

    new TableHelper<>(equipTable, 1).linkColumns(MedicalEquipment.class);
    new TableHelper<>(patientTable, 1).linkColumns(Patient.class);
    new TableHelper<>(requestTable, 1).linkColumns(Request.class);
  }

  private ValueEventListener equipListener;
  private ValueEventListener patListener;
  private ValueEventListener reqListener;

  private void setListeners() {
    equipListener =
        TableListeners.eventListener(
            () -> {
              List<MedicalEquipment> equipment =
                  new ArrayList<>(
                      DAOPouch.getMedicalEquipmentDAO().filter(6, pos.getId()).values());
              equipTable.getItems().clear();
              equipTable.getItems().addAll(equipment);
            });
    TableListeners.addListener(new MedicalEquipment().tableName(), equipListener);

    patListener =
        TableListeners.eventListener(
            () -> {
              List<Patient> patients =
                  new ArrayList<>(DAOPouch.getPatientDAO().filter(6, pos.getId()).values());
              patientTable.getItems().clear();
              patientTable.getItems().addAll(patients);
            });
    TableListeners.addListener(new Patient().tableName(), patListener);

    reqListener =
        TableListeners.eventListener(
            () -> {
              List<Request> requests = DAOFacade.getFilteredRequests(pos.getId());
              requestTable.getItems().clear();
              requestTable.getItems().addAll(requests);
            });
    TableListeners.addListeners(
        DAOFacade.getAllRequests().stream()
            .map((r) -> ((TableObject) r).tableName())
            .collect(Collectors.toCollection(ArrayList<String>::new)),
        reqListener);
  }

  public void open() {
    roomInfoBox.setVisible(true);
    setListeners();
  }

  public void toggleTable(TableDisplayType type) {
    infoTables.setVisible(true);
    switch (type) {
      case EQUIPMENT:
        if (equipTable.isVisible()) {
          equipTable.setVisible(false);
          infoTables.setVisible(false);
          return;
        }
        equipTable.setVisible(true);
        patientTable.setVisible(false);
        requestTable.setVisible(false);
        break;
      case PATIENT:
        if (patientTable.isVisible()) {
          patientTable.setVisible(false);
          infoTables.setVisible(false);
          return;
        }
        equipTable.setVisible(false);
        patientTable.setVisible(true);
        requestTable.setVisible(false);
        break;
      case REQUEST:
        if (requestTable.isVisible()) {
          requestTable.setVisible(false);
          infoTables.setVisible(false);
          return;
        }
        equipTable.setVisible(false);
        patientTable.setVisible(false);
        requestTable.setVisible(true);
        break;
      default:
        equipTable.setVisible(false);
        patientTable.setVisible(false);
        requestTable.setVisible(false);
    }
  }

  public void close() {
    roomInfoBox.setVisible(false);
    infoTables.setVisible(false);
    if (equipListener != null)
      TableListeners.removeListener(new MedicalEquipment().tableName(), equipListener);
    if (patListener != null) TableListeners.removeListener(new Patient().tableName(), patListener);
    if (reqListener != null)
      DAOFacade.getAllRequests().stream()
          .map((r) -> ((TableObject) r).tableName())
          .forEach(s -> TableListeners.removeListener(s, reqListener));
  }

  public void openLoc(
      PositionInfo pos,
      List<MedicalEquipment> equipment,
      List<Patient> patients,
      List<Request> requests) {
    this.pos = pos;

    nameTxt.setText(pos.getLongName());
    floorTxt.setText(pos.getFloor());
    typeTxt.setText(pos.getType());
    buildingTxt.setText(pos.getBuilding());

    this.loc = pos.getLoc();

    equipTable.getItems().clear();
    equipTable.getItems().addAll(equipment);

    patientTable.getItems().clear();
    patientTable.getItems().addAll(patients);

    requestTable.getItems().clear();
    requestTable.getItems().addAll(requests);
  }

  public Location getPosition() {
    App.LOG.info("Returning position info for request showing on map");
    return loc;
  }

  public Location change(PositionInfo selected) {
    String type = typeTxt.getText();
    String building = buildingTxt.getText();
    String longName = nameTxt.getText();
    String shortName;
    if (longName.length() > 3) {
      shortName = longName.substring(0, 3);
    } else shortName = longName;

    return new Location(
        selected.getId(),
        selected.getX(),
        selected.getY(),
        selected.getFloor(),
        building,
        type,
        longName,
        shortName);
  }

  public boolean allFilled() {
    return !(nameTxt.getText().trim().equals("")
        || typeTxt.getText().trim().equals("")
        || floorTxt.getText().trim().equals("")
        || buildingTxt.getText().trim().equals(""));
  }
}

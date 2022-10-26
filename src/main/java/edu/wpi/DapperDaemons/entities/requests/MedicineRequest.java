package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.AutoAssigner;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class MedicineRequest extends TableObject implements Request {

  /*


  DO NOT CHANGE THIS CLASS EVER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

   */

  // TABLE OBJECT AND REQUEST METHODS
  @Override
  public String tableInit() {
    return "CREATE TABLE MEDICINEREQUESTS(nodeid varchar(1000) PRIMARY KEY,"
        + "priority varchar(1000) DEFAULT 'LOW',"
        + "roomID varchar(1000) DEFAULT 'Unknown',"
        + "requesterID varchar(1000) ,"
        + "assigneeID varchar(1000) DEFAULT 'Unselected',"
        + "status varchar(1000),"
        + "notes varchar(1000),"
        + "dateTime varchar(1000),"
        + "patientID varchar(1000) DEFAULT 'Someone',"
        + "medicationName varchar(1000) ,"
        + "quantity varchar(1000),"
        + "dateNeeded varchar(1000))";
  }

  @Override
  public String tableName() {
    return "MEDICINEREQUESTS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return nodeID;
      case 2:
        return priority.toString();
      case 3:
        return roomID;
      case 4:
        return requesterID;
      case 5:
        return assigneeID;
      case 6:
        return status.toString();
      case 7:
        return notes;
      case 8:
        return dateTime;
      case 9:
        return patientID;
      case 10:
        return medicationName;
      case 11:
        return Integer.toString(quantity);
      case 12:
        return dateNeeded;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public void setAttribute(int columnNumber, String newAttribute) {
    switch (columnNumber) {
      case 1:
        nodeID = newAttribute;
        break;
      case 2:
        priority = Priority.valueOf(newAttribute);
        break;
      case 3:
        roomID = newAttribute;
        break;
      case 4:
        requesterID = newAttribute;
        break;
      case 5:
        assigneeID = newAttribute;
        break;
      case 6:
        status = RequestStatus.valueOf(newAttribute);
        break;
      case 7:
        notes = newAttribute;
        break;
      case 8:
        dateTime = newAttribute;
        break;
      case 9:
        patientID = newAttribute;
        break;
      case 10:
        medicationName = newAttribute;
        break;
      case 11:
        quantity = Integer.parseInt(newAttribute);
        break;
      case 12:
        dateNeeded = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    MedicineRequest temp = new MedicineRequest();
    for (int i = 0; i < l.size(); i++) {
      temp.setAttribute(i + 1, l.get(i));
    }
    return temp;
  }

  @Override
  public void setAttribute(String attribute, String newAttribute) {
    switch (attribute) {
      case "nodeID":
        nodeID = newAttribute;
        break;
      case "priority":
        priority = Priority.valueOf(newAttribute);
        break;
      case "roomID":
        roomID = newAttribute;
        break;
      case "requesterID":
        requesterID = newAttribute;
        break;
      case "assigneeID":
        assigneeID = newAttribute;
        break;
      case "status":
        status = RequestStatus.valueOf(newAttribute);
        break;
      case "notes":
        notes = newAttribute;
        break;
      case "dateTime":
        dateTime = newAttribute;
        break;
      case "patientID":
        patientID = newAttribute;
        break;
      case "medicationName":
        medicationName = newAttribute;
        break;
      case "quantity":
        quantity = Integer.valueOf(newAttribute);
        break;
      case "dateNeeded":
        dateNeeded = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String requestType() {
    return "Medicine Request";
  }

  @Override
  @TableHandler(table = 0, col = 7)
  public Priority getPriority() {
    return priority;
  }

  @Override
  public boolean requiresTransport() {
    return true;
  }

  // ATTRIBUTES
  private String nodeID;
  private Priority priority;
  private String roomID;
  private String requesterID;
  private String assigneeID;
  private RequestStatus status = RequestStatus.REQUESTED;
  private String notes;
  private String dateTime;
  private String patientID;
  private String medicationName;
  private int quantity;
  private String dateNeeded;

  // CONTSTRUCTORS

  public MedicineRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String assigneeID,
      String notes,
      String patientID,
      String medicationName,
      int quantity,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = assigneeID;
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.patientID = patientID;
    this.medicationName = medicationName;
    this.quantity = quantity;
    this.dateNeeded = dateNeeded;
  }

  public MedicineRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String notes,
      String patientID,
      String medicationName,
      int quantity,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = AutoAssigner.assignNurse();
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.patientID = patientID;
    this.medicationName = medicationName;
    this.quantity = quantity;
    this.dateNeeded = dateNeeded;
  }

  public MedicineRequest() {}
  // SETTERS AND GETTERS
  @Override
  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  @TableHandler(table = 0, col = 2)
  public String getRoomID() {
    return roomID;
  }

  @Override
  public String transportFromRoomID() {
    return RandomizeFields.getRandomStor();
  }

  public void setRoomID(String roomID) {
    this.roomID = roomID;
  }

  @TableHandler(table = 0, col = 0)
  public String getRequesterID() {
    return requesterID;
  }

  public void setRequesterID(String requesterID) {
    this.requesterID = requesterID;
  }

  @TableHandler(table = 0, col = 1)
  public String getAssigneeID() {
    return assigneeID;
  }

  public void setAssigneeID(String assigneeID) {
    this.assigneeID = assigneeID;
  }

  @TableHandler(table = 0, col = 3)
  public String getPatientID() {
    return patientID;
  }

  public void setPatientID(String patientID) {
    this.patientID = patientID;
  }

  @TableHandler(table = 0, col = 4)
  public String getMedicationName() {
    return medicationName;
  }

  public void setMedicationName(String medicationName) {
    this.medicationName = medicationName;
  }

  @TableHandler(table = 0, col = 5)
  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public void setStatus(RequestStatus status) {
    this.status = status;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  @TableHandler(table = 0, col = 6)
  public String getDateNeeded() {
    return dateNeeded;
  }

  public void setDateNeeded(String dateNeeded) {
    this.dateNeeded = dateNeeded;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MedicineRequest that = (MedicineRequest) o;

    if (quantity != that.quantity) return false;
    if (!nodeID.equals(that.nodeID)) return false;
    if (priority != that.priority) return false;
    if (!roomID.equals(that.roomID)) return false;
    if (!requesterID.equals(that.requesterID)) return false;
    if (!assigneeID.equals(that.assigneeID)) return false;
    if (status != that.status) return false;
    if (!notes.equals(that.notes)) return false;
    if (!dateTime.equals(that.dateTime)) return false;
    if (!patientID.equals(that.patientID)) return false;
    if (!medicationName.equals(that.medicationName)) return false;
    return dateNeeded.equals(that.dateNeeded);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + priority.hashCode();
    result = 31 * result + roomID.hashCode();
    result = 31 * result + requesterID.hashCode();
    result = 31 * result + assigneeID.hashCode();
    result = 31 * result + status.hashCode();
    result = 31 * result + notes.hashCode();
    result = 31 * result + dateTime.hashCode();
    result = 31 * result + patientID.hashCode();
    result = 31 * result + medicationName.hashCode();
    result = 31 * result + quantity;
    result = 31 * result + dateNeeded.hashCode();
    return result;
  }
}

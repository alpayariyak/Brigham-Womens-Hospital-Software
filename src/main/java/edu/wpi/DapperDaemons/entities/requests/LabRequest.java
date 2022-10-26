package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.AutoAssigner;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class LabRequest extends TableObject implements Request {

  /*


  DO NOT CHANGE THIS CLASS EVER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

   */

  // CLASS ENUMS
  public enum LabType {
    BLOOD_DRAW,
    URINE_SAMPLE,
    X_RAY,
    CAT_SCAN,
    MRI;
  }

  // TABLE OBJECT AND REQUEST METHODS

  @Override
  public String tableInit() {
    return "CREATE TABLE LABREQUESTS(nodeid varchar(1000) PRIMARY KEY,"
        + "priority varchar(1000),"
        + "roomID varchar(1000) ,"
        + "requesterID varchar(1000),"
        + "assigneeID varchar(1000),"
        + "status varchar(1000),"
        + "notes varchar(1000),"
        + "dateTime varchar(1000),"
        + "patientID varchar(1000),"
        + "labType varchar(1000),"
        + "dateNeeded varchar(1000))";
  }

  @Override
  public TableObject newInstance(List<String> l) {
    LabRequest temp = new LabRequest();
    for (int i = 0; i < l.size(); i++) {
      temp.setAttribute(i + 1, l.get(i));
    }
    return temp;
  }

  @Override
  public String tableName() {
    return "LABREQUESTS";
  }

  public void setStatus(String status) {
    this.status = RequestStatus.valueOf(status);
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
        return labType.toString();
      case 11:
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
        labType = LabType.valueOf(newAttribute);
        break;
      case 11:
        dateNeeded = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

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
      case "patientID":
        patientID = newAttribute;
        break;
      case "labType":
        labType = LabType.valueOf(newAttribute);
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
      case "dateNeeded":
        dateNeeded = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @TableHandler(table = 0, col = 5)
  public RequestStatus getStatus() {
    return status;
  }

  @Override
  public String getDateNeeded() {
    return dateNeeded;
  }

  @Override
  public String requestType() {
    return "Lab Request";
  }

  @Override
  @TableHandler(table = 0, col = 6)
  public Priority getPriority() {
    return priority;
  }

  @Override
  public boolean requiresTransport() {
    return false;
  }

  // ATTRIBUTES
  private String nodeID;
  private Priority priority;
  private String roomID;
  private String requesterID;
  private String assigneeID;
  private String patientID;
  private LabType labType;
  private RequestStatus status;
  private String notes;
  private String dateTime;
  private String dateNeeded;

  // CONSTRUCTORS

  public LabRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String assigneeID,
      String notes,
      String patientID,
      LabType labType,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = assigneeID;
    this.patientID = patientID;
    this.labType = labType;
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = RequestStatus.REQUESTED;
    this.dateNeeded = dateNeeded;
  }

  public LabRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String notes,
      String patientID,
      LabType labType,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = AutoAssigner.assignDoctor();
    this.patientID = patientID;
    this.labType = labType;
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = RequestStatus.REQUESTED;
    this.dateNeeded = dateNeeded;
  }

  public LabRequest() {}

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

  @TableHandler(table = 0, col = 3)
  public String getRoomID() {
    return roomID;
  }

  @Override
  public String transportFromRoomID() {
    return roomID;
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

  @TableHandler(table = 0, col = 4)
  public String getPatientID() {
    return patientID;
  }

  public void setPatientID(String patientID) {
    this.patientID = patientID;
  }

  @TableHandler(table = 0, col = 2)
  public LabType getLabType() {
    return labType;
  }

  public void setLabType(LabType labType) {
    this.labType = labType;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LabRequest that = (LabRequest) o;

    if (!nodeID.equals(that.nodeID)) return false;
    if (priority != that.priority) return false;
    if (!roomID.equals(that.roomID)) return false;
    if (!requesterID.equals(that.requesterID)) return false;
    if (!assigneeID.equals(that.assigneeID)) return false;
    if (!patientID.equals(that.patientID)) return false;
    if (labType != that.labType) return false;
    if (status != that.status) return false;
    if (!notes.equals(that.notes)) return false;
    if (!dateTime.equals(that.dateTime)) return false;
    return dateNeeded.equals(that.dateNeeded);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + priority.hashCode();
    result = 31 * result + roomID.hashCode();
    result = 31 * result + requesterID.hashCode();
    result = 31 * result + assigneeID.hashCode();
    result = 31 * result + patientID.hashCode();
    result = 31 * result + labType.hashCode();
    result = 31 * result + status.hashCode();
    result = 31 * result + notes.hashCode();
    result = 31 * result + dateTime.hashCode();
    result = 31 * result + dateNeeded.hashCode();
    return result;
  }
}

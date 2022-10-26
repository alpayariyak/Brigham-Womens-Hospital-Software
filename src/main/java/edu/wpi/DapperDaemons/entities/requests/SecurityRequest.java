package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.AutoAssigner;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class SecurityRequest extends TableObject implements Request {

  public String getNodeID() {
    return nodeID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SecurityRequest that = (SecurityRequest) o;

    if (!nodeID.equals(that.nodeID)) return false;
    if (priority != that.priority) return false;
    if (!roomID.equals(that.roomID)) return false;
    if (!requester.equals(that.requester)) return false;
    if (!assignee.equals(that.assignee)) return false;
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
    result = 31 * result + requester.hashCode();
    result = 31 * result + assignee.hashCode();
    result = 31 * result + status.hashCode();
    result = 31 * result + notes.hashCode();
    result = 31 * result + dateTime.hashCode();
    result = 31 * result + dateNeeded.hashCode();
    return result;
  }

  @Override
  public String getRequesterID() {
    return getRequester();
  }

  @Override
  public String getAssigneeID() {
    return getAssignee();
  }

  @Override
  public String requestType() {
    return "Security Request";
  }

  @TableHandler(table = 0, col = 3)
  public Request.Priority getPriority() {
    return priority;
  }

  @Override
  public boolean requiresTransport() {
    return true;
  }

  @TableHandler(table = 0, col = 2)
  public String getRoomID() {
    return roomID;
  }

  @Override
  public String transportFromRoomID() {
    return "FINFO00101";
  }

  @TableHandler(table = 0, col = 0)
  public String getRequester() {
    return requester;
  }

  @TableHandler(table = 0, col = 1)
  public String getAssignee() {
    return assignee;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  public void setPriority(Request.Priority priority) {
    this.priority = priority;
  }

  public void setRoomID(String roomID) {
    this.roomID = roomID;
  }

  public void setRequester(String requester) {
    this.requester = requester;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  private String nodeID;
  private Request.Priority priority;
  private String roomID;
  private String requester;
  private String assignee;
  private Request.RequestStatus status = Request.RequestStatus.REQUESTED;
  private String notes = "";
  private String dateTime = "";
  private String dateNeeded;

  public SecurityRequest() {}

  public SecurityRequest(
      Request.Priority priority,
      String roomID,
      String requesterID,
      String assigneeID,
      // add notes after Assignee JOE
      String notes,
      // thats it, should be pretty easy
      String dateNeeded) {
    this.nodeID = String.valueOf(priority) + roomID + LocalDateTime.now();
    this.priority = priority;
    this.roomID = roomID;
    this.assignee = assigneeID;
    this.requester = requesterID;
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = Request.RequestStatus.REQUESTED;
    this.dateNeeded = dateNeeded;
  }

  public SecurityRequest(
      Request.Priority priority,
      String roomID,
      String requesterID,
      String notes,
      String dateNeeded) {
    this.nodeID = String.valueOf(priority) + roomID + LocalDateTime.now();
    this.priority = priority;
    this.roomID = roomID;
    this.assignee = AutoAssigner.assignAdmin();
    this.requester = requesterID;
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = Request.RequestStatus.REQUESTED;
    this.dateNeeded = dateNeeded;
  }

  @Override
  public String tableInit() {
    return "CREATE TABLE SECURITYREQUESTS(nodeID varchar(1000) PRIMARY KEY,"
        + "priority varchar(1000),"
        + "roomID varchar(1000),"
        + "requester varchar(1000),"
        + "assignee varchar(1000),"
        + "status varchar(1000),"
        + "notes varchar(1000),"
        + "dateTime varchar(1000),"
        + "dateNeeded varchar(1000))";
  }

  @Override
  public String tableName() {
    return "SECURITYREQUESTS";
  }

  @Override
  public String getAttribute(int columnNumber) throws ArrayIndexOutOfBoundsException {
    switch (columnNumber) {
      case 1:
        return nodeID;
      case 2:
        return String.valueOf(priority);
      case 3:
        return roomID;
      case 4:
        return requester;
      case 5:
        return assignee;
      case 6:
        return status.toString();
      case 7:
        return notes;
      case 8:
        return dateTime;
      case 9:
        return dateNeeded;
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  @Override
  public void setAttribute(int columnNumber, String newAttribute) {
    switch (columnNumber) {
      case 1:
        nodeID = newAttribute;
        break;
      case 2:
        priority = Request.Priority.valueOf(newAttribute);
        break;
      case 3:
        roomID = newAttribute;
        break;
      case 4:
        requester = newAttribute;
        break;
      case 5:
        assignee = newAttribute;
        break;
      case 6:
        status = Request.RequestStatus.valueOf(newAttribute);
        break;
      case 7:
        notes = newAttribute;
        break;
      case 8:
        dateTime = newAttribute;
        break;
      case 9:
        dateNeeded = newAttribute;
        break;
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    SecurityRequest temp = new SecurityRequest();
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
        priority = Request.Priority.valueOf(newAttribute);
        break;
      case "roomID":
        roomID = newAttribute;
        break;
      case "requester":
        requester = newAttribute;
        break;
      case "assignee":
        assignee = newAttribute;
        break;
      case "status":
        status = Request.RequestStatus.valueOf(newAttribute);
        break;
      case "notes":
        notes = newAttribute;
        break;
      case "dateTime":
        dateTime = newAttribute;
        break;
      case "dateNeeded":
        dateNeeded = newAttribute;
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  public Request.RequestStatus getStatus() {
    return status;
  }

  @Override
  public String getDateNeeded() {
    return dateNeeded;
  }

  public void setStatus(Request.RequestStatus status) {
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
}

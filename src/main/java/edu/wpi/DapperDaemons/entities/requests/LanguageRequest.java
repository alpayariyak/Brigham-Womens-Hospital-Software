package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.AutoAssigner;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class LanguageRequest extends TableObject implements Request {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LanguageRequest that = (LanguageRequest) o;

    if (!nodeID.equals(that.nodeID)) return false;
    if (priority != that.priority) return false;
    if (!roomID.equals(that.roomID)) return false;
    if (!requesterID.equals(that.requesterID)) return false;
    if (!assignee.equals(that.assignee)) return false;
    if (status != that.status) return false;
    if (!notes.equals(that.notes)) return false;
    if (!dateTime.equals(that.dateTime)) return false;
    if (language != that.language) return false;
    return dateNeeded.equals(that.dateNeeded);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + priority.hashCode();
    result = 31 * result + roomID.hashCode();
    result = 31 * result + requesterID.hashCode();
    result = 31 * result + assignee.hashCode();
    result = 31 * result + status.hashCode();
    result = 31 * result + notes.hashCode();
    result = 31 * result + dateTime.hashCode();
    result = 31 * result + language.hashCode();
    result = 31 * result + dateNeeded.hashCode();
    return result;
  }

  @Override
  public String getNodeID() {
    return nodeID;
  }

  @Override
  public String getRequesterID() {
    return requesterID;
  }

  @Override
  public String getAssigneeID() {
    return assignee;
  }

  @Override
  public RequestStatus getStatus() {
    return status;
  }

  @TableHandler(table = 0, col = 2)
  public Language getLanguage() {
    return language;
  }

  @Override
  public String requestType() {
    return "Language Request";
  }

  @Override
  @TableHandler(table = 0, col = 4)
  public Request.Priority getPriority() {
    return priority;
  }

  @Override
  public boolean requiresTransport() {
    return true;
  }

  @TableHandler(table = 0, col = 3)
  public String getRoomID() {
    return roomID;
  }

  @Override
  public String transportFromRoomID() {
    return RandomizeFields.getRandomExit();
  }

  @TableHandler(table = 0, col = 0)
  public String getRequester() {
    return requesterID;
  }

  @TableHandler(table = 0, col = 1)
  public String getAssignee() {
    return assignee;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public void setRoomID(String roomID) {
    this.roomID = roomID;
  }

  public void setRequester(String requester) {
    this.requesterID = requester;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public void setDateNeeded(String dateNeeded) {
    this.dateNeeded = dateNeeded;
  }

  public void setPriority(Request.Priority priority) {
    this.priority = priority;
  }

  @Override
  public String getDateNeeded() {
    return dateNeeded;
  }

  private String nodeID;
  private Request.Priority priority = Request.Priority.LOW;
  private String roomID;
  private String requesterID;
  private String assignee;
  private RequestStatus status = RequestStatus.REQUESTED;
  private String notes = "";
  private String dateTime = "";
  private Language language;
  private String dateNeeded;

  public enum Language {
    CHINESE,
    SPANISH,
    ENGLISH,
    HINDI,
    BENGALI,
    PORTUGUESE,
    RUSSIAN,
    JAPANESE,
    TURKISH,
    KOREAN,
    FRENCH,
    ITALIAN,
    ARABIC,
    GERMAN,
    VIETNAMESE,
    POLISH,
    THAI,
    DUTCH,
    GREEK,
    SWEDISH,
    NORWEGIAN,
    FINNISH
  }

  public LanguageRequest() {}

  public LanguageRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String assignee,
      String notes,
      Language language,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assignee = assignee;
    // This is what you add Joanna
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = RequestStatus.REQUESTED;
    this.language = language;
    this.dateNeeded = dateNeeded;
  }

  public LanguageRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String notes,
      Language language,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assignee = AutoAssigner.assignAny();
    // This is what you add Joanna
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = RequestStatus.REQUESTED;
    this.language = language;
    this.dateNeeded = dateNeeded;
  }

  @Override
  public String tableInit() {
    return "CREATE TABLE LANGUAGEREQUESTS(nodeID varchar(1000) PRIMARY KEY,"
        + "priority varchar(1000),"
        + "roomID varchar(1000),"
        + "requester varchar(1000),"
        + "assignee varchar(1000),"
        + "status varchar(1000),"
        + "notes varchar(1000),"
        + "dateTime varchar(1000),"
        + "language varchar(1000),"
        + "dateNeed varchar(1000))";
  }

  @Override
  public String tableName() {
    return "LANGUAGEREQUESTS";
  }

  @Override
  public String getAttribute(int columnNumber) throws ArrayIndexOutOfBoundsException {
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
        return assignee;
      case 6:
        return status.toString();
      case 7:
        return notes;
      case 8:
        return dateTime;
      case 9:
        return language.toString();
      case 10:
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
        priority = Priority.valueOf(newAttribute);
        break;
      case 3:
        roomID = newAttribute;
        break;
      case 4:
        requesterID = newAttribute;
        break;
      case 5:
        assignee = newAttribute;
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
        language = Language.valueOf(newAttribute);
        break;
      case 10:
        dateNeeded = newAttribute;
        break;
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    LanguageRequest temp = new LanguageRequest();
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
      case "language":
        language = Language.valueOf(newAttribute);
        break;
      case "roomID":
        roomID = newAttribute;
        break;
      case "requester":
        requesterID = newAttribute;
        break;
      case "assignee":
        assignee = newAttribute;
        break;
      case "dateNeeded":
        dateNeeded = newAttribute;
        break;
      case "priority":
        priority = Priority.valueOf(newAttribute);
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
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  public void setRequesterID(String requesterID) {
    this.requesterID = requesterID;
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
}

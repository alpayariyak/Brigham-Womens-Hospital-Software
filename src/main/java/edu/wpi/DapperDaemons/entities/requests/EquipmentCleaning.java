package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.AutoAssigner;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class EquipmentCleaning extends TableObject implements Request {
  // TABLE OBJECT AND REQUEST METHODS
  @Override
  public String tableInit() {
    return "CREATE TABLE EQUIPMENTCLEANINGREQUESTS(nodeid varchar(1000) PRIMARY KEY,"
        + "priority varchar(1000),"
        + "roomID varchar(1000),"
        + "requesterID varchar(1000),"
        + "assigneeID varchar(1000),"
        + "status varchar(1000),"
        + "notes varchar(1000),"
        + "dateTime varchar(1000),"
        + "equipmentID varchar(1000),"
        + "equipmentType varchar(1000),"
        + "cleanStatus varchar(1000),"
        + "cleanBy varchar(1000))";
  }

  @Override
  public String tableName() {
    return "EQUIPMENTCLEANINGREQUESTS";
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
        return equipmentID;
      case 10:
        return equipmentType.toString();
      case 11:
        return cleanStatus.toString();
      case 12:
        return cleanBy;
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
        equipmentID = newAttribute;
        break;
      case 10:
        equipmentType = MedicalEquipment.EquipmentType.valueOf(newAttribute);
        break;
      case 11:
        cleanStatus = MedicalEquipment.CleanStatus.valueOf(newAttribute);
        break;
      case 12:
        cleanBy = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
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
      case "patientID":
        equipmentID = newAttribute;
        break;
      case "labType":
        equipmentType = MedicalEquipment.EquipmentType.valueOf(newAttribute);
        break;
      case "cleanStatus":
        cleanStatus = MedicalEquipment.CleanStatus.valueOf(newAttribute);
        break;
      case "cleabBy":
        cleanBy = newAttribute;
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
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String requestType() {
    return "Equipment Cleaning Request";
  }

  @Override
  public TableObject newInstance(List<String> l) {
    EquipmentCleaning temp = new EquipmentCleaning();
    for (int i = 0; i < l.size(); i++) {
      temp.setAttribute(i + 1, l.get(i));
    }
    return temp;
  }

  @Override
  @TableHandler(table = 0, col = 4)
  public Priority getPriority() {
    return priority;
  }

  @Override
  public boolean requiresTransport() {
    return true;
  }

  // ATTRIBUTES
  private String nodeID = "";
  private Priority priority = Priority.HIGH;
  private String roomID = "";
  private String requesterID = "";
  private String assigneeID = "";
  private RequestStatus status = RequestStatus.REQUESTED;
  private String notes = "";
  private String dateTime = "";
  private String equipmentID = "ID";
  private MedicalEquipment.EquipmentType equipmentType = MedicalEquipment.EquipmentType.BED;
  private MedicalEquipment.CleanStatus cleanStatus = MedicalEquipment.CleanStatus.UNCLEAN;
  private String cleanBy = "";

  // CONSTRUCTORS

  public EquipmentCleaning(
      Priority priority,
      String roomID,
      String requesterID,
      String assigneeID,
      String notes,
      String equipmentID,
      MedicalEquipment.EquipmentType equipmentType,
      MedicalEquipment.CleanStatus cleanStatus,
      String cleanBy) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = assigneeID;
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.equipmentID = equipmentID;
    this.equipmentType = equipmentType;
    this.cleanStatus = cleanStatus;
    this.cleanBy = cleanBy;
    this.status = RequestStatus.REQUESTED;
  }

  public EquipmentCleaning(
      Priority priority,
      String roomID,
      String requesterID,
      String notes,
      String equipmentID,
      MedicalEquipment.EquipmentType equipmentType,
      MedicalEquipment.CleanStatus cleanStatus,
      String cleanBy) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = AutoAssigner.assignJanitor();
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.equipmentID = equipmentID;
    this.equipmentType = equipmentType;
    this.cleanStatus = cleanStatus;
    this.cleanBy = cleanBy;
    this.status = RequestStatus.REQUESTED;
  }

  public EquipmentCleaning() {}

  // SETTERS AND GETTERS
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
    return RandomizeFields.getRandomDirt();
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

  @Override
  public RequestStatus getStatus() {
    return status;
  }

  public void setAssigneeID(String assigneeID) {
    this.assigneeID = assigneeID;
  }

  public String getEquipmentID() {
    return equipmentID;
  }

  public void setEquipmentID(String equipmentID) {
    this.equipmentID = equipmentID;
  }

  @TableHandler(table = 0, col = 2)
  public MedicalEquipment.EquipmentType getEquipmentType() {
    return equipmentType;
  }

  public void setEquipmentType(MedicalEquipment.EquipmentType equipmentType) {
    this.equipmentType = equipmentType;
  }

  public MedicalEquipment.CleanStatus getCleanStatus() {
    return cleanStatus;
  }

  public void setCleanStatus(MedicalEquipment.CleanStatus cleanStatus) {
    this.cleanStatus = cleanStatus;
  }

  public String getCleanBy() {
    return cleanBy;
  }

  public void setCleanBy(String cleanBy) {
    this.cleanBy = cleanBy;
  }

  @Override
  public String getDateNeeded() {
    return cleanBy;
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

    EquipmentCleaning that = (EquipmentCleaning) o;

    if (!nodeID.equals(that.nodeID)) return false;
    if (priority != that.priority) return false;
    if (!roomID.equals(that.roomID)) return false;
    if (!requesterID.equals(that.requesterID)) return false;
    if (!assigneeID.equals(that.assigneeID)) return false;
    if (status != that.status) return false;
    if (!notes.equals(that.notes)) return false;
    if (!dateTime.equals(that.dateTime)) return false;
    if (!equipmentID.equals(that.equipmentID)) return false;
    if (equipmentType != that.equipmentType) return false;
    if (cleanStatus != that.cleanStatus) return false;
    return cleanBy.equals(that.cleanBy);
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
    result = 31 * result + equipmentID.hashCode();
    result = 31 * result + equipmentType.hashCode();
    result = 31 * result + cleanStatus.hashCode();
    result = 31 * result + cleanBy.hashCode();
    return result;
  }
}

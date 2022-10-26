package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.AutoAssigner;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class MealDeliveryRequest extends TableObject implements Request {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MealDeliveryRequest that = (MealDeliveryRequest) o;

    if (!nodeID.equals(that.nodeID)) return false;
    if (priority != that.priority) return false;
    if (!roomID.equals(that.roomID)) return false;
    if (!requesterID.equals(that.requesterID)) return false;
    if (!assigneeID.equals(that.assigneeID)) return false;
    if (status != that.status) return false;
    if (!notes.equals(that.notes)) return false;
    if (!dateTime.equals(that.dateTime)) return false;
    if (!patientID.equals(that.patientID)) return false;
    if (!entree.equals(that.entree)) return false;
    if (!side.equals(that.side)) return false;
    if (!drink.equals(that.drink)) return false;
    if (!dessert.equals(that.dessert)) return false;
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
    result = 31 * result + entree.hashCode();
    result = 31 * result + side.hashCode();
    result = 31 * result + drink.hashCode();
    result = 31 * result + dessert.hashCode();
    result = 31 * result + dateNeeded.hashCode();
    return result;
  }

  /*


  DO NOT CHANGE THIS CLASS EVER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

   */

  // TABLE OBJECT AND REQUEST METHODS
  @Override
  public String tableInit() {
    return "CREATE TABLE MEALDELIVERYREQUESTS(nodeid varchar(1000) PRIMARY KEY,"
        + "priority varchar(1000),"
        + "roomID varchar(1000),"
        + "requesterID varchar(1000),"
        + "assigneeID varchar(1000),"
        // copy paste these three JOE
        + "status varchar(1000),"
        + "notes varchar(1000),"
        + "dateTime varchar(1000),"
        // Stop after this^^^ one HU
        + "patientID varchar(1000),"
        + "entree varchar(1000),"
        + "side varchar(1000),"
        + "drink varchar(1000),"
        + "dessert varchar(1000),"
        + "dateNeed varchar(1000))";
  }

  @Override
  public String tableName() {
    return "MEALDELIVERYREQUESTS";
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
        // add these three here!!!!
      case 6:
        return status.toString();
      case 7:
        return notes;
      case 8:
        return dateTime;
        // done not the ones below JO
      case 9:
        return patientID;
      case 10:
        return entree;
      case 11:
        return side;
      case 12:
        return drink;
      case 13:
        return dessert;
      case 14:
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
        // the three below this comment
      case 6:
        status = RequestStatus.valueOf(newAttribute);
        break;
      case 7:
        notes = newAttribute;
        break;
      case 8:
        dateTime = newAttribute;
        break;
        // the three above this comment ^^^^
      case 9:
        patientID = newAttribute;
        break;
      case 10:
        entree = newAttribute;
        break;
      case 11:
        side = newAttribute;
        break;
      case 12:
        drink = newAttribute;
        break;
      case 13:
        dessert = newAttribute;
        break;
      case 14:
        dateNeeded = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String requestType() {
    return "Meal Delivery Request";
  }

  @Override
  @TableHandler(table = 0, col = 8)
  public Priority getPriority() {
    return priority;
  }

  @Override
  public TableObject newInstance(List<String> l) {
    MealDeliveryRequest temp = new MealDeliveryRequest();
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
      case "patientID":
        patientID = newAttribute;
        break;
      case "entree":
        entree = newAttribute;
        break;
      case "side":
        side = newAttribute;
        break;
      case "drink":
        drink = newAttribute;
        break;
      case "dessert":
        dessert = newAttribute;
        break;
        // add these three at the bottom
      case "status":
        status = RequestStatus.valueOf(newAttribute);
        break;
      case "notes":
        notes = newAttribute;
        break;
      case "dateTime":
        dateTime = newAttribute;
        break;
        // the three above this comment
      case "dateNeeded":
        dateNeeded = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean requiresTransport() {
    return true;
  }

  // ATTRIBUTES

  private String nodeID = "";
  private Priority priority = Priority.LOW;
  private String roomID = "";
  private String requesterID = "";
  private String assigneeID = "";
  // the three below this comment
  private RequestStatus status = RequestStatus.REQUESTED;
  private String notes = "";
  private String dateTime = "";
  // the three above this comment
  private String patientID = "";
  private String entree = "";
  private String side = "";
  private String drink = "";
  private String dessert = "";
  private String dateNeeded = "";

  // CONSTRUCTORS

  public MealDeliveryRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String assigneeID,
      // add notes after Assignee JOE
      String notes,
      // thats it, should be pretty easy
      String patientID,
      String entree,
      String side,
      String drink,
      String dessert,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = assigneeID;
    // This is what you add Joanna
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = RequestStatus.REQUESTED;
    // end of what you add
    this.patientID = patientID;
    this.entree = entree;
    this.side = side;
    this.drink = drink;
    this.dessert = dessert;
    this.dateNeeded = dateNeeded;
  }

  public MealDeliveryRequest(
      Priority priority,
      String roomID,
      String requesterID,
      String notes,
      String patientID,
      String entree,
      String side,
      String drink,
      String dessert,
      String dateNeeded) {
    this.nodeID = priority.toString() + requesterID + LocalDateTime.now().toString();

    this.priority = priority;
    this.roomID = roomID;
    this.requesterID = requesterID;
    this.assigneeID = AutoAssigner.assignKitchenStaff();
    // This is what you add Joanna
    this.notes = notes;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - MM/dd");
    Date now = new Date();
    this.dateTime = formatter.format(now);
    this.status = RequestStatus.REQUESTED;
    // end of what you add
    this.patientID = patientID;
    this.entree = entree;
    this.side = side;
    this.drink = drink;
    this.dessert = dessert;
    this.dateNeeded = dateNeeded;
  }

  public MealDeliveryRequest() {}

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
    return RandomizeFields.getRandomFood();
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

  @TableHandler(table = 0, col = 2)
  public String getPatientID() {
    return patientID;
  }

  public void setPatientID(String patientID) {
    this.patientID = patientID;
  }

  @TableHandler(table = 0, col = 4)
  public String getEntree() {
    return entree;
  }

  public void setEntree(String entree) {
    this.entree = entree;
  }

  @TableHandler(table = 0, col = 5)
  public String getSide() {
    return side;
  }

  public void setSide(String side) {
    this.side = side;
  }

  @TableHandler(table = 0, col = 6)
  public String getDrink() {
    return drink;
  }

  public void setDrink(String drink) {
    this.drink = drink;
  }

  @TableHandler(table = 0, col = 7)
  public String getDessert() {
    return dessert;
  }

  public void setDessert(String dessert) {
    this.dessert = dessert;
  }

  // add setters and getters here
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
  public String getDateNeeded() {
    return dateNeeded;
  }

  public void setDateNeeded(String dateNeeded) {
    this.dateNeeded = dateNeeded;
  }
}

package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.tables.TableHandler;
import java.util.List;

public class Alert extends TableObject {
  private String description;
  private String nodeID;
  private Request.Priority priority;
  private String type;
  private String floor;

  public Alert() {}

  public Alert(String description, String type, Request.Priority priority, String floor) {
    this.description = description;
    this.nodeID = priority.toString() + description + type;
    this.priority = priority;
    this.type = type;
    this.floor = floor;
  }

  public String tableInit() {
    return "CREATE TABLE ALERTS(nodeID varchar(100) PRIMARY KEY,"
        + "type varchar(255),"
        + "description varchar(255),"
        + "priority varchar(255),"
        + "floor varchar(5))";
  }

  public String tableName() {
    return "ALERTS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return this.nodeID;
      case 2:
        return this.type;
      case 3:
        return this.description;
      case 4:
        return this.priority.toString();
      case 5:
        return this.floor;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public void setAttribute(int columnNumber, String newAttribute) {
    switch (columnNumber) {
      case 1:
        this.nodeID = newAttribute;
        break;
      case 2:
        this.type = newAttribute;
        break;
      case 3:
        this.description = newAttribute;
        break;
      case 4:
        this.priority = Request.Priority.valueOf(newAttribute);
        break;
      case 5:
        this.floor = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    Alert temp = new Alert();
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
      case "description":
        description = newAttribute;
        break;
      case "type":
        type = newAttribute;
        break;
      case "priority":
        priority = Request.Priority.valueOf(newAttribute);
        break;
      case "floor":
        floor = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @TableHandler(table = 1, col = 0)
  public String getDescription() {

    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  @TableHandler(table = 1, col = 2)
  public Request.Priority getPriority() {
    return priority;
  }

  public void setPriority(Request.Priority priority) {
    this.priority = priority;
  }

  @TableHandler(table = 1, col = 1)
  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Alert alert = (Alert) o;

    if (!description.equals(alert.description)) return false;
    if (!nodeID.equals(alert.nodeID)) return false;
    if (priority != alert.priority) return false;
    if (!type.equals(alert.type)) return false;
    return floor.equals(alert.floor);
  }

  @Override
  public int hashCode() {
    int result = description.hashCode();
    result = 31 * result + nodeID.hashCode();
    result = 31 * result + priority.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + floor.hashCode();
    return result;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFloor() {
    return this.floor;
  }
}

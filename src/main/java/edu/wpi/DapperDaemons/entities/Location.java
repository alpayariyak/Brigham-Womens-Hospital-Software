package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.tables.TableHandler;
import java.lang.reflect.Array;
import java.util.List;

public class Location extends TableObject {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Location location = (Location) o;

    if (xcoord != location.xcoord) return false;
    if (ycoord != location.ycoord) return false;
    if (!nodeID.equals(location.nodeID)) return false;
    if (!floor.equals(location.floor)) return false;
    if (!building.equals(location.building)) return false;
    if (!nodeType.equals(location.nodeType)) return false;
    if (!longName.equals(location.longName)) return false;
    return shortName.equals(location.shortName);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + xcoord;
    result = 31 * result + ycoord;
    result = 31 * result + floor.hashCode();
    result = 31 * result + building.hashCode();
    result = 31 * result + nodeType.hashCode();
    result = 31 * result + longName.hashCode();
    result = 31 * result + shortName.hashCode();
    return result;
  }

  private String nodeID;
  private int xcoord = -1;
  private int ycoord = -1;
  private String floor = "unknown";
  private String building = "unknown";
  private String nodeType = "unassigned";
  private String longName = "room";
  private String shortName = "r";

  public Location() {}

  public Location(
      String nodeID,
      int xcoord,
      int ycoord,
      String floor,
      String building,
      String nodeType,
      String longName,
      String shortName) {
    this.nodeID = nodeID;
    this.xcoord = xcoord;
    this.ycoord = ycoord;
    this.floor = floor;
    this.building = building;
    this.nodeType = nodeType;
    this.longName = longName;
    this.shortName = shortName;
  }
  // TableObject Implementation
  public Location(Array[] attributes) {}

  public int getNumAttributes() {
    return 8;
  }

  public String tableInit() {

    return "CREATE TABLE LOCATIONS(nodeid varchar(20) PRIMARY KEY,"
        + "xcoord varchar(20) DEFAULT '-1',"
        + "ycoord varchar(20) DEFAULT '-1',"
        + "floor varchar(20) DEFAULT 'unknown',"
        + "building varchar(20) DEFAULT 'unknown',"
        + "nodetype varchar(20) DEFAULT 'unassigned',"
        + "longname varchar(255) DEFAULT 'room',"
        + "shortname varchar(255) DEFAULT 'r')";
  }

  public String tableName() {
    return "LOCATIONS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return nodeID;
      case 2:
        return Integer.toString(xcoord);
      case 3:
        return Integer.toString(ycoord);
      case 4:
        return floor;
      case 5:
        return building;
      case 6:
        return nodeType;
      case 7:
        return longName;
      case 8:
        return shortName;
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
        xcoord = Integer.parseInt(newAttribute);
        break;
      case 3:
        ycoord = Integer.parseInt(newAttribute);
        break;
      case 4:
        floor = newAttribute;
        break;
      case 5:
        building = newAttribute;
        break;
      case 6:
        nodeType = newAttribute;
        break;
      case 7:
        longName = newAttribute;
        break;
      case 8:
        shortName = newAttribute;
        break;
    }
  }

  // Generic Setters and Getters
  public Location(String nodeID) {
    this.nodeID = nodeID;
  }

  @TableHandler(table = 2, col = 0)
  @TableHandler(table = 0, col = 0)
  public String getNodeID() {
    return nodeID;
  }

  @TableHandler(table = 0, col = 1)
  public int getXcoord() {
    return xcoord;
  }

  @TableHandler(table = 0, col = 2)
  public int getYcoord() {
    return ycoord;
  }

  @TableHandler(table = 2, col = 1)
  @TableHandler(table = 0, col = 3)
  public String getFloor() {
    return floor;
  }

  @TableHandler(table = 2, col = 2)
  @TableHandler(table = 0, col = 4)
  public String getBuilding() {
    return building;
  }

  @TableHandler(table = 2, col = 3)
  @TableHandler(table = 0, col = 5)
  public String getNodeType() {
    return nodeType;
  }

  @TableHandler(table = 2, col = 4)
  @TableHandler(table = 0, col = 6)
  public String getLongName() {
    return longName;
  }

  @TableHandler(table = 0, col = 7)
  public String getShortName() {
    return shortName;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  public void setXcoord(int xcoord) {
    this.xcoord = xcoord;
  }

  public void setYcoord(int ycoord) {
    this.ycoord = ycoord;
  }

  public void setFloor(String floor) {
    this.floor = floor;
  }

  public void setBuilding(String building) {
    this.building = building;
  }

  public void setNodeType(String nodeType) {
    this.nodeType = nodeType;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @Override
  public TableObject newInstance(List<String> l) {
    Location temp = new Location();
    //    System.out.println("Size in location new instance" + l.size());
    for (int i = 0; i < l.size(); i++) {
      //      System.out.println(temp);
      //      System.out.println(l.get(i));
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
      case "xcoord":
        xcoord = Integer.parseInt(newAttribute);
        break;
      case "ycoord":
        ycoord = Integer.parseInt(newAttribute);
        break;
      case "floor":
        floor = newAttribute;
        break;
      case "building":
        building = newAttribute;
        break;
      case "nodeType":
        nodeType = newAttribute;
        break;
      case "longName":
        longName = newAttribute;
        break;
      case "shortName":
        shortName = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public String toString() {
    return nodeID
        + ","
        + String.valueOf(xcoord)
        + ","
        + String.valueOf(ycoord)
        + ","
        + floor
        + ","
        + building
        + ","
        + nodeType
        + ","
        + longName
        + ","
        + shortName
        + "\n";
  }

  public boolean equals(Location l) {
    return l.getNodeID().equals(this.nodeID);
  }
}

package edu.wpi.DapperDaemons.entities;

import java.lang.reflect.Array;
import java.util.List;

public class LocationNodeConnections extends TableObject {
  private String startNodeID = "";

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LocationNodeConnections that = (LocationNodeConnections) o;

    if (!startNodeID.equals(that.startNodeID)) return false;
    if (!connectionOne.equals(that.connectionOne)) return false;
    return connectionTwo.equals(that.connectionTwo);
  }

  @Override
  public int hashCode() {
    int result = startNodeID.hashCode();
    result = 31 * result + connectionOne.hashCode();
    result = 31 * result + connectionTwo.hashCode();
    return result;
  }

  private String connectionOne = "";

  private String connectionTwo = "";

  public LocationNodeConnections() {}

  public LocationNodeConnections(String startNodeID, String connectionOne, String connectionTwo) {
    this.startNodeID = startNodeID;
    this.connectionOne = connectionOne;
    this.connectionTwo = connectionTwo;
  }
  // TableObject Implementation
  public LocationNodeConnections(Array[] attributes) {}

  public int getNumAttributes() {
    return 8;
  }

  public String tableInit() {

    return "CREATE TABLE LOCATIONNODECONNECTIONS(nodeID varchar(60) PRIMARY KEY,"
        + "connectionOne varchar(60) DEFAULT '',"
        + "connectionTwo varchar(60) DEFAULT '')";
  }

  public String tableName() {
    return "LOCATIONNODECONNECTIONS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return startNodeID;
      case 2:
        return connectionOne;
      case 3:
        return connectionTwo;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public void setAttribute(int columnNumber, String newAttribute) {
    switch (columnNumber) {
      case 1:
        startNodeID = newAttribute;
        break;
      case 2:
        connectionOne = newAttribute;
        break;
      case 3:
        connectionTwo = newAttribute;
        break;
    }
  }

  // Generic Setters and Getters
  public String getStartNodeID() {
    return startNodeID;
  }

  public void setStartNodeID(String startNodeID) {
    this.startNodeID = startNodeID;
  }

  public String getConnectionOne() {
    return connectionOne;
  }

  public void setConnectionOne(String connectionOne) {
    this.connectionOne = connectionOne;
  }

  public String getConnectionTwo() {
    return connectionTwo;
  }

  public void setConnectionTwo(String connectionTwo) {
    this.connectionTwo = connectionTwo;
  }

  public String toString() {
    return startNodeID + "," + connectionOne + "," + connectionTwo + "\n";
  }

  @Override
  public TableObject newInstance(List<String> l) {
    LocationNodeConnections temp = new LocationNodeConnections();
    for (int i = 0; i < l.size(); i++) {
      temp.setAttribute(i + 1, l.get(i));
    }
    return temp;
  }

  @Override
  public void setAttribute(String attribute, String newAttribute) {
    switch (attribute) {
      case "startNodeID":
        startNodeID = newAttribute;
        break;
      case "connectionOne":
        connectionOne = newAttribute;
        break;
      case "connectionTwo":
        connectionTwo = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean equals(Location l) {
    return l.getNodeID().equals(this.startNodeID);
  }
}

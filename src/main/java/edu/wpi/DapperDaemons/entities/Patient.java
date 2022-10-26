package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.tables.TableHandler;
import java.util.List;

public class Patient extends TableObject {

  private String nodeID;
  private String firstName;
  private String lastName;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Patient patient = (Patient) o;

    if (dateOfBirth != patient.dateOfBirth) return false;
    if (!nodeID.equals(patient.nodeID)) return false;
    if (!firstName.equals(patient.firstName)) return false;
    if (!lastName.equals(patient.lastName)) return false;
    if (bloodType != patient.bloodType) return false;
    return locationID.equals(patient.locationID);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    result = 31 * result + dateOfBirth;
    result = 31 * result + bloodType.hashCode();
    result = 31 * result + locationID.hashCode();
    return result;
  }

  private int dateOfBirth;
  private BloodType bloodType;
  private String locationID;

  public enum BloodType {
    APOS,
    ANEG,
    BPOS,
    BNEG,
    ABPOS,
    ABNEG,
    OPOS,
    ONEG,
    UNKNOWN;
  }

  public Patient() {
    nodeID = "JohnDoe" + (int) ((double) Integer.MAX_VALUE * Math.random());
  }

  public Patient(
      String firstName, String lastName, int dateOfBirth, BloodType bloodType, String locationID) {
    // candidateID

    this.nodeID = firstName + lastName + dateOfBirth;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.bloodType = bloodType;
    this.locationID = locationID;
  }

  // TableObject Methods
  @Override
  public String tableInit() {
    return "CREATE TABLE PATIENTS(nodeid varchar(48) PRIMARY KEY,"
        + "firstname varchar(20) DEFAULT 'John',"
        + "lastname varchar(20) DEFAULT 'Doe',"
        + "dateofbirth varchar(8) DEFAULT '04201969',"
        + "bloodtype varchar(20) DEFAULT 'UNKOWN',"
        + "locationID varchar(20) DEFAULT 'unknown')";
  }

  @Override
  public String tableName() {
    return "PATIENTS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return nodeID;
      case 2:
        return firstName;
      case 3:
        return lastName;
      case 4:
        return Integer.toString(dateOfBirth);
      case 5:
        return bloodType.toString();
      case 6:
        return locationID;
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
        firstName = newAttribute;
        break;
      case 3:
        lastName = newAttribute;
        break;
      case 4:
        dateOfBirth = Integer.parseInt(newAttribute);
        break;
      case 5:
        bloodType = BloodType.valueOf(newAttribute);
        break;
      case 6:
        locationID = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    Patient temp = new Patient();
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
      case "firstName":
        firstName = newAttribute;
        break;
      case "lastName":
        lastName = newAttribute;
        break;
      case "dateOfBirth":
        dateOfBirth = Integer.parseInt(newAttribute);
        break;
      case "bloodType":
        bloodType = BloodType.valueOf(newAttribute);
        break;
      case "locationID":
        locationID = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  // getters and setters
  @TableHandler(table = 0, col = 0)
  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  @TableHandler(table = 1, col = 0)
  @TableHandler(table = 2, col = 0)
  @TableHandler(table = 0, col = 1)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @TableHandler(table = 1, col = 1)
  @TableHandler(table = 2, col = 1)
  @TableHandler(table = 0, col = 2)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @TableHandler(table = 2, col = 2)
  @TableHandler(table = 0, col = 3)
  public int getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(int dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  @TableHandler(table = 2, col = 4)
  @TableHandler(table = 0, col = 4)
  public BloodType getBloodType() {
    return bloodType;
  }

  public void setBloodType(BloodType bloodType) {
    this.bloodType = bloodType;
  }

  @TableHandler(table = 2, col = 3)
  @TableHandler(table = 0, col = 5)
  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }
}

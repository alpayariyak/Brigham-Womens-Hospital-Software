package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.tables.TableHandler;
import java.util.List;

public class Employee extends TableObject {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Employee employee = (Employee) o;

    if (securityClearance != employee.securityClearance) return false;
    if (!nodeID.equals(employee.nodeID)) return false;
    if (!firstName.equals(employee.firstName)) return false;
    if (!lastName.equals(employee.lastName)) return false;
    if (!dateOfBirth.equals(employee.dateOfBirth)) return false;
    return employeeType == employee.employeeType;
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    result = 31 * result + dateOfBirth.hashCode();
    result = 31 * result + employeeType.hashCode();
    result = 31 * result + securityClearance;
    return result;
  }

  // CLASS ENUMS
  public enum EmployeeType {
    ADMINISTRATOR,
    DOCTOR,
    NURSE,
    JANITOR,
    KITCHEN
  }

  // TABLEOBJECT METHODS
  @Override
  public String tableInit() {

    return "CREATE TABLE EMPLOYEES(nodeid varchar(48) PRIMARY KEY,"
        + "firstname varchar(60),"
        + "lastname varchar(60),"
        + "dateofbirth varchar(20),"
        + "employeetype varchar(30),"
        + "securityclearance varchar(2))";
  }

  @Override
  public String tableName() {
    return "EMPLOYEES";
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
        return dateOfBirth;
      case 5:
        return employeeType.toString();
      case 6:
        return Integer.toString(securityClearance);
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
        dateOfBirth = newAttribute;
        break;
      case 5:
        employeeType = EmployeeType.valueOf(newAttribute);
        break;
      case 6:
        securityClearance = Integer.parseInt(newAttribute);
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    Employee temp = new Employee();
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
        dateOfBirth = newAttribute;
        break;
      case "employeeType":
        employeeType = EmployeeType.valueOf(newAttribute);
        break;
      case "securityClearance":
        securityClearance = Integer.valueOf(newAttribute);
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  // ATTRIBUTES
  private String nodeID;
  private String firstName;
  private String lastName;
  private String dateOfBirth;
  private EmployeeType employeeType;
  private int securityClearance;

  // CONSTRUCTORS

  public Employee() {}

  public Employee(
      String firstName,
      String lastName,
      String dateOfBirth,
      EmployeeType employeeType,
      int securityClearance) {
    this.nodeID = firstName + lastName + dateOfBirth;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.employeeType = employeeType;
    this.securityClearance = securityClearance;
  }

  // SETTERS AND GETTERS

  @TableHandler(table = 0, col = 0)
  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  @TableHandler(table = 0, col = 1)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @TableHandler(table = 0, col = 2)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @TableHandler(table = 0, col = 3)
  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  @TableHandler(table = 0, col = 4)
  public EmployeeType getEmployeeType() {
    return employeeType;
  }

  public void setEmployeeType(EmployeeType employeeType) {
    this.employeeType = employeeType;
  }

  @TableHandler(table = 0, col = 5)
  public int getSecurityClearance() {
    return securityClearance;
  }

  public void setSecurityClearance(int securityClearance) {
    this.securityClearance = securityClearance;
  }

  /**
   * Returns the security level's description for this user, all descriptions are subject to change
   *
   * @return
   */
  public String getSecurityDescription() {
    switch (securityClearance) {
      case 1:
        return "Base Security access, ability to accept and request XYZ";
      case 2:
        return "Low Level Security access, ability to accept and request XYZ";
      case 3:
        return "Medium Level Security access, ability to accept and request XYZ";
      case 4:
        return "High Level Security access, ability to accept and request XYZ";
      case 5:
        return "Admin Level Security access, ability to accept and request XYZ";
    }
    return "Security Level has no description, you are god";
  }
}

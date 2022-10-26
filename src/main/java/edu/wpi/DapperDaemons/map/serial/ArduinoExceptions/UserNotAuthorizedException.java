package edu.wpi.DapperDaemons.map.serial.ArduinoExceptions;

import edu.wpi.DapperDaemons.entities.Employee;

/** Exception thrown when the user is not authorized when attempting to use RFID to sign in */
public class UserNotAuthorizedException extends Exception {
  private Employee.EmployeeType employeeType;

  public UserNotAuthorizedException(Employee.EmployeeType employeeType) {
    this.employeeType = employeeType;
  }

  public Employee.EmployeeType getEmployeeType() {
    return this.employeeType;
  }
}

package edu.wpi.DapperDaemons.map.serial;

import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.ArduinoTimeOutException;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.UnableToConnectException;
import java.security.NoSuchAlgorithmException;

/** Handles login of user using RFID, to interact directly with UI */
public class RFIDMachine {

  public enum LoginState {
    INVALIDUSER,
    TIMEOUT,
    UNABLETOCONNECT,
    SUCCESS
  }

  private Employee employee;

  public RFIDMachine(Employee employee) {
    this.employee = employee;
  }

  /**
   * Login Method for RFID, It will only allow users who are Administrators to login with a valid
   * RFID card All other users will be denied
   *
   * @return a login state representing the result of the attempt
   */
  public LoginState login(String COM) {
    RFIDHandler handler = new RFIDHandler(this.employee);
    boolean loginAttempt;
    try {
      loginAttempt = handler.scan(COM);
    } catch (ArduinoTimeOutException e) {
      System.out.println("ACCESSED DENIED: RFID SCAN TIMEOUT");
      return LoginState.TIMEOUT;
    } catch (UnableToConnectException e) {
      System.err.println("ERROR: UNABLE TO CONNECT TO RFID SCANNER");
      return LoginState.UNABLETOCONNECT;
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Unable to Hash UID");
      return LoginState.UNABLETOCONNECT;
    }
    if (loginAttempt) return LoginState.SUCCESS;
    else return LoginState.INVALIDUSER;
  }

  /**
   * gets the employee being used
   *
   * @return
   */
  public Employee getEmployee() {
    return this.employee;
  }
}

package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.backend.SHA;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Account extends TableObject {
  private String username;
  private String employeeID;
  private String password;
  private String phoneNumber;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    if (!username.equals(account.username)) return false;
    if (!employeeID.equals(account.employeeID)) return false;
    if (!password.equals(account.password)) return false;
    if (!phoneNumber.equals(account.phoneNumber)) return false;
    if (!settingsFile.equals(account.settingsFile)) return false;
    if (!twoFactor.equals(account.twoFactor)) return false;
    return email.equals(account.email);
  }

  @Override
  public int hashCode() {
    int result = username.hashCode();
    result = 31 * result + employeeID.hashCode();
    result = 31 * result + password.hashCode();
    result = 31 * result + phoneNumber.hashCode();
    result = 31 * result + settingsFile.hashCode();
    result = 31 * result + twoFactor.hashCode();
    result = 31 * result + email.hashCode();
    return result;
  }

  private String settingsFile = "edu/wpi/DapperDaemons/notifications/Bloop.wav";
  private String twoFactor = "false";
  private String email;

  public Account(String employeeID, String username, String password)
      throws NoSuchAlgorithmException {
    this.username = username;
    this.employeeID = employeeID;
    this.password = SHA.toHexString(SHA.getSHA(password));
  }

  public Account(
      String employeeID,
      String username,
      String password,
      String phoneNumber,
      String fileName,
      String twofactor,
      String email)
      throws NoSuchAlgorithmException {
    this.username = username;
    this.employeeID = employeeID;
    this.password = SHA.toHexString(SHA.getSHA(password));
    this.phoneNumber = phoneNumber;
    this.settingsFile = fileName;
    this.twoFactor = twofactor;
    this.email = email;
  }

  public Account() {}

  @Override
  public String tableInit() {
    return "CREATE TABLE ACCOUNTS(username varchar(100) PRIMARY KEY,"
        + "employeeID varchar(20),"
        + "password varchar(255),"
        + "phoneNumber varchar(12),"
        + "settingsFile varchar(255),"
        + "twoFactor varchar(5),"
        + "email varchar(255))";
  }

  @Override
  public String tableName() {
    return "ACCOUNTS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return this.username;
      case 2:
        return this.employeeID;
      case 3:
        return this.password;
      case 4:
        return this.phoneNumber;
      case 5:
        return this.settingsFile;
      case 6:
        return this.twoFactor;
      case 7:
        return this.email;
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  @Override
  public void setAttribute(int columnNumber, String newAttribute) {
    switch (columnNumber) {
      case 1:
        this.username = newAttribute;
        break;
      case 2:
        this.employeeID = newAttribute;
        break;
      case 3:
        this.password = newAttribute;
        break;
      case 4:
        this.phoneNumber = newAttribute;
        break;
      case 5:
        this.settingsFile = newAttribute;
        break;
      case 6:
        this.twoFactor = newAttribute;
        break;
      case 7:
        this.email = newAttribute;
        break;
      default:
        break;
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    Account temp = new Account();
    for (int i = 0; i < l.size(); i++) {
      temp.setAttribute(i + 1, l.get(i));
    }
    return temp;
  }

  @Override
  public void setAttribute(String attribute, String newAttribute) {
    switch (attribute) {
      case "username":
        username = newAttribute;
        break;
      case "employeeID":
        employeeID = newAttribute;
        break;
      case "password":
        password = newAttribute;
        break;
      case "phoneNumber":
        phoneNumber = newAttribute;
        break;
      case "settingsFile":
        settingsFile = newAttribute;
        break;
      case "twoFactor":
        twoFactor = newAttribute;
        break;
      case "email":
        email = newAttribute;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean checkPassword(String password) throws NoSuchAlgorithmException {
    return SHA.toHexString(SHA.getSHA(password)).equals(this.password);
  }
}

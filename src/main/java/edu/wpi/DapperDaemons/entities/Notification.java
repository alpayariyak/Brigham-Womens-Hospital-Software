package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.entities.requests.Request;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Notification extends TableObject {

  private String nodeID;
  private String userID;
  private String subject;
  private String body;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Notification that = (Notification) o;

    if (read != that.read) return false;
    if (chimed != that.chimed) return false;
    if (!nodeID.equals(that.nodeID)) return false;
    if (!userID.equals(that.userID)) return false;
    if (!subject.equals(that.subject)) return false;
    if (!date.equals(that.date)) return false;
    return body.equals(that.body);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + userID.hashCode();
    result = 31 * result + subject.hashCode();
    result = 31 * result + body.hashCode();
    result = 31 * result + (read ? 1 : 0);
    result = 31 * result + (chimed ? 1 : 0);
    return result;
  }

  private boolean read = false;

  public String getNodeID() {
    return nodeID;
  }

  public String getUserID() {
    return userID;
  }

  public boolean isRead() {
    return read;
  }

  public boolean isChimed() {
    return chimed;
  }

  private boolean chimed = false;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  SimpleDateFormat f = new SimpleDateFormat("MMddyyyy");

  private String date = f.format(new Date());

  public Notification() {}

  public Notification(String subject, String body, String userID) {
    this.subject = subject;
    this.body = body;
    this.userID = userID;
    this.nodeID = userID + subject + LocalDateTime.now();
    this.date = f.format(new Date());
  }

  public Notification(Notification old, Request updated) {
    this.subject = old.getSubject();
    this.body = old.getBody();
    this.userID = updated.getAssigneeID();
    this.nodeID = old.getNodeID();
    this.date = old.getDate();
  }

  public Notification(String subject, String body, Request r) {
    this.subject = subject;
    this.body = body;
    this.userID = r.getAssigneeID();
    this.nodeID = "not" + r.getNodeID();
    this.date = f.format(new Date());
  }

  @Override
  public String tableInit() {
    return "CREATE TABLE NOTIFICATIONS(nodeID varchar(255) PRIMARY KEY,"
        + "userID varchar(100),"
        + "subject varchar(100),"
        + "body varchar(255),"
        + "isRead varchar(5),"
        + "chimed varchar(5),"
        + "date varchar(100))";
  }

  @Override
  public String tableName() {
    return "NOTIFICATIONS";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return nodeID;
      case 2:
        return userID;
      case 3:
        return subject;
      case 4:
        return body;
      case 5:
        return String.valueOf(read);
      case 6:
        return String.valueOf(chimed);
      case 7:
        return date;
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

  @Override
  public void setAttribute(int columnNumber, String newAttribute) {
    switch (columnNumber) {
      case 1:
        this.nodeID = newAttribute;
        break;
      case 2:
        this.userID = newAttribute;
        break;
      case 3:
        this.subject = newAttribute;
        break;
      case 4:
        this.body = newAttribute;
        break;
      case 5:
        this.read = Boolean.parseBoolean(newAttribute);
        break;
      case 6:
        this.chimed = Boolean.parseBoolean(newAttribute);
        break;
      case 7:
        this.date = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public TableObject newInstance(List<String> l) {
    Notification temp = new Notification();
    for (int i = 0; i < l.size(); i++) {
      temp.setAttribute(i + 1, l.get(i));
    }
    return temp;
  }

  @Override
  public void setAttribute(String attribute, String newAttribute) {
    switch (attribute) {
      case "nodeID":
        this.nodeID = newAttribute;
        break;
      case "user":
        this.userID = newAttribute;
        break;
      case "subject":
        this.subject = newAttribute;
        break;
      case "body":
        this.body = newAttribute;
        break;
      case "read":
        this.read = Boolean.parseBoolean(newAttribute);
        break;
      case "chimed":
        this.chimed = Boolean.parseBoolean(newAttribute);
        break;
      case "date":
        this.date = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean getRead() {
    return read;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }
}

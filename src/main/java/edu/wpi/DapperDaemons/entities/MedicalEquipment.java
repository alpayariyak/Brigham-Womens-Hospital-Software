package edu.wpi.DapperDaemons.entities;

import edu.wpi.DapperDaemons.tables.TableHandler;
import java.util.List;

public class MedicalEquipment extends TableObject {

  private String nodeID;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MedicalEquipment that = (MedicalEquipment) o;

    if (!nodeID.equals(that.nodeID)) return false;
    if (!equipmentName.equals(that.equipmentName)) return false;
    if (equipmentType != that.equipmentType) return false;
    if (!serialNumber.equals(that.serialNumber)) return false;
    if (cleanStatus != that.cleanStatus) return false;
    return locationID.equals(that.locationID);
  }

  @Override
  public int hashCode() {
    int result = nodeID.hashCode();
    result = 31 * result + equipmentName.hashCode();
    result = 31 * result + equipmentType.hashCode();
    result = 31 * result + serialNumber.hashCode();
    result = 31 * result + cleanStatus.hashCode();
    result = 31 * result + locationID.hashCode();
    return result;
  }

  private String equipmentName;
  private EquipmentType equipmentType;
  private String serialNumber;
  private CleanStatus cleanStatus = CleanStatus.UNCLEAN;;

  private String locationID;

  public enum CleanStatus {
    UNCLEAN,
    INPROGRESS,
    CLEAN;
  }

  public enum EquipmentType {
    BED,
    RECLINER,
    XRAY,
    INFUSIONPUMP;
  }

  public MedicalEquipment() {}

  public MedicalEquipment(
      String equipmentName, EquipmentType equipmentType, String serialNumber, String locID) {
    this.nodeID = equipmentType.toString() + serialNumber;
    this.locationID = locID;
    this.equipmentName = equipmentName;
    this.equipmentType = equipmentType;
    this.serialNumber = serialNumber;
  }
  // TableObject Methods
  @Override
  public String tableInit() {
    return "CREATE TABLE MEDICALEQUIPMENT(nodeid varchar(40) PRIMARY KEY,"
        + " equipmentname varchar(20),"
        + "equipmenttype varchar(20),"
        + "serialnumber varchar(20),"
        + "cleanstatus varchar(20),"
        + "locationID varchar(20))";
  }

  @Override
  public String tableName() {
    return "MEDICALEQUIPMENT";
  }

  @Override
  public String getAttribute(int columnNumber) {
    switch (columnNumber) {
      case 1:
        return nodeID;
      case 2:
        return equipmentName;
      case 3:
        return equipmentType.toString();
      case 4:
        return serialNumber;
      case 5:
        return cleanStatus.toString();
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
        equipmentName = newAttribute;
        break;
      case 3:
        equipmentType = EquipmentType.valueOf(newAttribute);
        break;
      case 4:
        serialNumber = newAttribute;
        break;
      case 5:
        cleanStatus = CleanStatus.valueOf(newAttribute);
        break;
      case 6:
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

  @Override
  public TableObject newInstance(List<String> l) {
    MedicalEquipment temp = new MedicalEquipment();
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
      case "equipmentName":
        equipmentName = newAttribute;
        break;
      case "equipmentType":
        equipmentType = EquipmentType.valueOf(newAttribute);
        break;
      case "serialNumber":
        serialNumber = newAttribute;
        break;
      case "cleanStatus":
        cleanStatus = CleanStatus.valueOf(newAttribute);
        break;
      case "locationID":
        locationID = newAttribute;
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @TableHandler(table = 2, col = 0)
  @TableHandler(table = 1, col = 0)
  @TableHandler(table = 0, col = 1)
  public String getEquipmentName() {
    return equipmentName;
  }

  public void setEquipmentName(String equipmentName) {
    this.equipmentName = equipmentName;
  }

  @TableHandler(table = 2, col = 1)
  @TableHandler(table = 1, col = 1)
  @TableHandler(table = 0, col = 2)
  public EquipmentType getEquipmentType() {
    return equipmentType;
  }

  public void setEquipmentType(EquipmentType equipmentType) {
    this.equipmentType = equipmentType;
  }

  @TableHandler(table = 2, col = 3)
  @TableHandler(table = 0, col = 3)
  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getLocationID() {
    return locationID;
  }

  @TableHandler(table = 2, col = 2)
  @TableHandler(table = 1, col = 2)
  @TableHandler(table = 0, col = 4)
  public CleanStatus getCleanStatus() {
    return cleanStatus;
  }

  public void setCleanStatus(CleanStatus cleanStatus) {
    this.cleanStatus = cleanStatus;
  }
}

package edu.wpi.DapperDaemons.backend;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.entities.Account;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.requests.MedicalEquipmentRequest;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.map.pathfinder.AStar;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DAOFacade {

  private DAOFacade() {}

  /** Gets a list of all long names of locations */
  public static List<String> getAllLocationLongNames() {
    List<String> all = new ArrayList<>();
    DAOPouch.getLocationDAO()
        .getAll()
        .values()
        .forEach(
            e -> {
              if (!e.getNodeType().equals("PATH")) { // If its not a path node
                all.add(e.getLongName());
              }
            });
    return all;
  }

  /** Gets a list of all long names of locations */
  public static List<String> getAllLocationLongNamesExit() {
    List<String> all = new ArrayList<>();
    DAOPouch.getLocationDAO()
        .getAll()
        .values()
        .forEach(
            e -> {
              if (e.getNodeType().equals("EXIT")) all.add(e.getLongName());
            });
    return all;
  }

  /** Gets the username of the current user */
  public static String getUsername() {
    String username =
        DAOPouch.getAccountDAO().filter(2, SecurityController.getUser().getNodeID()).values()
            .stream()
            .findFirst()
            .get()
            .getAttribute(1);
    if (username == null) return "";
    else return username;
  }

  /** Gets all Requests (every request DAO) */
  public static List<Request> getAllRequests() {
    LinkedList<Request> allReq = new LinkedList<>();
    allReq.addAll(new ArrayList<>(DAOPouch.getLabRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getMealDeliveryRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getMedicalEquipmentRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getPatientTransportRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getSanitationRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getMedicineRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getEquipmentCleaningDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getSecurityRequestDAO().getAll().values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getLanguageRequestDAO().getAll().values()));
    return allReq;
  }

  /**
   * Gets every request by locationID
   *
   * @param locationID - the locationID of the requests
   */
  public static List<Request> getFilteredRequests(String locationID) {
    LinkedList<Request> allReq = new LinkedList<>();
    allReq.addAll(new ArrayList<>(DAOPouch.getLabRequestDAO().filter(3, locationID).values()));
    allReq.addAll(
        new ArrayList<>(DAOPouch.getMealDeliveryRequestDAO().filter(3, locationID).values()));
    allReq.addAll(
        new ArrayList<>(DAOPouch.getMedicalEquipmentRequestDAO().filter(3, locationID).values()));
    allReq.addAll(
        new ArrayList<>(DAOPouch.getPatientTransportRequestDAO().filter(3, locationID).values()));
    allReq.addAll(
        new ArrayList<>(DAOPouch.getSanitationRequestDAO().filter(3, locationID).values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getMedicineRequestDAO().filter(3, locationID).values()));
    allReq.addAll(
        new ArrayList<>(DAOPouch.getEquipmentCleaningDAO().filter(3, locationID).values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getLanguageRequestDAO().filter(3, locationID).values()));
    allReq.addAll(new ArrayList<>(DAOPouch.getSecurityRequestDAO().filter(3, locationID).values()));
    return allReq;
  }

  /**
   * Searches for requests based on their exact name
   *
   * @param reqType - The name of the request
   */
  public static List<Request> searchRequestsByName(String reqType) {
    List<Request> allReq = getAllRequests();
    LinkedList<Request> searchReq = new LinkedList<>();
    for (Request request : allReq) {
      if (request.requestType().equals(reqType)) {
        searchReq.add(request);
      }
    }
    return searchReq;
  }

  /**
   * Searches for requests based on the assignee's name and returns list of all tasks assigned to
   * them Only returns tasks which have not been marked as "COMPLETED" or "CANCELLED"
   *
   * @param assignee
   * @return
   */
  public static List<Request> searchRequestsByAssignee(String assignee) {
    List<Request> allReq = getAllRequests();
    LinkedList<Request> assigneeReq = new LinkedList<>();
    for (Request request : allReq) {
      // If correct user and it hasn't been marked as completed or cancelled
      try {
        if (request.getAssigneeID().equals(assignee)
            && (!request.getStatus().equals("COMPLETED")
                || !request.getStatus().equals("CANCELLED"))) assigneeReq.add(request);
      } catch (Exception e) {
        App.LOG.info(
            "Something went wrong trying to find requests for "
                + assignee
                + " With request "
                + request.getNodeID()
                + " In "
                + request.requestType());
      }
    }
    return assigneeReq;
  }

  /**
   * Filters Medical Equipment in a certain LOCATION by a given TYPE and a given CLEANSTATUS
   *
   * @param loc location to check for equipment
   * @param medicalEquipmentDAO database DAO
   * @param type type of equipment to look for
   * @param cleanStatus the desired clean status
   * @return A Map containing the filtered Equipment
   */
  public static Map<String, MedicalEquipment> filterEquipByTypeAndStatus(
      Location loc, DAO<MedicalEquipment> medicalEquipmentDAO, String type, String cleanStatus) {
    Map<String, MedicalEquipment> tempMap =
        medicalEquipmentDAO.filter(
            medicalEquipmentDAO.filter(6, loc.getAttribute(1)), 5, cleanStatus);
    tempMap = medicalEquipmentDAO.filter(tempMap, 3, type);
    return tempMap;
  }

  /**
   * determines if an AUTOMATIC equip req has already been submitted and added to the DAO
   *
   * @param requestToCheck the request to check
   * @return true if it is already in the DAO
   */
  public static boolean automaticRequestAlreadyExists(MedicalEquipmentRequest requestToCheck) {
    Map<String, MedicalEquipmentRequest> requestMap =
        DAOPouch.getMedicalEquipmentRequestDAO().getAll();

    for (MedicalEquipmentRequest request : requestMap.values()) {
      // This should be enough to determine that the automatic request has already been submitted
      if (request.getEquipmentID().equals(requestToCheck.getEquipmentID())
          && request.getPriority().equals(requestToCheck.getPriority())) return true;
    }
    return false;
  }

  /**
   * Gets the location of an equipment
   *
   * @param equipment - The Medical Equipment table object
   */
  public static Location getLocationOfEquip(MedicalEquipment equipment) {
    return DAOPouch.getLocationDAO().get(equipment.getLocationID());
  }

  public static List<String> getAllPlebs() {
    DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();
    Map<String, Employee> map = new HashMap<>();

    for (int i = 0; i < SecurityController.getUser().getSecurityClearance(); i++) {
      map.putAll(employeeDAO.filter(6, String.valueOf(i)));
    }
    return new ArrayList<>(map.keySet());
  }

  public static Employee getEmployee(String username) throws IllegalAccessException {
    Account account = DAOPouch.getAccountDAO().get(username);
    List<Employee> employees =
        new ArrayList<Employee>(
            DAOPouch.getEmployeeDAO().filter(1, account.getAttribute(2)).values());
    if (!(employees.size() == 1))
      throw new IllegalAccessException("Duplicate or No Employee Account(s) Found: " + username);
    else return employees.get(0);
  }

  public static Account getUserAccount() {
    return DAOPouch.getAccountDAO().get(getUsername());
  }

  /**
   * Finds the closest medical equipment to your current location and what you are looking for
   *
   * @param type
   * @param location
   * @return
   */
  public static String getClosestMedicalEquipment(String type, String location) {
    AStar ppHelper = new AStar();
    List<MedicalEquipment> equipmentList =
        new ArrayList<>(DAOPouch.getMedicalEquipmentDAO().filter(3, type).values());
    Double bestDistance = Double.MAX_VALUE;
    Double currentDistance = 0.0;
    String bestNodeID = equipmentList.get(0).getNodeID();
    Double previousBest = Double.MAX_VALUE;
    for (MedicalEquipment equipment : equipmentList) {
      if (DAOPouch.getLocationDAO().get(equipment.getLocationID()) != null) {
        if (DAOPouch.getLocationDAO().get(equipment.getLocationID()).getXcoord() != -1) {
          String startLocation = location;

          List<String> ppPath = ppHelper.getPath(equipment.getLocationID(), startLocation);

          for (int i = 0; i < ppPath.size() - 2; i++) {
            currentDistance += ppHelper.getDistance(ppPath.get(i), ppPath.get(i + 1));
          }
          if (currentDistance < bestDistance) {
            bestNodeID = equipment.getNodeID();
            previousBest = bestDistance;
            bestDistance = currentDistance;
            if (previousBest - bestDistance < 800
                && Math.abs(bestDistance - Double.MAX_VALUE) < 1.0) {
              break;
            }
          }
          currentDistance = 0.0;
        }
      }
    }
    return bestNodeID;
  }

  /**
   * Gets a list of all equipment that is dirty in the DAOs
   *
   * @param type - the type of equipment to check for
   */
  public static List<MedicalEquipment> getDirtyEquipment(MedicalEquipment.EquipmentType type) {
    List<MedicalEquipment> dirty = new ArrayList<>();
    List<MedicalEquipment> ofType =
        new ArrayList<>(DAOPouch.getMedicalEquipmentDAO().filter(3, type.name()).values());
    ofType.forEach(
        equip -> {
          if (equip.getCleanStatus().equals(MedicalEquipment.CleanStatus.UNCLEAN)) dirty.add(equip);
        });
    return dirty;
  }

  /**
   * Gets a list of all equipment on a specified floor that is dirty in the DAOs
   *
   * @param type - the type of equipment to check for
   * @param floor - the floor number/string
   */
  public static List<MedicalEquipment> getDirtyEquipmentByFloor(
      MedicalEquipment.EquipmentType type, String floor) {
    List<MedicalEquipment> toReturn = new ArrayList<>();

    List<MedicalEquipment> byType = getDirtyEquipment(type);
    List<String> locIDSFloor =
        DAOPouch.getLocationDAO().filter(4, floor).values().stream()
            .map(Location::getNodeID)
            .collect(Collectors.toList());
    for (MedicalEquipment equip : byType) {
      if (locIDSFloor.contains(equip.getLocationID())) {
        toReturn.add(equip);
      }
    }
    return toReturn;
  }

  /**
   * Gets all equipment of a type on a specified floor
   *
   * @param type - the type of equipment
   * @param floor - the floor as a number/string
   */
  public static List<MedicalEquipment> getEquipmentOnFloor(
      MedicalEquipment.EquipmentType type, String floor) {
    List<MedicalEquipment> toReturn = new ArrayList<>();
    List<String> locIDSFloor =
        DAOPouch.getLocationDAO().filter(4, floor).values().stream()
            .map(Location::getNodeID)
            .collect(Collectors.toList());
    for (MedicalEquipment equip : DAOPouch.getMedicalEquipmentDAO().getAll().values()) {
      if (equip.getEquipmentType().equals(type) && locIDSFloor.contains(equip.getLocationID()))
        toReturn.add(equip);
    }
    return toReturn;
  }

  public static List<Request> getRequestsByFloor(String floor) {
    List<Request> allReqs = new ArrayList<>();
    Collection<String> floorIDS =
        DAOPouch.getLocationDAO().filter(4, floor).values().stream()
            .map(Location::getNodeID)
            .collect(Collectors.toList());
    getAllRequests()
        .forEach(
            r -> {
              if (floorIDS.contains(r.getRoomID())) allReqs.add(r);
            });
    return allReqs;
  }
}

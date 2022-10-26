package edu.wpi.DapperDaemons.controllers.helpers;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.requests.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverdueHandler {
  private static DAO<LabRequest> labRequestDAO;
  private static DAO<MealDeliveryRequest> mealDeliveryRequestDAO;
  private static DAO<MedicalEquipmentRequest> medicalEquipmentRequestDAO;
  private static DAO<MedicineRequest> medicineRequestDAO;
  private static DAO<PatientTransportRequest> patientTransportRequestDAO;
  private static DAO<SanitationRequest> sanitationRequestDAO;
  private static DAO<EquipmentCleaning> equipmentCleaningDAO;
  private static OverdueHandler handler;
  private static int dateRepresentation;
  private static Timer overdueTimer;
  private static final int updateTime = 180; // Every 3 minutes check if something is overdue

  public static void init() {
    labRequestDAO = DAOPouch.getLabRequestDAO();
    mealDeliveryRequestDAO = DAOPouch.getMealDeliveryRequestDAO();
    medicalEquipmentRequestDAO = DAOPouch.getMedicalEquipmentRequestDAO();
    medicineRequestDAO = DAOPouch.getMedicineRequestDAO();
    patientTransportRequestDAO = DAOPouch.getPatientTransportRequestDAO();
    sanitationRequestDAO = DAOPouch.getSanitationRequestDAO();
    equipmentCleaningDAO = DAOPouch.getEquipmentCleaningDAO();
    handler = new OverdueHandler();

    updateOverdue();
  }

  public static void updateOverdue() {
    if (overdueTimer != null) overdueTimer.cancel();
    overdueTimer = new Timer();
    overdueTimer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            App.LOG.info("Checking for overdue things");
            try {
              Date dateDat = new Date();
              SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
              dateRepresentation = Integer.parseInt(dateFormat.format(dateDat));
              handler.checkLabReq();
              handler.checkEquipmentCleanReq();
              handler.checkMealReq();
              handler.checkMedicineReq();
              handler.checkMedicalEqReq();
              handler.checkPatientTransportReq();
              handler.checkSanitaitonReq();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        },
        0,
        updateTime * 1000 // every 180 * 1000ms so 180 seconds
        );
  }

  private boolean isFrontIsAMonth(String frontTwo) {
    if (Integer.parseInt(frontTwo.substring(0, 1)) >= 2
        && Integer.parseInt(frontTwo.substring(0, 1)) <= 9) {
      // if front is a month
      return true;
    }
    return false;
  }

  private List<Request> checkOverdue(List<Request> requestList) {
    List<Request> overdueList = new ArrayList<>();
    for (Request req : requestList) {
      // Convert date into the same format to get a linear reqresentation
      int dateOf;
      try {
        String reqDate = req.getDateNeeded();
        if (reqDate.length() == 6) {
          reqDate = "0" + reqDate.substring(0, 1) + "0" + reqDate.substring(1);
        }
        if (reqDate.length() == 7) {
          String frontString = reqDate.substring(0, 3);
          if (isFrontIsAMonth(frontString.substring(0, 2))) { // If front number is a month
            frontString = "0" + frontString;
          } else {
            if (Integer.parseInt(frontString.substring(0, 2)) == 10) { // If its october
              frontString = "100" + frontString.substring(2);
            } else {
              if (Integer.parseInt(frontString.substring(0, 2))
                  >= 13) { // If the first two aren't a month
                frontString = "0" + frontString;
              } else {
                if (frontString.substring(2).equals("0")) { // If the second one is for sure a date
                  frontString = "0" + frontString;
                } else {
                  frontString = frontString.substring(0, 2) + "0" + frontString.substring(2);
                }
              }
            }
          }
          reqDate = frontString + reqDate.substring(3);
        }
        dateOf = Integer.parseInt(reqDate.substring(4) + reqDate.substring(0, 4));

      } catch (Exception e) {
        e.printStackTrace();
        App.LOG.info("The date for request " + req.getNodeID() + " Was wrong or nonexistent");
        dateOf = dateRepresentation;
      }
      if (dateOf < dateRepresentation
          && (req.getStatus().equals(Request.RequestStatus.REQUESTED)
              || req.getStatus().equals(Request.RequestStatus.IN_PROGRESS))
          && !req.getPriority().equals(Request.Priority.OVERDUE)) {
        // If the due date has passed, is in progress or requested, and
        // is not overdue
        overdueList.add(req); // Add the req to the list
        App.LOG.info("Request " + req.getNodeID() + " Was overdue, updating priority");
      }
    }
    return overdueList;
  }

  private void checkLabReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(labRequestDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      LabRequest overdueReq = labRequestDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      labRequestDAO.update(overdueReq);
    }
  }

  private void checkMealReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(mealDeliveryRequestDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      MealDeliveryRequest overdueReq = mealDeliveryRequestDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      mealDeliveryRequestDAO.update(overdueReq);
    }
  }

  private void checkMedicalEqReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(medicalEquipmentRequestDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      MedicalEquipmentRequest overdueReq = medicalEquipmentRequestDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      medicalEquipmentRequestDAO.update(overdueReq);
    }
  }

  private void checkMedicineReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(medicineRequestDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      MedicineRequest overdueReq = medicineRequestDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      medicineRequestDAO.update(overdueReq);
    }
  }

  private void checkPatientTransportReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(patientTransportRequestDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      PatientTransportRequest overdueReq = patientTransportRequestDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      patientTransportRequestDAO.update(overdueReq);
    }
  }

  private void checkSanitaitonReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(sanitationRequestDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      SanitationRequest overdueReq = sanitationRequestDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      sanitationRequestDAO.update(overdueReq);
    }
  }

  private void checkEquipmentCleanReq() throws SQLException {
    List<Request> requestList = new ArrayList<>(equipmentCleaningDAO.getAll().values());

    requestList = checkOverdue(requestList);
    for (Request req : requestList) {
      EquipmentCleaning overdueReq = equipmentCleaningDAO.get(req.getNodeID());
      overdueReq.setPriority(Request.Priority.OVERDUE);
      equipmentCleaningDAO.update(overdueReq);
    }
  }
}

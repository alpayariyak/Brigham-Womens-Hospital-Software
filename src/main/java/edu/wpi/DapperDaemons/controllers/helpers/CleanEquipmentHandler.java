package edu.wpi.DapperDaemons.controllers.helpers;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.requests.EquipmentCleaning;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.text.SimpleDateFormat;
import java.util.*;

public class CleanEquipmentHandler {
  private static DAO<MedicalEquipment> medicalEquipmentDAO;
  private static DAO<EquipmentCleaning> cleaningDAO;
  private static String nextDate;
  private static Timer cleaningTimer;
  public static final int cleanTimeCheck = 300;

  public static void init() {
    medicalEquipmentDAO = DAOPouch.getMedicalEquipmentDAO();
    cleaningDAO = DAOPouch.getEquipmentCleaningDAO();
    Date dateDat = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    int nextDateRepresentation = Integer.parseInt(dateFormat.format(dateDat)) + 1;
    nextDate = Integer.toString(nextDateRepresentation);

    checkAndUpdateCleanRequests();
  }

  public static void checkAndUpdateCleanRequests() {
    if (cleaningTimer != null) cleaningTimer.cancel();
    cleaningTimer = new Timer();
    cleaningTimer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            App.LOG.info("Checking if things must be cleaned");
            List<MedicalEquipment> cleanEquipment =
                new ArrayList<>(medicalEquipmentDAO.filter(5, "CLEAN").values());
            List<MedicalEquipment> uncleanEquipment =
                new ArrayList<>(medicalEquipmentDAO.filter(5, "UNCLEAN").values());
            List<MedicalEquipment> inProgressEquipment =
                new ArrayList<>(medicalEquipmentDAO.filter(5, "INPROGRESS").values());

            for (MedicalEquipment.EquipmentType type : MedicalEquipment.EquipmentType.values()) {
              App.LOG.info("Checking " + type.name() + " For if it needs cleaning");
              List<MedicalEquipment> uncleanEqOfType =
                  new ArrayList<>(
                      medicalEquipmentDAO.filter(uncleanEquipment, 3, type.name()).values());
              List<MedicalEquipment> cleanEqOfType =
                  new ArrayList<>(
                      medicalEquipmentDAO.filter(cleanEquipment, 3, type.name()).values());
              List<MedicalEquipment> inProgressEqOfType =
                  new ArrayList<>(
                      medicalEquipmentDAO.filter(inProgressEquipment, 3, type.name()).values());

              if (uncleanEqOfType.size() + inProgressEqOfType.size() > cleanEqOfType.size() / 2) {
                int highPriorityNeeded = cleanEqOfType.size() / 2;
                while (highPriorityNeeded > 0) {
                  if (!inProgressEqOfType
                      .isEmpty()) { // While its not empty, set the ones in it to be high priority
                    // Set all in progress uncleanEQOfType to High priority and the rest of either
                    // dirty or
                    // in progress to medium
                    MedicalEquipment makeCleaningRequestFor =
                        inProgressEquipment.remove(0); // remove the bottom from the stack
                    EquipmentCleaning requestInProgress =
                        new ArrayList<>(
                                cleaningDAO
                                    .filter(9, makeCleaningRequestFor.getSerialNumber())
                                    .values())
                            .get(0);
                    if (requestInProgress
                        .getPriority()
                        .equals(Request.Priority.HIGH)) { // carry on if its already HIGH
                      App.LOG.info("This Request is already a high priority");
                    } else {
                      cleaningDAO.delete(requestInProgress); // Deletes the current
                      EquipmentCleaning request =
                          new EquipmentCleaning(
                              Request.Priority.HIGH,
                              makeCleaningRequestFor.getLocationID(),
                              requestInProgress.getRequesterID(),
                              requestInProgress.getAssigneeID(),
                              requestInProgress.getNotes(),
                              makeCleaningRequestFor.getSerialNumber(),
                              makeCleaningRequestFor.getEquipmentType(),
                              MedicalEquipment.CleanStatus.INPROGRESS,
                              nextDate);
                      cleaningDAO.add(request);
                    }
                  } else {
                    MedicalEquipment makeCleaningRequestFor =
                        uncleanEquipment.remove(0); // remove the bottom from the stack
                    EquipmentCleaning request =
                        new EquipmentCleaning(
                            Request.Priority.HIGH,
                            makeCleaningRequestFor.getLocationID(),
                            "admin",
                            "Needed",
                            "Needed Now!!",
                            makeCleaningRequestFor.getSerialNumber(),
                            makeCleaningRequestFor.getEquipmentType(),
                            MedicalEquipment.CleanStatus.INPROGRESS,
                            nextDate);
                    cleaningDAO.add(request);
                    makeCleaningRequestFor.setCleanStatus(MedicalEquipment.CleanStatus.INPROGRESS);
                    medicalEquipmentDAO.update(makeCleaningRequestFor);
                  }
                  highPriorityNeeded--;
                }
              }
            }
          }
        },
        0,
        cleanTimeCheck * 1000 // happens every 5 mins to check if cleaning is necessary
        );
  }
}

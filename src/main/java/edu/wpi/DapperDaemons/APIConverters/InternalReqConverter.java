package edu.wpi.DapperDaemons.APIConverters;

import edu.wpi.DapperDaemons.entities.requests.PatientTransportRequest;
import edu.wpi.cs3733.D22.teamB.api.Request;

public class InternalReqConverter extends Converter {

  public InternalReqConverter() {}

  public static String employeeID;
  public static String patientID;

  /**
   * Converts from a team B patient req to our patient req
   *
   * @param internalRequest team b req to be converted
   * @return converted req
   */
  public static PatientTransportRequest convert(Request internalRequest) {

    String currID;

    if (employeeID == null) currID = "admin"; // default to admin
    else currID = employeeID;

    return new PatientTransportRequest(
        parsePriority(internalRequest.getPriority()),
        internalRequest.getStartLocation().getNodeID(),
        currID,
        currID,
        internalRequest.getInformation(),
        patientID,
        internalRequest.getFinishLocation().getNodeID(),
        determineDateNeeded(120));
  }

  /**
   * converts int priority to priority enum
   *
   * @param priority integer priority
   * @return the corresponding priority enum
   */
  public static edu.wpi.DapperDaemons.entities.requests.Request.Priority parsePriority(
      int priority) {
    if (priority == 1 || priority == 2)
      return edu.wpi.DapperDaemons.entities.requests.Request.Priority.LOW;
    else if (priority == 3) return edu.wpi.DapperDaemons.entities.requests.Request.Priority.MEDIUM;
    else if (priority == 4 || priority == 5)
      return edu.wpi.DapperDaemons.entities.requests.Request.Priority.HIGH;
    else return edu.wpi.DapperDaemons.entities.requests.Request.Priority.OVERDUE;
  }
}

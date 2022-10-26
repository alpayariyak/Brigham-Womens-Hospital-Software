package edu.wpi.DapperDaemons.APIConverters;

import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.entities.requests.SanitationRequest;
import edu.wpi.cs3733.D22.teamD.request.IRequest;
import edu.wpi.cs3733.D22.teamD.request.SanitationIRequest;

/** This class contains methods to convert between the two different Sanitation Request */
public class SanitationReqConverter extends Converter {

  public SanitationReqConverter() {}

  /**
   * Converts from a SanitationIRequest (Team D API) to a SanitationRequest (our object)
   *
   * @param request API sanitation request to be converted
   * @return the converted request
   */
  public static SanitationRequest convert(SanitationIRequest request) {
    return new SanitationRequest(
        parsePriority(request),
        request.getRoomID(),
        request.getRequesterID().substring(0, request.getRequesterID().indexOf(" ")).trim(),
        request.getAssigneeID().substring(0, request.getAssigneeID().indexOf(" ")).trim(),
        "Created Using API",
        request.getSanitationType(),
        determineDateNeeded(1440));
  }

  /**
   * Converts from an API priority to our priority
   *
   * @param request request with the given priority
   * @return the converted priority
   */
  public static Request.Priority parsePriority(SanitationIRequest request) {

    IRequest.Priority priority = request.getPriority();

    if (priority.toString().equals("HIGH")) return Request.Priority.HIGH;
    else if (priority.toString().equals("MEDIUM")) return Request.Priority.MEDIUM;
    else if (priority.toString().equals("LOW")) return Request.Priority.LOW;
    else if (priority.toString().equals("OVERDUE")) return Request.Priority.OVERDUE;
    else return Request.Priority.OVERDUE;
  }
}

package edu.wpi.DapperDaemons.APIConverters;

import edu.wpi.DapperDaemons.entities.requests.PatientTransportRequest;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.cs3733.D22.teamZ.api.entity.ExternalTransportRequest;

/** Converts from Team Z's external patient request to our external patient requests */
public class ExternalReqConverter extends Converter {

  public ExternalReqConverter() {}

  /**
   * Converts Team Z's External Patient Request to our Patient Request Class
   *
   * @param extRequest request to be converted
   * @param origin origin location
   * @param destination destination location
   * @return the converted request object
   */
  public static PatientTransportRequest convert(
      ExternalTransportRequest extRequest, String origin, String destination) {

    return new PatientTransportRequest(
        Request.Priority.LOW,
        origin,
        extRequest.getIssuerID(),
        extRequest.getHandlerID(),
        "Called using Team-Z API. Using transport method: "
            + extRequest.getTransportMethod().toString(),
        extRequest.getPatientID(),
        destination,
        determineDateNeeded(120));
  }
}

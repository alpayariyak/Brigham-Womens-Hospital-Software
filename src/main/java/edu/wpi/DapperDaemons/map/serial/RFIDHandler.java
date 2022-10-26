package edu.wpi.DapperDaemons.map.serial;

import edu.wpi.DapperDaemons.backend.SHA;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.ArduinoTimeOutException;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.UnableToConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/** Handles RFID scan and determines validity */
public class RFIDHandler {

  private Employee user;
  private List<String> UIDs;

  public RFIDHandler(Employee user) {
    this.user = user;
    this.UIDs = new ArrayList<String>();
    this.UIDs.add(
        "9a1b036b82baba3177d83c27c1f7d0beacaac6de1c5fdcc9680c49f638c5fb9"); // 256-bit hash of the
    // UID
  }

  /**
   * Opens serial communication to get incoming UID and checks if it is valid Uses the SHA256 hash
   * for security
   *
   * @return true if the scan was valid, false otherwise
   */
  public boolean scan(String COM)
      throws UnableToConnectException, ArduinoTimeOutException, NoSuchAlgorithmException {
    SerialCOM reader = new SerialCOM();
    String inputID = reader.readData(COM);
    return UIDs.contains(SHA.toHexString(SHA.getSHA(inputID)));
  }
}

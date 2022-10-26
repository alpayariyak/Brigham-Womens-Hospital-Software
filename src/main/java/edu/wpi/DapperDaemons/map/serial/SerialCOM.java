package edu.wpi.DapperDaemons.map.serial;

import arduino.*;
import com.fazecast.jSerialComm.SerialPort;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.ArduinoTimeOutException;
import edu.wpi.DapperDaemons.map.serial.ArduinoExceptions.UnableToConnectException;
import java.util.ArrayList;
import java.util.List;

/** Handles interaction with Arduino */
public class SerialCOM {
  String input;

  public SerialCOM() {
    this.input = "";
  }

  /**
   * @param COM port that contains an arduino
   * @return the string received by the Java program from the arduino
   * @throws ArduinoTimeOutException 10 seconds have gone by and no RFID card was scanned
   * @throws UnableToConnectException the connection is interrupted
   */
  public String readData(String COM) throws ArduinoTimeOutException, UnableToConnectException {

    Arduino arduino = new Arduino(COM, 9600);

    // Collect System time
    long startTime = System.currentTimeMillis();

    // Open Connection
    boolean connection = arduino.openConnection();

    // Throw exception if unable to connect to serial port
    if (!connection) {
      arduino.closeConnection(); // Should be impossible to reach
      throw new UnableToConnectException();
    }
    // otherwise, attempt to collect data, timeout if it has been over 10 seconds
    else {
      while (this.input.equals("")) {
        input = arduino.serialRead();
        if (containsLetters(input)) input = "";
        long currentTime = System.currentTimeMillis();
        if (currentTime >= startTime + 10000) {
          arduino.closeConnection();
          throw new ArduinoTimeOutException();
        }
      }
      arduino.closeConnection();
      input = input.trim();
    }
    return input;
  }

  /**
   * Obtains a list of open coms
   *
   * @return a list of available coms that have SOMETHING (not necessarily an Arduino) connected to
   *     them
   */
  public List<String> getAvailableCOMs() {
    List<String> available = new ArrayList<>();
    SerialPort[] ports = SerialPort.getCommPorts();
    for (SerialPort s : ports) {
      if (s.openPort()) {
        available.add(s.getSystemPortName().trim());
        //        System.out.println(s.getSystemPortName().trim());
      }
      s.closePort();
    }
    return available;
  }

  /**
   * check all available ports to attempt to establish a connection with the arduino. The Arduino is
   * continuously sending an "BUFFER" string so this method attempts to check if a "BUFFER" string
   * is being sent on a list of open com ports
   *
   * @return the arduino object containing the valid port that has the arduino on it
   * @throws UnableToConnectException if no ports can establish a connection
   */
  public Arduino setupArduino() throws UnableToConnectException {
    List<String> ports = this.getAvailableCOMs();
    // For each available port
    for (String port : ports) {
      // Create arduino object on that port
      Arduino arduino = new Arduino(port, 9600);
      // Check if arduino has a connection and sends buffer message
      if (arduino.openConnection() && this.checkForBuffer(arduino)) {
        arduino.closeConnection();
        return arduino;
      } else arduino.closeConnection();
    }
    throw new UnableToConnectException(); // Throw exception if unable to connect to any of the
    // ports
  }

  /**
   * checks for the arduino buffer indicator for 2.5 seconds
   *
   * @param arduino arduino connect to COM
   * @return true if buffer is received (COM is correct)
   */
  public boolean checkForBuffer(Arduino arduino) {
    long startTime = System.currentTimeMillis();
    String aIn = "";
    while (!containsLetters(aIn)) {
      aIn = arduino.serialRead();
      long currentTime = System.currentTimeMillis();
      if (currentTime >= startTime + 2000) {
        return false;
      }
    }
    System.out.println("Buffer received on " + arduino.getPortDescription());
    return true;
  }

  /**
   * Checks if a given String has a letter
   *
   * @param input string input
   * @return true if a letter is involved
   */
  public boolean containsLetters(String input) {
    if (input.isEmpty()) return false;
    else {
      for (int i = 0; i < input.length(); ++i) {
        if (Character.isLetter(input.charAt(i))) return true;
      }
    }
    return false;
  }
}

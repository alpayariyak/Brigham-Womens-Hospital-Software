package edu.wpi.DapperDaemons.backend;

import com.google.firebase.database.DatabaseReference;
import edu.wpi.DapperDaemons.entities.*;
import edu.wpi.DapperDaemons.entities.requests.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import javax.net.ssl.HttpsURLConnection;

public class ConnectionHandler {
  static Connection connection;

  static connectionType type = connectionType.CLOUD;

  public enum connectionType {
    EMBEDDED,
    CLIENTSERVER,
    CLOUD
  }

  private ConnectionHandler() {}

  public static void init() {}

  public static connectionType getType() {
    return type;
  }

  public static Connection getConnection() {
    return connection;
  }

  public static DatabaseReference getCloudConnection() {
    return FireBase.getReference();
  }

  public static boolean switchToCloudServer() {
    // Getting everything currently in the embedded or client database
    HttpsURLConnection testCon = null;
    boolean connected = false;
    try {
      URL url = new URL("https://google.com");
      testCon = (HttpsURLConnection) url.openConnection();
      testCon.connect();
      connected = true;
    } catch (MalformedURLException e) {
      System.out.println("Not connected to the internet");
    } catch (IOException e) {
      System.out.println("Not connected to the internet");
    }

    if (connected) {
      try { // TODO fix
        DAO<LabRequest> labRequestDAO = DAOPouch.getLabRequestDAO();
        DAO<MealDeliveryRequest> mealDeliveryRequestDAO = DAOPouch.getMealDeliveryRequestDAO();
        DAO<MedicalEquipmentRequest> medicalEquipmentRequestDAO =
            DAOPouch.getMedicalEquipmentRequestDAO();
        DAO<MedicineRequest> medicineRequestDAO = DAOPouch.getMedicineRequestDAO();
        DAO<PatientTransportRequest> patientTransportRequestDAO =
            DAOPouch.getPatientTransportRequestDAO();
        DAO<SanitationRequest> sanitationRequestDAO = DAOPouch.getSanitationRequestDAO();
        DAO<Account> accountDAO = DAOPouch.getAccountDAO();
        DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();
        DAO<Location> locationDAO = DAOPouch.getLocationDAO();
        DAO<MedicalEquipment> medicalEquipmentDAO = DAOPouch.getMedicalEquipmentDAO();
        DAO<Patient> patientDAO = DAOPouch.getPatientDAO();
        DAO<LocationNodeConnections> locationNodeConnectionsDAO = DAOPouch.getLocationNodeDAO();
        DAO<LanguageRequest> languageRequestDAO = DAOPouch.getLanguageRequestDAO();

        new FireBaseLoader(labRequestDAO, new LabRequest());
        new FireBaseLoader(mealDeliveryRequestDAO, new MealDeliveryRequest());
        new FireBaseLoader(medicalEquipmentRequestDAO, new MedicalEquipmentRequest());
        new FireBaseLoader(medicineRequestDAO, new MedicineRequest());
        new FireBaseLoader(patientTransportRequestDAO, new PatientTransportRequest());
        new FireBaseLoader(sanitationRequestDAO, new SanitationRequest());
        new FireBaseLoader(accountDAO, new Account());
        new FireBaseLoader(employeeDAO, new Employee());
        new FireBaseLoader(locationDAO, new Location());
        new FireBaseLoader(medicalEquipmentDAO, new MedicalEquipment());
        new FireBaseLoader(patientDAO, new Patient());
        new FireBaseLoader(locationNodeConnectionsDAO, new LocationNodeConnections());
        new FireBaseLoader(languageRequestDAO, new LanguageRequest());
      } catch (Exception e) {
        return false;
      }
      connection = null;
      type = connectionType.CLOUD;
      //      try {
      //        DAOPouch.init();
      //      } catch (IOException e) {
      //        System.out.println("DAOPouch could not initialize");
      //      }
    } else {
      return false;
    }
    return true;
  }

  public static boolean switchToClientServer() {
    try {
      Class.forName("org.apache.derby.jdbc.ClientDriver");
      System.out.println("Connecting to client");
      connection =
          DriverManager.getConnection("jdbc:derby://localhost:1527/BaW_Database;create=true");
      System.out.println("Connected to the client server");
      type = connectionType.CLIENTSERVER;
      loadToSQL();
    } catch (SQLException e) {
      System.out.println("Could not connect to the client server");
      return false;
    } catch (ClassNotFoundException e) {
      System.out.println("Driver error, try making sure you don't have any other instances open!");
      return false;
    }
    return true;
  }

  public static boolean switchToEmbedded() {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
      System.out.println("Connecting to embedded");
      connection = DriverManager.getConnection("jdbc:derby:BaW_database;create = true");
      System.out.println("Connected to the embedded server");
      type = connectionType.EMBEDDED;
      loadToSQL();
    } catch (SQLException e) {
      System.out.println("Could not connect to the embedded server");
      return false;
    } catch (ClassNotFoundException e) {
      return false;
    }
    return true;
  }

  private static void loadToSQL() throws SQLException {
    for (String filename : CSVLoader.filenames.keySet()) {
      TableObject temp = CSVLoader.filenames.get(filename);
      Statement stmt;
      stmt = ConnectionHandler.getConnection().createStatement();
      try {
        stmt.execute(temp.tableInit());
      } catch (SQLException ignored) {
        // table already created
      }
      String query = "SELECT * FROM " + temp.tableName();
      ResultSet resultSet = stmt.executeQuery(query);
      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
      int numAttributes = resultSetMetaData.getColumnCount();
      String updateStatement = "INSERT INTO " + temp.tableName() + " VALUES(";
      for (int i = 1; i < numAttributes; i++) {
        updateStatement += "?,";
      }
      updateStatement += "?)";
      PreparedStatement prepStmt =
          ConnectionHandler.getConnection().prepareStatement(updateStatement);
      DAO<TableObject> dao = DAOPouch.getDAO(temp);
      dao.getAll()
          .forEach(
              (k, v) -> {
                try {
                  if (!KeyChecker.validID(temp, v.getAttribute(1))) {
                    for (int i = 1; i <= numAttributes; i++) {
                      prepStmt.setString(i, v.getAttribute(i));
                    }
                    prepStmt.executeUpdate();
                  }
                } catch (SQLException e) {
                  e.printStackTrace();
                }
              });
      prepStmt.close();
    }
  }
}

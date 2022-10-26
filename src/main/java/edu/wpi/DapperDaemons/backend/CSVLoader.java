package edu.wpi.DapperDaemons.backend;

import com.google.firebase.database.DatabaseReference;
import com.opencsv.CSVReader;
import edu.wpi.DapperDaemons.entities.*;
import edu.wpi.DapperDaemons.entities.requests.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class CSVLoader {

  public static HashMap<String, TableObject> filenames = new HashMap<>();

  static {
    filenames.put("TowerLocations", new Location());
    filenames.put("MedEquipReq", new MedicalEquipmentRequest());
    filenames.put("LabRequest", new LabRequest());
    filenames.put("Employee", new Employee());
    filenames.put("MealDeliveryRequest", new MealDeliveryRequest());
    filenames.put("PatientTransportRequest", new PatientTransportRequest());
    filenames.put("SanitationRequest", new SanitationRequest());
    filenames.put("MedicalEquipment", new MedicalEquipment());
    filenames.put("Patient", new Patient());
    filenames.put("MedicineRequest", new MedicineRequest());
    filenames.put("Accounts", new Account());
    filenames.put("AllEdges", new LocationNodeConnections());
    filenames.put("LanguageRequests", new LanguageRequest());
    filenames.put("Notifications", new Notification());
    filenames.put("Alerts", new Alert());
    filenames.put("SecurityRequests", new SecurityRequest());
    filenames.put("EquipmentCleanRequest", new EquipmentCleaning());
  }

  private CSVLoader() {}

  public static void loadAll() throws SQLException {
    Statement stmt = ConnectionHandler.getConnection().createStatement();
    filenames.forEach(
        (k, v) -> {
          //          System.out.println("Currently on " + v.getTableName());
          try {
            try {
              stmt.execute(v.tableInit());
            } catch (SQLException e) {
              System.out.printf("%s table already created\n", v.tableName());
            }
            load(v, k + ".csv");
          } catch (IOException e) {
            e.printStackTrace();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
    stmt.close();
  }

  public static void load(TableObject type, String filename) throws IOException, SQLException {
    InputStreamReader f =
        new InputStreamReader(
            Objects.requireNonNull(CSVLoader.class.getClassLoader().getResourceAsStream(filename)));
    CSVReader read = new CSVReader(f);
    List<String[]> entries = read.readAll();
    if (entries.size() <= 1) return;
    entries.remove(0);
    String tableName = type.tableName();
    String query = "SELECT * FROM " + tableName;

    Statement stmt = ConnectionHandler.getConnection().createStatement();
    ResultSet resultSet = stmt.executeQuery(query);
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    int numAttributes = resultSetMetaData.getColumnCount();

    String updateStatement = "INSERT INTO " + tableName + " VALUES(";
    String drop = "DELETE FROM " + tableName + " WHERE ";
    drop += resultSetMetaData.getColumnLabel(1);
    drop += " = ?";
    for (int i = 1; i < numAttributes; i++) {
      updateStatement += "?,";
    }
    updateStatement += "?)";
    PreparedStatement prepStmt =
        ConnectionHandler.getConnection().prepareStatement(updateStatement);
    PreparedStatement dropStmt = ConnectionHandler.getConnection().prepareStatement(drop);
    for (String[] line : entries) {
      if (KeyChecker.validID(type, line[0])) {
        dropStmt.setString(1, line[0]);
        dropStmt.executeUpdate();
      }
      for (int i = 1; i <= numAttributes; i++) {
        prepStmt.setString(i, line[i - 1]);
      }
      prepStmt.executeUpdate();
    }

    prepStmt.close();
  }

  public static void resetFirebase() {
    filenames.forEach(
        (k, v) -> {
          try {
            loadToFirebase(v, k + ".csv");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  public static void loadToFirebase(TableObject type, String filename) throws IOException {
    InputStreamReader f =
        new InputStreamReader(
            Objects.requireNonNull(CSVLoader.class.getClassLoader().getResourceAsStream(filename)));
    CSVReader read = new CSVReader(f);
    List<String[]> entries = read.readAll();
    if (entries.size() < 1) return;
    entries.remove(0);
    String tableName = type.tableName();

    DatabaseReference ref = FireBase.getReference();
    ref = ref.child(type.tableName());
    Map<String, Map<String, String>> map = new HashMap<>();
    Map<String, String> data;
    for (String[] line : entries) {
      data = new HashMap<>();
      for (Integer i = 0; i < line.length; i++) {
        data.put(i.toString(), FireBaseCoder.encodeForFirebaseKey(line[i]));
      }
      map.put(FireBaseCoder.encodeForFirebaseKey(line[0]), data);
    }
    ref.setValueAsync(map);
  }

  public static void loadPCToFirebase(TableObject type, String filename) throws IOException {
    InputStreamReader f = new InputStreamReader(new FileInputStream(filename));
    CSVReader read = new CSVReader(f);
    List<String[]> entries = read.readAll();
    if (entries.size() < 1) return;
    String tableName = type.tableName();

    DatabaseReference ref = FireBase.getReference();
    ref = ref.child(type.tableName());
    Map<String, Map<String, String>> map = new HashMap<>();
    Map<String, String> data;
    for (String[] line : entries) {
      data = new HashMap<>();
      for (Integer i = 0; i < line.length; i++) {
        data.put(i.toString(), FireBaseCoder.encodeForFirebaseKey(line[i]));
      }
      map.put(FireBaseCoder.encodeForFirebaseKey(line[0]), data);
    }
    ref.setValueAsync(map);
  }
}

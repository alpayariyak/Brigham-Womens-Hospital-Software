package edu.wpi.DapperDaemons.backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import edu.wpi.DapperDaemons.entities.TableObject;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ORM<T extends TableObject> {
  private String tableName;

  private T type;
  private HashMap<String, T> map = new HashMap<>();
  private DatabaseReference ref;

  private int numAttributes;

  private ArrayList<String> columnNames = new ArrayList<>();

  // TODO test sql
  public ORM(T type) {
    this.type = type;
    tableName = type.tableName();
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD)) {
      ref = FireBase.getReference().child(type.tableName());
      ref.addValueEventListener(
          new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {
              System.out.println(tableName + " data updating");
              new Thread( // this is very important, so that no other event listeners overwrite
                      // this one
                      () -> {
                        try {
                          HashMap<String, T> temp = new HashMap<>();
                          HashMap<String, List<String>> snap =
                              ((HashMap<String, List<String>>) snapshot.getValue());
                          snap.forEach(
                              (k, v) -> {
                                try {
                                  TableObject x =
                                      type.newInstance(
                                          v.stream()
                                              .map(
                                                  e -> {
                                                    if (e != null) {
                                                      return FireBaseCoder.decodeFirebaseKey(e);
                                                    }
                                                    return e;
                                                  })
                                              .collect(
                                                  Collectors.toCollection(ArrayList<String>::new)));
                                  temp.put(FireBaseCoder.decodeFirebaseKey(k), (T) x);
                                } catch (Exception e) {
                                  System.out.println("Malformed Data in " + tableName + "\n" + v);
                                }
                              });
                          map = temp;
                        } catch (ClassCastException e) {
                          // TODO test if this is ever reached, I dont think it ever does oop
                          System.out.println(
                              "Caught in event listener, make sure the data is correct in firebase!\nAll of it!");
                        } catch (NullPointerException e) {
                          map = new HashMap<>();
                        }
                      })
                  .start();
            }

            @Override
            public void onCancelled(DatabaseError error) {
              System.out.println("There was an error in the event listener");
            }
          });
    } else {
      try {
        Statement stmt = ConnectionHandler.getConnection().createStatement();
        String query = "SELECT * FROM " + tableName;
        ResultSet resultSet = stmt.executeQuery(query);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        this.numAttributes = resultSetMetaData.getColumnCount();
        for (int i = 1; i <= numAttributes; i++) {
          columnNames.add(resultSetMetaData.getColumnName(i));
        }
        stmt.close();
      } catch (SQLException e) {
        System.out.println("SQLException");
      }
    }
  }

  public T get(String primaryKey) {
    return map.get(primaryKey);
  }

  public Map<String, T> getAll() {
    return map;
  }

  // TODO test sql

  public void add(T newTableObject) {
    map.put(newTableObject.getAttribute(1), newTableObject);
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD)) {
      Map<String, Map<String, String>> put = new HashMap<>();
      Map<String, String> data = new HashMap<>();
      try {
        for (int i = 1; i < 100; i++) { // not at all how we should do this, but, were lazy
          data.put(
              Integer.toString(i - 1),
              FireBaseCoder.encodeForFirebaseKey(newTableObject.getAttribute(i)));
        }
      } catch (IndexOutOfBoundsException ignored) {
      }
      put.put(FireBaseCoder.encodeForFirebaseKey(newTableObject.getAttribute(1)), data);
      ref.child(FireBaseCoder.encodeForFirebaseKey(newTableObject.getAttribute(1)))
          .setValueAsync(data);
    } else {
      try {
        String updateStatement = "INSERT INTO " + tableName + " VALUES(";
        Statement stmt = ConnectionHandler.getConnection().createStatement();
        String query = "SELECT * FROM " + tableName;
        ResultSet resultSet = stmt.executeQuery(query);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        this.numAttributes = resultSetMetaData.getColumnCount();
        for (int i = 1; i < numAttributes; i++) {
          updateStatement += "?,";
        }
        updateStatement += "?)";
        PreparedStatement prepStmt =
            ConnectionHandler.getConnection().prepareStatement(updateStatement);
        if (!KeyChecker.validID(newTableObject, newTableObject.getAttribute(1))) {
          for (int i = 1; i <= numAttributes; i++) {
            prepStmt.setString(i, newTableObject.getAttribute(i));
          }
          prepStmt.executeUpdate();
        }
        stmt.close();
        prepStmt.close();
      } catch (SQLException e) {
        System.out.println("SQLException");
      }
    }
  }
  // TODO test sql
  public void delete(String primaryKey) {
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD))
      ref.child(FireBaseCoder.encodeForFirebaseKey(primaryKey)).setValueAsync(null);
    else {
      try {
        String query = "DELETE FROM " + tableName + " WHERE " + columnNames.get(0) + " = ?";
        PreparedStatement prepStmt = ConnectionHandler.getConnection().prepareStatement(query);
        prepStmt.setString(1, primaryKey);
        prepStmt.executeUpdate();
        prepStmt.close();
      } catch (SQLException e) {
        System.out.println("SQLException");
      }
    }
  }

  // TODO test sql
  public void update(T type) {
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD)) add(type);
    else {
      try {
        Statement stmt = ConnectionHandler.getConnection().createStatement();
        String query = "SELECT * FROM " + tableName;
        ResultSet resultSet = stmt.executeQuery(query);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        this.numAttributes = resultSetMetaData.getColumnCount();
        T instance = (T) type.newInstance(new ArrayList<>());
        String statement = "UPDATE " + tableName + " SET " + columnNames.get(1) + " = ?,";
        for (int i = 2; i < numAttributes - 1; i++) {
          statement += columnNames.get(i) + " = ?,";
        }
        statement += columnNames.get(numAttributes - 1) + " = ?";
        statement += " WHERE " + columnNames.get(0) + " = ?";

        PreparedStatement prepStmt = ConnectionHandler.getConnection().prepareStatement(statement);
        T temp = get(type.getAttribute(1));
        for (int i = 1; i < numAttributes; i++) {
          prepStmt.setString(i, type.getAttribute(i + 1));
        }
        prepStmt.setString(numAttributes, type.getAttribute(1));
        prepStmt.executeUpdate();
        stmt.close();
        prepStmt.close();
      } catch (SQLException e) {
        System.out.println("SQLException");
      }
    }
  }

  public void fillFromDatabase() {
    if (!ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD)) {
      try {
        Statement stmt = ConnectionHandler.getConnection().createStatement();
        String query = "SELECT * FROM " + tableName;
        ResultSet resultSet = stmt.executeQuery(query);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        this.numAttributes = resultSetMetaData.getColumnCount();
        stmt = ConnectionHandler.getConnection().createStatement();
        query = "SELECT * FROM " + tableName;
        resultSet = stmt.executeQuery(query);
        while (resultSet.next()) {
          List<String> attributes = new ArrayList<>();
          for (int i = 1; i <= numAttributes; i++) {
            attributes.add(resultSet.getString(columnNames.get(i - 1)));
          }
          map.put(attributes.get(0), (T) type.newInstance(attributes));
        }
        stmt.close();
      } catch (SQLException e) {
        System.out.println("SQLException");
      }
    }
  }
}

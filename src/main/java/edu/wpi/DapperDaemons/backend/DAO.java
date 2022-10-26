package edu.wpi.DapperDaemons.backend;

import edu.wpi.DapperDaemons.entities.TableObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAO<T extends TableObject> {
  ORM<T> orm;
  TableObject type;

  /**
   * Creates a basic DAO which connects to the database
   *
   * @param type : The table / TableObject you wish to connect to and access
   * @throws SQLException
   * @throws IOException
   */
  public DAO(T type) {
    orm = new ORM<T>(type);
    this.type = type;
  }

  /**
   * Returns a list of everything in the table
   *
   * @return : a List of TableObjects for the type you set up the DAO with
   * @throws SQLException
   */
  public Map<String, T> getAll() {
    return orm.getAll();
  }

  /**
   * Gets a singular instance of the TableObject using its primaryKey
   *
   * @param primaryKey : the nodeID / ID String you wanted to find
   * @return
   * @throws SQLException
   */
  public T get(String primaryKey) {
    return orm.get(primaryKey);
  }

  /**
   * Updates the current TableObject you send in into the database
   *
   * @param type : the object you wish to update
   * @throws SQLException
   */
  public boolean update(T type) {
    boolean hasClearance = SecurityController.permissionToUpdate(type);
    if (hasClearance) {
      orm.update(type);
    }
    return hasClearance;
  }

  /**
   * Deletes the occurrence for the TableObject you send in
   *
   * @param type : the TableObject you wish to delete
   * @throws SQLException
   */
  public boolean delete(T type) {
    boolean hasClearance = SecurityController.permissionToDelete(type);
    if (hasClearance) {
      orm.delete(type.getAttribute(1));
    }
    return hasClearance;
  }

  /**
   * Adds the TableObject type into the database
   *
   * @param type
   * @throws SQLException
   */
  public boolean add(T type) {
    boolean hasClearance = SecurityController.permissionToAdd(type);
    if (hasClearance) {
      orm.add(type);
    }
    return hasClearance;
  }

  /**
   * Saves the current TableObject's table into the CSV file you input
   *
   * @param filename : save file name
   */
  public void save(String filename) {
    try {
      CSVSaver.save(type, filename);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Loads the information from the inputted file csv into the database */
  public void load() {
    this.orm.fillFromDatabase();
  }

  /**
   * Sorts the input list of objects you give, will add all occurences of the attribute occuring in
   * the given column to the return List
   *
   * @param list : inputted dataset to sort from
   * @param column : column you wish to search
   * @param attribute : the attribute / key / ID you wish to select
   * @return : A List of all TableObjects with the occurring attribute for the column given
   */
  public Map<String, T> filter(Map<String, T> list, int column, String attribute) {
    Map<String, T> ret = new HashMap<>();
    list.forEach(
        (k, v) -> {
          if (list.get(k).getAttribute(column).equals(attribute)) {
            ret.put(k, v);
          }
        });
    return ret;
  }

  public Map<String, T> filter(List<T> list, int column, String attribute) {
    Map<String, T> ret = new HashMap<>();
    list.forEach(
        (l) -> {
          if (l.getAttribute(column).equals(attribute)) {
            ret.put(l.getAttribute(1), l);
          }
        });
    return ret;
  }

  /**
   * Sorts the entire database and returns a List of TableObjects in which the attribute occurs in
   * the specific column you are looking in Heavy emphasis on ENTIRE DATABASE
   *
   * @param column : The column you wish to sort
   * @param attribute : the key / attribute you are searching for
   * @return : a List of all TableObjects in which the attribute occurs
   * @throws SQLException
   */
  public Map<String, T> filter(int column, String attribute) {
    return filter(orm.getAll(), column, attribute);
  }

  public Map<String, T> search(List<T> list, int column, String attribute) {
    Map<String, T> ret = new HashMap<>();
    list.forEach(
        (l) -> {
          if (l.getAttribute(column).contains(attribute)) {
            ret.put(l.getAttribute(1), l);
          }
        });
    return ret;
  }

  public Map<String, T> search(Map<String, T> list, int column, String attribute) {
    Map<String, T> ret = new HashMap<>();
    list.forEach(
        (k, v) -> {
          if (list.get(k).getAttribute(column).contains(attribute)) {
            ret.put(k, v);
          }
        });
    return ret;
  }

  /**
   * Sorts the entire database and returns a List of TableObjects in which the attribute occurs in
   * the specific column you are looking in Heavy emphasis on ENTIRE DATABASE
   *
   * @param column : The column you wish to sort
   * @param attribute : the key / attribute you are searching for
   * @return : a List of all TableObjects in which the attribute occurs
   * @throws SQLException
   */
  public Map<String, T> search(int column, String attribute) {
    return search(orm.getAll(), column, attribute);
  }
}

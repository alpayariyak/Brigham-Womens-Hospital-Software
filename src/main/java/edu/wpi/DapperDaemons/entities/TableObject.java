package edu.wpi.DapperDaemons.entities;

import java.util.List;

public abstract class TableObject {

  public TableObject() {}

  public abstract String tableInit();

  public abstract String tableName();

  public abstract String getAttribute(int columnNumber) throws ArrayIndexOutOfBoundsException;

  public abstract void setAttribute(int columnNumber, String newAttribute);

  public abstract TableObject newInstance(List<String> l);

  public abstract void setAttribute(String attribute, String newAttribute);
}

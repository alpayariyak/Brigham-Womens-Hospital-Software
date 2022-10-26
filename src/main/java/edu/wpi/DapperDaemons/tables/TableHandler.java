package edu.wpi.DapperDaemons.tables;

import java.lang.annotation.*;

/**
 * Class used to link data types made by the user to the JavaFX TableViews Tag methods
 * with @TableHandler(table=#,col=#) to associate a getter method with a specific part of a table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(MultiTable.class)
public @interface TableHandler {
  /** An associated number identifier for a table, can be anything */
  int table();

  /**
   * Column number associated with the function being invoked. All columns use 0-based indexing, so
   * the number should go 0,1,...etc.
   */
  int col();
}

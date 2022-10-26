package edu.wpi.DapperDaemons.tables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

public class TableHelper<R> {

  private final TableView<R> table;
  private final ObservableList<TableColumn<R, ?>> columns;
  private final int tableNum;
  private final HashMap<TableColumn<R, ?>, Object> filters;

  /**
   * Constructs a TableHelper to give a facade of useful features involving a JavaFX TableView (and
   * associated data)
   *
   * @param jfxTable - The table being used
   * @param tableNum - A number associated with the table, can be anything. Use this number as table
   *     reference in TableHandler.class
   */
  public TableHelper(TableView<R> jfxTable, int tableNum) {
    this.table = jfxTable;
    this.columns = jfxTable.getColumns();
    filters = new HashMap<>();
    jfxTable.getColumns().forEach(e -> e.setReorderable(false));
    table.setEditable(true);

    this.tableNum = tableNum;
  }

  /**
   * Maps a class with associated TableHandler tags above method declarations to table columns,
   * include @TableHandler(table=#,col=#)
   *
   * @param type - (YOUR_REQUEST).class
   */
  public void linkColumns(Class<R> type) {
    // Search for any TableHandler methods
    for (Method m : type.getDeclaredMethods()) {
      m.setAccessible(true);
      TableHandler[] annotations = m.getAnnotationsByType(TableHandler.class);

      // Find methods with the same table number association (or none)
      boolean match = false;
      int i;
      for (i = 0; i < annotations.length; i++) {
        if (annotations[i].table() == tableNum) {
          match = true;
          break;
        }
      }

      // Get the column referenced in the tag (ignore if table cannot store)
      if (match && annotations[i].col() < columns.size()) {
        TableColumn<R, Object> dispCol = (TableColumn<R, Object>) columns.get(annotations[i].col());

        // Invoke the method when the cell value is called
        dispCol.setCellValueFactory(
            cell -> {
              try {
                return new SimpleObjectProperty<>(m.invoke(cell.getValue()));
              } catch (IllegalAccessException | InvocationTargetException ignored) {
                return null;
              }
            });
      }
    }
  }

  public static int getNumCols(Object instance, int tableNum) {
    HashMap<Integer, Object> indexes = new HashMap<>();
    int count = 0;
    // Search for any TableHandler methods
    for (Method m : instance.getClass().getDeclaredMethods()) {
      m.setAccessible(true);
      TableHandler[] annotations = m.getAnnotationsByType(TableHandler.class);

      // Find methods with the same table number association (or none)
      for (int i = 0; i < annotations.length; i++) {
        if (annotations[i].table() == tableNum) {
          count++;
        }
      }
    }
    return count + 1;
  }

  public static <I> List<Object> getDataList(Class<I> type, I instance, int tableNum) {
    HashMap<Integer, Object> indexes = new HashMap<>();
    // Search for any TableHandler methods
    for (Method m : type.getDeclaredMethods()) {
      m.setAccessible(true);
      TableHandler[] annotations = m.getAnnotationsByType(TableHandler.class);

      // Find methods with the same table number association (or none)
      for (int i = 0; i < annotations.length; i++) {
        if (annotations[i].table() == tableNum && instance != null) {
          try {
            indexes.put(annotations[i].col(), m.invoke(instance));
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
    }
    Object[] toReturn = new Object[indexes.size()];
    indexes.keySet().forEach(i -> toReturn[i] = indexes.get(i));
    return List.of(toReturn);
  }

  /** Makes a column containing Strings editable */
  public void addStringEditProperty(TableColumn<R, String> column) {
    column.setEditable(true);
    column.setCellFactory(TextFieldTableCell.forTableColumn());
  }

  /** Makes a column containing integers editable */
  public void addIntegerEditProperty(TableColumn<R, Integer> column) {
    column.setEditable(true);
    column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
  }

  /** Makes a column containing longs editable */
  public void addLongEditProperty(TableColumn<R, Long> column) {
    column.setEditable(true);
    column.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
  }

  /** Converts an enum into a ComboBox of strings as an edit property of the column */
  public <E extends Enum<E>> void addEnumEditProperty(
      TableColumn<R, E> column, Class<E> enumClass) {
    column.setCellFactory(
        cb -> {
          ComboBoxTableCell<R, E> cellDropDown = new ComboBoxTableCell<>();
          cellDropDown.getItems().addAll(enumClass.getEnumConstants());
          return cellDropDown;
        });
  }

  /** Helper function to convert an enum into a list of its values as strings */
  public static <E extends Enum<E>> List<String> convertEnum(Class<E> e) {
    ArrayList<String> values = new ArrayList<>();
    Arrays.stream(e.getEnumConstants()).map(Enum::name).forEach(values::add);
    return values;
  }

  /** Adds a combo box of values as a drop-down for editing cells of the column */
  public void addDropDownEditProperty(TableColumn<R, String> column, String... values) {
    column.setCellFactory(
        cb -> {
          ComboBoxTableCell<R, String> cellDropDown = new ComboBoxTableCell<>();
          cellDropDown.getItems().addAll(values);
          return cellDropDown;
        });
  }

  /**
   * Adds a filter to the table based on a data value from the column given (Need reference to the
   * JavaFX column in order to filter by its data (including its type))
   *
   * @param column - The TableColumn to be filtered
   * @param toFilter - The thing to filter for. Ex: If a TableColumn<X,String> is given, only String
   *     cans can be filtered
   * @param <T> - The data type stored in the column (can all be cast to Object, but not
   *     recommended)
   */
  public <T> void addFilter(TableColumn<R, T> column, T toFilter) {
    filters.put(column, toFilter);
    filter();
  }

  /**
   * Adds a filter to the table based on a column number
   *
   * @param column - The column number using indexing
   * @param toFilter - The data to filter for
   */
  @Deprecated
  public void addFilter(int column, Object toFilter) {
    if (column < table.getColumns().size()) {
      filters.put(table.getColumns().get(column), toFilter);
      filter();
    }
  }

  private void filter() {
    ObservableList<R> filteredItems = FXCollections.observableArrayList(table.getItems());
    filters.forEach(
        (column, value) -> {
          for (R row : new ArrayList<>(filteredItems)) {
            Object columnValue = column.getCellObservableValue(row).getValue();
            if (columnValue == null || !columnValue.equals(value)) {
              filteredItems.remove(row);
            }
          }
        });
    table.setItems(filteredItems);
  }

  /**
   * Updates the table with new (or the same) data
   *
   * @param data - List of values that should be shown on the table (pull from database)
   */
  public void update(List<R> data) {
    table.getItems().clear();
    table.setItems(FXCollections.observableArrayList(data));
    filter();
    table.refresh();
  }

  /** Refreshes the table values */
  public void update() {
    filter();
    table.refresh();
  }
}

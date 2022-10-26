package edu.wpi.DapperDaemons.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableAdapter<R> {

  private final TableView<R> table;
  private final TableHelper<R> helper;
  private final Class<R> link;
  private final HashMap<Integer, List<Consumer<R>>> allRowProcesses;
  private final HashMap<Integer, List<Consumer<TableColumn<R, ?>>>> allColumnProcesses;

  public TableAdapter(TableView<R> table, Class<R> link, int tableNum) {
    this.table = table;
    this.link = link;
    this.helper = new TableHelper<>(table, tableNum);
    helper.linkColumns(link);

    allRowProcesses = new HashMap<>();
    allColumnProcesses = new HashMap<>();
  }

  public void addRowObserver(int row, Consumer<R> process) {
    if (table.getItems().size() <= row) return;
    if (!allRowProcesses.containsKey(row)) allRowProcesses.put(row, new ArrayList<>());
    allRowProcesses.get(row).add(process);
  }

  public void addColumnObserver(int col, Consumer<TableColumn<R, ?>> process) {
    if (table.getColumns().size() <= col) addNecessaryColumns(col);
    if (!allColumnProcesses.containsKey(col)) allColumnProcesses.put(col, new ArrayList<>());
    allColumnProcesses.get(col).add(process);
  }

  private void addNecessaryColumns(int newC) {
    int diff = newC - table.getColumns().size();
    for (int i = 0; i < diff; i++) {
      table.getColumns().add(new TableColumn<>());
    }
    helper.linkColumns(link);
  }

  public void updateAll(List<R> allItems) {
    helper.update(allItems);
    allRowProcesses.forEach(
        (n, listOfC) -> listOfC.forEach(c -> c.accept(table.getItems().get(n))));
    allColumnProcesses.forEach(
        (n, listOfC) -> listOfC.forEach(c -> c.accept(table.getColumns().get(n))));
  }
}

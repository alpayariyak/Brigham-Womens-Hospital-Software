package edu.wpi.DapperDaemons.tables;

import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.MapDashboardController;
import edu.wpi.DapperDaemons.controllers.helpers.TableListeners;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Table<R> {

  private List<R> rows = new ArrayList<>();
  private final GridPane table;
  private final int tableNum;
  private final Class<R> instance;
  private final List<Runnable> editProperties = new ArrayList<>();
  private final int padding;
  private HashMap<Integer, List<String>> filters = new HashMap<>();

  public Table(Class<R> classinst, GridPane table, int tableNum) {
    this(classinst, table, tableNum, 30);
  }

  public Table(Class<R> classinst, GridPane table, int tableNum, int padding) {
    this.instance = classinst;
    this.table = table;
    table.getColumnConstraints().clear();
    table.getRowConstraints().clear();
    this.tableNum = tableNum;
    this.padding = padding;
  }

  public void setListeners(R type) {
    TableListeners.addListener(
        ((TableObject) type).tableName(),
        TableListeners.eventListener(
            () -> {
              new Thread(
                      () -> {
                        if (type instanceof Request) {
                          List<R> req =
                              new ArrayList<R>(
                                  DAOPouch.getDAO((TableObject) type)
                                      .filter(6, "REQUESTED")
                                      .values());
                          List<R> inprog =
                              new ArrayList<R>(
                                  DAOPouch.getDAO((TableObject) type)
                                      .filter(6, "IN_PROGRESS")
                                      .values());
                          req.addAll(inprog);

                          for (int col : filters.keySet()) {
                            req.removeIf(
                                r ->
                                    !filters
                                        .get(col)
                                        .contains(((TableObject) r).getAttribute(col)));
                          }
                          difference(req, rows);
                        } else {
                          List<R> req =
                              new ArrayList<R>(
                                  DAOPouch.getDAO((TableObject) type).getAll().values());
                          difference(req, rows);
                        }
                      })
                  .start();
            }));
  }

  public void setRequestListeners() {
    List<String> allTableNames =
        DAOFacade.getAllRequests().stream()
            .map(n -> ((TableObject) n).tableName())
            .collect(Collectors.toList());
    TableListeners.addListeners(
        allTableNames,
        TableListeners.eventListener(
            () -> {
              new Thread(
                      () -> {
                        List<R> updated = (List<R>) new ArrayList<>(DAOFacade.getAllRequests());
                        for (int col : filters.keySet()) {
                          updated.removeIf(
                              r -> !filters.get(col).contains(((TableObject) r).getAttribute(col)));
                        }
                        difference(updated, rows);
                      })
                  .start();
            }));
  }

  public void setDashboardListeners() {
    List<String> allTableNames =
        DAOFacade.getAllRequests().stream()
            .map(n -> ((TableObject) n).tableName())
            .collect(Collectors.toList());
    TableListeners.addListeners(
        allTableNames,
        TableListeners.eventListener(
            () -> {
              new Thread(
                      () -> {
                        difference(
                            (List<R>) DAOFacade.getRequestsByFloor(MapDashboardController.floor),
                            rows);
                      })
                  .start();
            }));
  }

  private void difference(List<R> cons, List<R> update) {
    List<R> dif = new ArrayList<>(update);
    for (int i = 0; i < cons.size(); i++) {
      if (!update.contains(cons.get(i))) {
        boolean added = false;
        for (int j = 0; j < update.size(); j++) {
          if (update.get(j) == null) continue;
          if (((TableObject) cons.get(i))
              .getAttribute(1)
              .equals(((TableObject) update.get(j)).getAttribute(1))) {
            added = true;
            dif.remove(update.get(j));
            int finalJ = j;
            int finalI1 = i;
            Platform.runLater(
                () -> {
                  updateRow(update.get(finalJ), cons.get(finalI1));
                });
          }
        }
        if (!added) {
          int finalI = i;
          Platform.runLater(
              () -> {
                addRow(cons.get(finalI), true);
              });
        }
      } else {
        dif.remove(cons.get(i));
      }
    }
    dif.remove(null);
    for (R r : dif) {
      Platform.runLater(
          () -> {
            removeRow(r);
          });
    }
  }

  public static int getRowIndexAsInteger(Node node) {
    final var a = GridPane.getRowIndex(node);
    if (a == null) {
      return 0;
    }
    return a;
  }

  public static int getColumnIndexAsInteger(Node node) {
    final var a = GridPane.getColumnIndex(node);
    if (a == null) {
      return 0;
    }
    return a;
  }

  public void removeRow(R type) {
    final int targetRowIndex = rows.indexOf(type);
    List<Node> r = getRow(targetRowIndex);
    animate(0.92, 0.25, 0.11, r);
    table.getChildren().removeIf(node -> getRowIndexAsInteger(node) == targetRowIndex);

    // Update indexes for elements in further rows
    table
        .getChildren()
        .forEach(
            node -> {
              final int rowIndex = getRowIndexAsInteger(node);
              if (targetRowIndex < rowIndex) {
                GridPane.setRowIndex(node, rowIndex - 1);
              }
            });
    rows.remove(type);
  }

  public void removeChildren(R type) {
    final int targetRowIndex = rows.indexOf(type);
    // Remove children from row
    table.getChildren().removeIf(node -> getRowIndexAsInteger(node) == targetRowIndex);
    rows.remove(type);
  }

  public void setHeader(List<String> labels) {
    List<Node> headerRow = new ArrayList<>();
    labels.forEach(
        s -> {
          VBox item = new VBox();
          item.setAlignment(Pos.BOTTOM_LEFT);
          HBox.setHgrow(item, Priority.ALWAYS);
          item.setPrefHeight(15);
          item.setMinHeight(Control.USE_PREF_SIZE);
          item.setMaxHeight(Control.USE_PREF_SIZE);
          item.setPadding(new Insets(0, 0, -8, padding));
          Text t = new Text(s);
          t.setFont(Font.font(t.getFont().getFamily(), FontWeight.BOLD, t.getFont().getSize() + 2));
          item.getChildren().add(t);
          item.setBackground(
              new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
          headerRow.add(item);
        });
    table.getChildren().forEach(n -> GridPane.setRowIndex(n, getRowIndexAsInteger(n) + 1));
    headerRow.forEach(n -> table.addRow(0, n));
    rows.add(0, null);
  }

  public List<Node> getRow(int rowNum) {
    List<Node> ret = new ArrayList<>();
    table
        .getChildren()
        .forEach(
            node -> {
              if (getRowIndexAsInteger(node) == rowNum) {
                ret.add(node);
              }
            });
    return ret;
  }

  public List<Node> getColumn(int colNum) {
    List<Node> ret = new ArrayList<>();
    table
        .getChildren()
        .forEach(
            node -> {
              if (getColumnIndexAsInteger(node) == colNum) {
                if (rows.get(0) == null && getRowIndexAsInteger(node) == 0) {
                  // Ignore header
                } else {
                  ret.add(node);
                }
              }
            });
    return ret;
  }

  public void setRows(List<R> newRows) {
    this.rows = newRows;
    for (R r : newRows) {
      addRow(r, false);
    }
  }

  public void updateRow(R old, R newObj) {
    final int targetRowIndex = rows.indexOf(old);
    // Remove children from row
    removeChildren(old);
    addRow(targetRowIndex, newObj);
    List<Node> r = getRow(targetRowIndex);
    animate(0.98, 0.73, 0.01, r);
    rows.remove(old);
    rows.add(targetRowIndex, newObj);
    update();
  }

  private void restyleRow(List<Node> row) {
    if (row.size() == 0) return;
    ((VBox) row.get(0))
        .setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE, new CornerRadii(10, 0, 0, 10, false), Insets.EMPTY)));
    for (int i = row.size() - 2; i >= 1; i--) {
      ((VBox) row.get(i))
          .setBackground(
              new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
      row.get(i)
          .setStyle(
              "-fx-background-color: FFFFFF;"
                  + "-fx-opacity: 1;"
                  + "-fx-border-style: solid hidden solid hidden;"
                  + "-fx-border-color: #F1F0F0;"
                  + "    -fx-border-width: 1;"
                  + "-fx-effect:dropshadow(three-pass-box,rgba(0,0,0,0.1),5,0.15,3,3);");
    }
    if (row.size() > 0) {
      row.get(0)
          .setStyle(
              "-fx-background-color: FFFFFF;"
                  + "-fx-opacity: 1;"
                  + "-fx-border-style: solid hidden solid solid;"
                  + "-fx-border-color: #F1F0F0;"
                  + "-fx-border-radius: 10 0 0 10;"
                  + "-fx-border-width:1;"
                  + "-fx-background-radius: 10 0 0 10;"
                  + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.1),5,0.15,3,3);");
      ((VBox) row.get(0)).setPadding(new Insets(0, 0, 0, 15));
      setPriority(row);
    }
  }

  private void setPriority(List<Node> row) {
    try {
      VBox priority = (VBox) row.get(row.size() - 1);
      ColumnConstraints prioritycons = new ColumnConstraints();
      prioritycons.setHgrow(Priority.SOMETIMES);
      prioritycons.setFillWidth(false);
      HBox.setHgrow(priority, Priority.NEVER);
      table.getColumnConstraints().set(table.getColumnCount() - 1, prioritycons);
      Request.Priority p =
          Request.Priority.valueOf(
              ((ComboBox) ((VBox) row.get(row.size() - 2)).getChildren().get(0))
                  .getValue()
                  .toString());
      DropShadow ds =
          new DropShadow(BlurType.THREE_PASS_BOX, new Color(0, 0, 0, 0.1), 5, 0.15, 3, 3);
      priority.setEffect(ds);
      //      row.remove(row.size() - 1);
      switch (p) {
        case LOW:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(.47, .87, .47, .8),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          //          row.add(priority);
          break;
        case MEDIUM:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(.96, .93, .26, .8),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          //          row.add(priority);
          break;
        case HIGH:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(.98, .41, .38, .8),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          //          row.add(priority);
          break;
        default:
          priority.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.color(0, 0, 0, 0.5),
                      new CornerRadii(0, 10, 10, 0, false),
                      Insets.EMPTY)));
          //          row.add(priority);
          break;
      }
    } catch (ClassCastException | IllegalArgumentException ignored) {
      ((VBox) row.get(row.size() - 1))
          .setBackground(
              new Background(
                  new BackgroundFill(
                      Color.WHITE, new CornerRadii(0, 10, 10, 0, false), Insets.EMPTY)));
      row.get(row.size() - 1)
          .setStyle(
              "-fx-background-color: FFFFFF;"
                  + "-fx-opacity: 1;"
                  + "-fx-border-style: solid solid solid hidden;"
                  + "-fx-border-color: #F1F0F0;"
                  + "-fx-border-radius: 0 10 10 0;"
                  + "-fx-border-width: 1;"
                  + "-fx-background-radius: 0 10 10 0;"
                  + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.1),5,0.15,-3,3);");
      Insets norm = ((VBox) row.get(row.size() - 1)).getPadding();
      ((VBox) row.get(row.size() - 1))
          .setPadding(new Insets(norm.getTop(), 15, norm.getBottom(), norm.getLeft()));
    }
  }

  private void animate(double r, double g, double b, List<Node> row) {
    Platform.runLater(
        () -> {
          for (int i = 0; i < row.size() - 1; i++) {
            int finalI = i;
            final Animation animation =
                new Transition() {
                  {
                    setCycleDuration(Duration.millis(1500));
                    setInterpolator(Interpolator.EASE_OUT);
                  }

                  @Override
                  protected void interpolate(double frac) {
                    Color vColor =
                        new Color((1 - r) * frac + r, (1 - g) * frac + g, (1 - b) * frac + b, 1);
                    Background old = ((VBox) row.get(finalI)).getBackground();
                    ((VBox) row.get(finalI))
                        .setBackground(
                            new Background(
                                new BackgroundFill(
                                    vColor,
                                    old.getFills().get(0).getRadii(),
                                    old.getFills().get(0).getInsets())));
                  }
                };
            animation.play();
          }
        });
  }

  public void addRow(int ind, R type) {
    List<Node> row =
        RowFactory.createRow(TableHelper.getDataList(instance, type, tableNum), padding);
    table.addRow(ind, row.toArray(new Node[] {}));
    ColumnConstraints c = new ColumnConstraints();
    c.setFillWidth(true);
    c.setHgrow(Priority.ALWAYS);
    table.getColumnConstraints().clear();
    row.forEach(e -> table.getColumnConstraints().add(c));
    restyleRow(row);
  }

  public void addRow(R type) {
    addRow(type, true);
  }

  public void addRow(R type, boolean animate) {
    List<Node> row =
        RowFactory.createRow(TableHelper.getDataList(instance, type, tableNum), padding);

    table.addRow(table.getRowCount(), row.toArray(new Node[] {}));
    ColumnConstraints c = new ColumnConstraints();
    c.setFillWidth(true);
    c.setHgrow(Priority.ALWAYS);
    table.getColumnConstraints().clear();
    row.forEach(e -> table.getColumnConstraints().add(c));
    restyleRow(row);
    List<Node> r = getRow(table.getRowCount() - 1);
    if (!rows.contains(type)) {
      rows.add(type);
      if (animate) animate(0.38, 1, 0.51, r);
      update();
    }
  }

  public void update() {
    editProperties.forEach(Runnable::run);
  }

  public void addDropDownEditProperty(int col, int sqlCol, String... elements) {
    editProperties.add(
        () -> {
          List<Node> boxesInCol = getColumn(col);
          boxesInCol.forEach(
              box ->
                  box.setOnMouseClicked(
                      mouse -> {
                        // Store old value
                        Node old = ((VBox) box).getChildren().get(0);
                        ((VBox) box).getChildren().clear();

                        ComboBox<String> elems = new ComboBox<>();
                        elems.getItems().addAll(elements);
                        elems.setValue(getTextWithin(old));
                        elems.setOnAction(
                            e -> {
                              TableObject item = (TableObject) getItem(getRowIndexAsInteger(box));
                              item.setAttribute(sqlCol, elems.getValue());
                              DAOPouch.getDAO(item).update(item);

                              // Reset
                              ((VBox) box).getChildren().clear();
                              ((VBox) box).getChildren().add(old);
                              editTextWithin(
                                  old,
                                  ((TableObject) getItem(getRowIndexAsInteger(box)))
                                      .getAttribute(sqlCol));
                              addDropDownEditProperty(col, sqlCol, elements);
                            });
                        ((VBox) box).getChildren().add(elems);

                        // Toggle reset line
                        box.setOnMouseClicked(
                            toggle -> {
                              ((VBox) box).getChildren().clear();
                              ((VBox) box).getChildren().add(old);
                              editTextWithin(
                                  old,
                                  ((TableObject) getItem(getRowIndexAsInteger(box)))
                                      .getAttribute(sqlCol));
                              addDropDownEditProperty(col, sqlCol, elements);
                            });
                      }));
        });
    update();
  }

  public <E extends Enum<E>> void addEnumEditProperty(int col, int sqlCol, Class<E> enumClass) {
    editProperties.add(
        () -> {
          List<Node> boxesInCol = getColumn(col);
          for (Node box : boxesInCol) {
            Node editable = ((VBox) box).getChildren().get(0);
            if (editable instanceof ComboBox) {
              ComboBox<String> editBox = ((ComboBox<String>) editable);
              editBox.setItems(null);
              editBox.setItems(
                  FXCollections.observableArrayList(TableHelper.convertEnum(enumClass)));
              TableObject t = (TableObject) getItem(getRowIndexAsInteger(box));
              editTextWithin(editBox, t.getAttribute(2));
              editBox.setOnAction(
                  e -> {
                    TableObject item = (TableObject) getItem(getRowIndexAsInteger(box));
                    if (editBox.getValue() == null) return;
                    item.setAttribute(sqlCol, editBox.getValue());
                    DAOPouch.getDAO(item).update(item);
                    restyleRow(getRow(getRowIndexAsInteger(box)));
                  });
            }
          }
        });
    update();
  }

  private void editTextWithin(Node n, String toEdit) {
    if (n instanceof Text) ((Text) n).setText(toEdit);
    else if (n instanceof ComboBox) ((ComboBox<String>) n).setValue(toEdit);
    else if (n instanceof TextField) ((TextField) n).setText(toEdit);
  }

  private String getTextWithin(Node n) {
    if (n instanceof Text) return ((Text) n).getText();
    else if (n instanceof ComboBox) return ((ComboBox<String>) n).getValue();
    else if (n instanceof TextField) return ((TextField) n).getText();
    else return "";
  }

  public R getItem(int row) {
    return rows.get(row);
  }

  public void clear() {
    if (rows.size() == 0) return;
    int i = rows.get(0) == null ? 1 : 0;
    List<R> row2 = new ArrayList<>(rows);
    for (; i < row2.size(); i++) {
      removeRow(row2.get(i));
    }
  }

  public void addFilter(int attrNum, String toFilter) {
    if (filters.containsKey(attrNum)) filters.get(attrNum).add(toFilter);
    else filters.put(attrNum, new ArrayList<>(List.of(toFilter)));
  }

  public void filter() {
    ArrayList<R> toKeep = new ArrayList<>(rows);
    toKeep.removeIf(Objects::isNull);
    for (int col : filters.keySet()) {
      toKeep.removeIf(r -> !filters.get(col).contains(((TableObject) r).getAttribute(col)));
    }
    clear();
    toKeep.forEach(r -> addRow(r, false));
  }

  public void removeFilter(int attrNum) {
    filters.remove(attrNum);
  }

  public void clearFilters() {
    filters = new HashMap<>();
  }
}

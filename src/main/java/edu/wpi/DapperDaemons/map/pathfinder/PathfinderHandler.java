package edu.wpi.DapperDaemons.map.pathfinder;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.AppController;
import edu.wpi.DapperDaemons.controllers.MapController;
import edu.wpi.DapperDaemons.controllers.helpers.AnimationHelper;
import edu.wpi.DapperDaemons.controllers.helpers.AutoCompleteFuzzy;
import edu.wpi.DapperDaemons.controllers.helpers.FuzzySearchComparatorMethod;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.LocationNodeConnections;
import java.net.URL;
import java.util.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/** Creates a path on the map */
public class PathfinderHandler extends AppController implements Initializable {

  private static AnchorPane lineLayer;
  private static MapController controller;
  private static List<Location> locations;
  private static Double lineSize;
  private static String currentFloor;

  public final int necessaryOffsetX = -110;
  public final int necessaryOffsetY = -5;
  public static int lineOffset = 0;
  public static int numberOfLines = 0;

  /* Pathfinder handler info */
  @FXML private JFXComboBox<String> fromLocation;
  @FXML private JFXComboBox<String> toLocation;

  @FXML
  public void startFuzzySearch() {
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(fromLocation, new FuzzySearchComparatorMethod());
    AutoCompleteFuzzy.autoCompleteComboBoxPlus(toLocation, new FuzzySearchComparatorMethod());
  }

  public PathfinderHandler(AnchorPane lineLayer, MapController controller) {
    this.lineLayer = lineLayer;
    this.controller = controller;
    locations = new ArrayList<>();
  }

  public PathfinderHandler() {}

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    toLocation.setItems(FXCollections.observableArrayList(DAOFacade.getAllLocationLongNames()));
    fromLocation.setItems(FXCollections.observableArrayList(DAOFacade.getAllLocationLongNames()));
    setLineSize(6.0); // Change this if you want to change the line size
  }

  public void setLineSize(Double size) {
    this.lineSize = size;
  }

  public void setCurrentFloor(String floor) {
    this.currentFloor = floor;
  }

  @FXML
  void hoveredSubmit(MouseEvent event) {
    Node node = (Node) event.getSource();
    java.awt.Color textStart = new java.awt.Color(5, 47, 146, 255);
    java.awt.Color textEnd = new java.awt.Color(255, 255, 255, 255);
    java.awt.Color backgroundStart = new java.awt.Color(5, 47, 146, 0);
    java.awt.Color backgroundEnd = new java.awt.Color(5, 47, 146, 255);
    AnimationHelper.fadeNodeWithText(node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredSubmit(MouseEvent event) {
    Node node = (Node) event.getSource();
    java.awt.Color textStart = new java.awt.Color(5, 47, 146, 255);
    java.awt.Color textEnd = new java.awt.Color(255, 255, 255, 255);
    java.awt.Color backgroundStart = new java.awt.Color(5, 47, 146, 0);
    java.awt.Color backgroundEnd = new java.awt.Color(5, 47, 146, 255);
    AnimationHelper.fadeNodeWithText(node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }

  @FXML
  void hoveredCancel(MouseEvent event) {
    Node node = (Node) event.getSource();
    java.awt.Color textStart = new java.awt.Color(129, 160, 207, 255);
    java.awt.Color textEnd = new java.awt.Color(255, 255, 255, 255);
    java.awt.Color backgroundStart = new java.awt.Color(129, 160, 207, 0);
    java.awt.Color backgroundEnd = new java.awt.Color(129, 160, 207, 255);
    AnimationHelper.fadeNodeWithText(node, textStart, textEnd, backgroundStart, backgroundEnd, 300);
  }

  @FXML
  void unhoveredCancel(MouseEvent event) {
    Node node = (Node) event.getSource();
    java.awt.Color textStart = new java.awt.Color(129, 160, 207, 255);
    java.awt.Color textEnd = new java.awt.Color(255, 255, 255, 255);
    java.awt.Color backgroundStart = new java.awt.Color(129, 160, 207, 0);
    java.awt.Color backgroundEnd = new java.awt.Color(129, 160, 207, 255);
    AnimationHelper.fadeNodeWithText(node, textEnd, textStart, backgroundEnd, backgroundStart, 300);
  }

  @FXML
  public void showPath() {
    fromLocation.setValue(fromLocation.getValue().trim());
    toLocation.setValue(toLocation.getValue().trim());
    List<Location> filterJuan;
    List<Location> filterDos;
    Location startLoc = new Location();
    Location toLoc;
    if (!fromLocation.getValue().isEmpty()) { // If they are good, lets goo
      if (!toLocation.getValue().isEmpty()) { // Else tell the user to input an actual location
        try {
          filterJuan =
              new ArrayList<>(
                  DAOPouch.getLocationDAO().filter(7, fromLocation.getValue()).values());
          filterDos =
              new ArrayList<>(DAOPouch.getLocationDAO().filter(7, toLocation.getValue()).values());
          startLoc = filterJuan.get(0);
          toLoc = filterDos.get(0);

          String startNode;
          String endNode;
          AStar ppFinder = new AStar();
          startNode = ppFinder.findClosestPathnode(startLoc);
          endNode = ppFinder.findClosestPathnode(toLoc);

          showPather(startNode, endNode);

        } catch (Exception e) {
          e.printStackTrace();
          // TODO : Show the error message?
        }
      } else {
        showError("Invalid Destination");
      }
    } else {
      showError("Invalid Starting Location");
    }

    makeAllInVisible();
    try {
      //      System.out.println("Current floor is " + currentFloor);
      filterByFloor(currentFloor);
    } catch (Exception e) {
      e.printStackTrace();
      // Do thing
    }
  }

  @FXML
  public void clearPath() {
    makeAllInVisible();
    lineLayer.getChildren().clear();
    locations.clear();
  }

  public void showPather(String startNode, String endNode) {
    makeLinePath(startNode, endNode);
  }

  /**
   * Populates the line list / line image with all of its nodes using the AStar ppPlaner
   *
   * @param startNode
   * @param endNode
   */
  private void makeLinePath(String startNode, String endNode) {
    clearPath(); // Clears the previous path

    AStar ppPlanner = new AStar(); // The path plan planner
    // Gives all nodeID's of the path
    List<String> nodePath = ppPlanner.getPath(startNode, endNode);

    int offsetX = necessaryOffsetX + PathfinderHandler.lineOffset * PathfinderHandler.numberOfLines;
    int offsetY = necessaryOffsetY + PathfinderHandler.lineOffset * PathfinderHandler.numberOfLines;
    try {
      locations.add(DAOPouch.getLocationDAO().get(endNode));
    } catch (Exception e) {
      App.LOG.info("Something went wrong adding the last location");
    }
    for (String node : nodePath) {
      try {
        locations.add(DAOPouch.getLocationDAO().get(node));
      } catch (Exception e) {
        e.printStackTrace();
        App.LOG.info("Location " + node + " not found");
      }
    }
    try {
      locations.add(DAOPouch.getLocationDAO().get(startNode));
    } catch (Exception e) {
      System.out.println("Something went wrong adding the start location");
    }

    double overflow = 0.0;
    for (int i = 0; i < locations.size() - 1; i++) {
      // Add a new line to the list of lines
      //      System.out.println(
      //          "Position " + locations.get(i).getNodeID() + " to " + locations.get(i +
      // 1).getNodeID());
      //      System.out.println(
      //          "X Start : "
      //              + locations.get(i).getXcoord()
      //              + " Start Y: "
      //              + locations.get(i).getYcoord());
      //      System.out.println(
      //          "X End : "
      //              + locations.get(i + 1).getXcoord()
      //              + " End Y: "
      //              + locations.get(i + 1).getYcoord());
      Line pathLine;
      Circle ifNecessary;
      if (!locations
          .get(i)
          .getFloor()
          .equals(
              locations.get(i + 1).getFloor())) { // If on different floor, create point particle
        ifNecessary =
            new Circle(
                locations.get(i).getXcoord() + offsetX, locations.get(i).getYcoord() + offsetY, 6);
        ifNecessary.setFill(Color.RED);
        lineLayer.getChildren().add(ifNecessary);
        //        System.out.println("Added new point since it went up a floor");
      } else { // If on the same floor, show the path
        pathLine =
            new Line(
                locations.get(i).getXcoord() + offsetX,
                locations.get(i).getYcoord() + offsetY,
                locations.get(i + 1).getXcoord() + offsetX,
                locations.get(i + 1).getYcoord() + offsetY);
        pathLine.setFill(Color.RED);
        pathLine.setStroke(Color.RED);
        pathLine.setStrokeWidth(6.0);
        pathLine.setStrokeLineCap(StrokeLineCap.SQUARE);
        double lineLength =
            Math.sqrt(
                Math.pow(locations.get(i).getXcoord() + locations.get(i + 1).getXcoord(), 2)
                    + Math.pow(locations.get(i).getYcoord() + locations.get(i + 1).getYcoord(), 2));

        // Start is 0d 24d
        double whiteSpace = 24;
        double lineSpace = 32;
        boolean chancla = true;
        while (lineLength > 0) {
          if (chancla) {
            pathLine.getStrokeDashArray().add(lineSpace);
            lineLength -= lineSpace;
            overflow = Math.abs(lineLength);
          } else {
            pathLine.getStrokeDashArray().add(whiteSpace);
            lineLength -= whiteSpace;
            overflow = 0;
          }
        }

        double maxOffset = -pathLine.getStrokeDashArray().stream().reduce(0d, (a, b) -> a + b);
        Timeline timeline =
            new Timeline(
                new KeyFrame(
                    Duration.ZERO,
                    new KeyValue(pathLine.strokeDashOffsetProperty(), 0, Interpolator.LINEAR)),
                new KeyFrame(
                    Duration.seconds(100),
                    new KeyValue(
                        pathLine.strokeDashOffsetProperty(), maxOffset, Interpolator.LINEAR)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        lineLayer.getChildren().add(pathLine);
      }
    }
  }

  public void makeAllInVisible() {
    lineLayer.getChildren().forEach(c -> c.setVisible(false));
  }

  public Boolean checkIfConnectedNode(String nodeID) {
    List<LocationNodeConnections> connected;
    // List of walkable connected nodes as strings using their nodeID
    connected = new ArrayList(DAOPouch.getLocationNodeDAO().filter(3, nodeID).values());
    for (LocationNodeConnections connection :
        DAOPouch.getLocationNodeDAO().filter(2, nodeID).values()) {
      connected.add(connection);
    }
    if (connected.isEmpty()) return false;
    return true;
  }

  public void filterByFloor(String floor) {
    setCurrentFloor(floor);
    makeAllInVisible();
    for (int i = 0;
        i < locations.size() - 1;
        i++) { // for every child, add make the locations on this floor visible
      if (locations.get(i).getFloor().equals(floor)) {
        //        System.out.println("Showing " + locations.get(i).getNodeID());
        lineLayer.getChildren().get(i).setVisible(true);
      }
    }
  }
}

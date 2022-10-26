package edu.wpi.DapperDaemons.map.pathfinder;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.MapController;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.LocationNodeConnections;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class ShowRequestPaths {
  private static AnchorPane lineLayer;
  private static MapController controller;
  List<LocationNodeConnections> actualConnections;
  List<String> nodeGrid;

  private final Double lineSize = 4.0;
  private static String currentFloor;

  private final int necessaryOffsetX = -110;
  private final int necessaryOffsetY = -5;
  private static int lineOffset = 4;
  private static int numberOfLines = 0;
  private static int startingOffset = 0;
  public static boolean showing = false;

  private int lastStart = 0;

  private static List<Location> locations;

  public ShowRequestPaths(AnchorPane lineLayer, MapController controller) {
    this.lineLayer = lineLayer;
    this.controller = controller;
    actualConnections = new ArrayList<>();
    locations = new ArrayList<>();
    nodeGrid = new ArrayList<>();
  }

  public void showAllPaths(Location location) {
    clearPath();
    if (!showing) {
      List<Request> requests = DAOFacade.getFilteredRequests(location.getNodeID());
      AStar ppHelper = new AStar();
      startingOffset = requests.size() / 2 * lineOffset;
      for (Request request : requests) {
        if (request.requiresTransport()) {
          if (request.requestType().equals("Patient Transport Request")) {
            makeLinePath(
                request.transportFromRoomID(),
                request.getRoomID(),
                getLineColor(request.requestType()));
            numberOfLines++;
          } else {
            makeLinePath(
                request.getRoomID(),
                request.transportFromRoomID(),
                getLineColor(request.requestType()));
            numberOfLines++;
          }
        }
      }
      filterByFloor(currentFloor);
      showing = true;
    } else {
      showing = false;
    }
  }

  public void clearPath() {
    lineLayer.getChildren().clear();
    locations.clear();
    numberOfLines = 0;
    lastStart = 0;
  }

  private void makeLinePath(String startNode, String endNode, Color color) {
    AStar ppPlanner = new AStar(); // The path plan planner
    // Gives all nodeID's of the path
    Location startLoc = DAOPouch.getLocationDAO().get(startNode);
    Location endLoc = DAOPouch.getLocationDAO().get(endNode);

    AStar ppFinder = new AStar();

    //    System.out.println("Start node is " + startNode);
    //    System.out.println("End node is " + endNode);

    startNode = ppFinder.findClosestPathnode(startLoc);
    endNode = ppFinder.findClosestPathnode(endLoc);
    List<String> nodePath = ppPlanner.getPath(startNode, endNode);

    int offsetX = necessaryOffsetX + lineOffset * numberOfLines - startingOffset;
    int offsetY = necessaryOffsetY + lineOffset * numberOfLines - startingOffset;

    for (String node : nodePath) {
      try {
        locations.add(DAOPouch.getLocationDAO().get(node));
      } catch (Exception e) {
        e.printStackTrace();
        App.LOG.info("Location " + node + " not found");
      }
    }

    //    for (int i = lastStart; i < locations.size() - 1; i++) {
    //      if (locations.get(i).getNodeID().equals(locations.get(i + 1).getNodeID())) {
    //        locations.remove(i);
    //        i--;
    //      }
    //    } // Gets rid of duplicates I hope

    double overflow = 0.0;
    for (int i = lastStart; i < locations.size() - 1; i++) {
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
        ifNecessary.setFill(color);
        lineLayer.getChildren().add(ifNecessary);
        //        System.out.println("Added new point since it went up a floor");
      } else { // If on the same floor, show the path
        pathLine =
            new Line(
                locations.get(i).getXcoord() + offsetX,
                locations.get(i).getYcoord() + offsetY,
                locations.get(i + 1).getXcoord() + offsetX,
                locations.get(i + 1).getYcoord() + offsetY);
        pathLine.setFill(color);
        pathLine.setStroke(color);
        pathLine.setStrokeWidth(lineSize);
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
      lastStart++;
    }
  }

  private Color getLineColor(String reqType) {
    switch (reqType) {
      case "Equipment Cleaning Request":
        return Color.rgb(20, 33, 61);
      case "Language Request":
        return Color.rgb(240, 156, 120);
      case "Meal Delivery Request":
        return Color.rgb(0, 157, 148);
      case "Medical Equipment Request":
        return Color.rgb(153, 204, 153);
      case "Medicine Request":
        return Color.rgb(247, 127, 0);
      case "Patient Transport Request":
        return Color.rgb(100, 60, 180);
      case "Security Request":
        return Color.rgb(153, 102, 204);
      default:
        return Color.GOLD;
    }
  }

  public void setCurrentFloor(String floor) {
    this.currentFloor = floor;
  }

  public void filterByFloor(String floor) {
    setCurrentFloor(floor);
    makeAllInVisible();
    for (int i = 0;
        i < locations.size() - 1;
        i++) { // for every child, add make the locations on this floor visible
      //      System.out.println(locations.get(i).getNodeID());
      if (locations.get(i).getFloor().equals(floor)) {
        // Need to check a double filter to make sure the two nodes are connected
        if (checkConnected(locations.get(i).getNodeID(), locations.get(i + 1).getNodeID()))
          lineLayer.getChildren().get(i).setVisible(true);
      }
    }
  }

  public void makeAllInVisible() {
    lineLayer.getChildren().forEach(c -> c.setVisible(false));
  }

  private boolean checkConnected(String leftNode, String rightNode) {
    List<LocationNodeConnections> nodeConnectionsJuan =
        new ArrayList<>(DAOPouch.getLocationNodeDAO().filter(2, leftNode).values());
    nodeConnectionsJuan =
        new ArrayList<>(
            DAOPouch.getLocationNodeDAO().filter(nodeConnectionsJuan, 3, rightNode).values());
    List<LocationNodeConnections> nodeConnectionsDos =
        new ArrayList<>(DAOPouch.getLocationNodeDAO().filter(3, leftNode).values());
    nodeConnectionsDos =
        new ArrayList<>(
            DAOPouch.getLocationNodeDAO().filter(nodeConnectionsDos, 2, rightNode).values());
    return nodeConnectionsJuan.size() > 0 || nodeConnectionsDos.size() > 0;
  }
}

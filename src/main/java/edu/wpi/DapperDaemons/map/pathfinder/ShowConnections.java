package edu.wpi.DapperDaemons.map.pathfinder;

import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.MapController;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.LocationNodeConnections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class ShowConnections {
  private static AnchorPane lineLayer;
  private static MapController controller;
  List<LocationNodeConnections> actualConnections;

  public ShowConnections(AnchorPane lineLayer, MapController controller) {
    this.lineLayer = lineLayer;
    this.controller = controller;
    actualConnections = new ArrayList<>();
  }

  public void showAllLines(String floor) {
    lineLayer.getChildren().clear();
    actualConnections.clear();
    List<Location> allLocations = new ArrayList<>(DAOPouch.getLocationDAO().getAll().values());
    List<LocationNodeConnections> connections =
        new ArrayList<>(DAOPouch.getLocationNodeDAO().getAll().values());
    for (Location location : allLocations) {
      List<LocationNodeConnections> connected =
          new ArrayList(
              DAOPouch.getLocationNodeDAO().filter(connections, 3, location.getNodeID()).values());
      for (LocationNodeConnections connection :
          DAOPouch.getLocationNodeDAO().filter(connections, 2, location.getNodeID()).values()) {
        connected.add(connection);
      }
      for (LocationNodeConnections locationNodeConnection : connected) {
        String nodeID1 = locationNodeConnection.getConnectionOne();
        String nodeID2 = locationNodeConnection.getConnectionTwo();
        try {
          if (DAOPouch.getLocationDAO().get(nodeID1).getYcoord() != -1
              && DAOPouch.getLocationDAO().get(nodeID2).getYcoord() != -1) {
            actualConnections.add(locationNodeConnection);
          }
        } catch (Exception e) {
          // Do nothin
        }
      }
    }

    // Now actualConnections is filled with all valid location node connections which is pretty lit
    Map<String, Location> locationHashMap = DAOPouch.getLocationDAO().getAll();
    for (LocationNodeConnections connection : actualConnections) {
      String nodeID1 = connection.getConnectionOne();
      String nodeID2 = connection.getConnectionTwo();
      Line pathLine =
          new Line(
              locationHashMap.get(nodeID1).getXcoord(),
              locationHashMap.get(nodeID1).getYcoord(),
              locationHashMap.get(nodeID2).getXcoord(),
              locationHashMap.get(nodeID2).getYcoord());
      pathLine.setFill(Color.BLACK);
      pathLine.setStroke(Color.BLACK);
      pathLine.setStrokeWidth(6.0);
      pathLine.setStrokeLineCap(StrokeLineCap.SQUARE);
      lineLayer.getChildren().add(pathLine);
    }

    makeAllInVisible();

    filterByFloor(floor);
  }

  public void filterByFloor(String floor) {
    makeAllInVisible();
    System.out.println("Resetting for your floor");
    for (LocationNodeConnections connection : actualConnections) {
      String nodeID = connection.getConnectionOne();
      if (DAOPouch.getLocationDAO().get(nodeID).getFloor().equals(floor))
        lineLayer.getChildren().get(actualConnections.indexOf(connection)).setVisible(true);
    }
  }

  public void makeAllInVisible() {
    lineLayer.getChildren().forEach(c -> c.setVisible(false));
  }
}

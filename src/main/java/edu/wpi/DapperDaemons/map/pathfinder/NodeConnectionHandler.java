package edu.wpi.DapperDaemons.map.pathfinder;

import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.LocationNodeConnections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeConnectionHandler {

  public static void addNode(Location newNode) {
    DAO<Location> locationDAO = DAOPouch.getLocationDAO();
    List<Location> locations =
        new ArrayList<>(
            locationDAO
                .filter(4, newNode.getFloor())
                .values()); // Filters out only the current nodes floor
    locations =
        new ArrayList<>(
            locationDAO
                .filter(locations, 6, "PATH")
                .values()); // Filter out so only path type is a thing
    Location bestLocation = new Location();
    Double bestDistance = 10000.0;

    for (Location location : locations) {
      if (newNode.getFloor().equals(location.getFloor())) {
        Double currentDistance = getDistance(location, newNode);
        if (currentDistance < bestDistance) {
          bestLocation = location;
          bestDistance = currentDistance;
        }
      }
    }

    if (locations.isEmpty()) {
      App.LOG.info("New node has no connections on this floor, making its own");
      // Not adding anything to connections DAO since there should be nothing to add
    } else {
      App.LOG.info("Linking node " + newNode.getNodeID() + " To " + bestLocation.getNodeID());
      LocationNodeConnections newConnection =
          new LocationNodeConnections(
              newNode.getNodeID() + "_" + bestLocation.getNodeID(),
              newNode.getNodeID(),
              bestLocation.getNodeID());
      DAOPouch.getLocationNodeDAO().add(newConnection);
      App.LOG.info("Node should have been added to the locationNodeDAO");
    }
  }

  public static void addPathNode(Location loc) {
    // Create a follow up node to allow for pathing to this location
    Map<String, Location> locationList = DAOPouch.getLocationDAO().filter(6, "PATH");
    String numberRoomTypes =
        Integer.toString(
            DAOPouch.getLocationDAO().filter(locationList, 4, loc.getFloor()).size() + 2);
    numberRoomTypes = "000" + numberRoomTypes;
    numberRoomTypes = numberRoomTypes.substring(numberRoomTypes.length() - 3);
    System.out.println("Room number for my test is : " + numberRoomTypes);
    String nodeID = "dPATH" + numberRoomTypes + loc.getFloor();
    Location createdNode =
        new Location(
            nodeID,
            loc.getXcoord() + 100,
            loc.getYcoord() + 30,
            loc.getFloor(),
            loc.getBuilding(),
            "PATH",
            loc.getLongName() + "NODE",
            loc.getShortName() + "N");
    App.LOG.info("Adding a new location to the path stuff");
    NodeConnectionHandler.addNode(createdNode);
    DAOPouch.getLocationDAO().add(createdNode);
  }

  private static Double getDistance(Location currentLocation, Location nextLocation) {
    Double distance =
        Math.sqrt(
            Math.pow(Math.abs((currentLocation.getXcoord() - nextLocation.getXcoord())), 2)
                + Math.pow(Math.abs((currentLocation.getYcoord() - nextLocation.getYcoord())), 2));
    System.out.println(
        "Distance from  "
            + currentLocation.getNodeID()
            + " To "
            + nextLocation.getNodeID()
            + " Is "
            + distance);
    return distance;
  }
}

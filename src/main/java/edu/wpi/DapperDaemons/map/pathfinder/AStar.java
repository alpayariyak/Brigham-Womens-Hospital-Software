package edu.wpi.DapperDaemons.map.pathfinder;

import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.LocationNodeConnections;
import java.sql.SQLException;
import java.util.*;

public class AStar {
  private List<Location> locations;
  private List<LocationNodeConnections> nodeConnections;

  /**
   * Creates an AStar helper class!!
   *
   * @throws SQLException only throws exception when the getAll calls no work
   */
  public AStar() {
    try {
      locations = new ArrayList(DAOPouch.getLocationDAO().getAll().values());
      nodeConnections = new ArrayList(DAOPouch.getLocationNodeDAO().getAll().values());
    } catch (Exception e) {
      //      e.printStackTrace();
      //      System.out.println("The connection failed to locations or nodeConnections");
    }
  }

  /**
   * Returns a list of location nodeID's for the path from one place to the next
   *
   * @param startLocation the starting location's nodeID
   * @param endLocation the ending location's nodeID
   */
  public List<String> getPath(String startLocation, String endLocation) {
    PriorityQueue<WalkableNode> queue = new PriorityQueue<>();
    HashMap<String, WalkableNode> moveOrder = new HashMap<>(); // The path actually taken
    // Uses the location's nodeID as a key for the previous node's WalkableNode
    HashMap<String, Double> costSoFar = new HashMap<>();
    // Uses the location's nodeID as a key for the previous node's WalkableNode

    queue.add(new WalkableNode(startLocation, 0.0));
    moveOrder.put(startLocation, null);
    costSoFar.put(startLocation, 0.0);

    while (!queue.isEmpty()) {
      WalkableNode current = queue.remove(); // Grab the next node
      //      System.out.println("Currently at " + current.getLocationName());

      if (current.getLocationName().equals(endLocation)) {
        //        System.out.println("It Reached the goal!!");
        break;
      }

      for (String nextLocation : getNeighbors(current)) {
        Double new_cost =
            costSoFar.get(current.getLocationName())
                + getDistance(
                    current.getLocationName(),
                    nextLocation); // add the distance from current to next
        if (!costSoFar.keySet().contains(nextLocation)
            || new_cost < costSoFar.get(nextLocation)) { // If nextLocation isn't in the queue,
          //          System.out.println("Saving the location " + nextLocation + " in the path");
          // the cost is less than the one already there, and the node is not in the moveOrder yet
          costSoFar.put(nextLocation, new_cost); // save it in the costSoFar and add it to the queue
          Double priority = new_cost + Math.pow(getDistance(nextLocation, endLocation), 3);
          //          System.out.println(
          //              "Going from "
          //                  + nextLocation
          //                  + " To "
          //                  + endLocation
          //                  + " Had a distance of "
          //                  + getDistance(nextLocation, endLocation));
          // Priority is the distance from this node to the goal + costSoFar of this node
          queue.add(new WalkableNode(nextLocation, priority));
          moveOrder.put(nextLocation, current);
        }
      }
    }

    List<String> path = new ArrayList<>();
    String current = endLocation;
    if (moveOrder.get(current) != null) {
      while (moveOrder.get(current) != null) {
        path.add(current);
        current = moveOrder.get(current).getLocationName();
      }
    }
    path.add(startLocation);

    //    for (int i = 0, j = path.size() - 1; i < j; i++) {
    //      path.add(i, path.remove(j));
    //    }
    //    if (path.isEmpty()) {
    //      path.add("Path Not Found");
    //    }

    //    System.out.println("Printing out the path from A*");
    //    for (String node : path) System.out.println(node);

    return path;
  }

  /**
   * gets all neighbors by doing some fancy pancy flip flops and utilizing ALlEdges / Location Node
   * database thingy
   *
   * @param currentLocation
   * @return
   */
  private List<String> getNeighbors(WalkableNode currentLocation) {
    List<LocationNodeConnections> connected;
    List<String> walkableNode = new ArrayList<>();
    // List of walkable connected nodes as strings using their nodeID
    connected =
        new ArrayList(
            DAOPouch.getLocationNodeDAO()
                .filter(nodeConnections, 3, currentLocation.getLocationName())
                .values());
    List<Boolean> flipFlop = new ArrayList<>(Arrays.asList(new Boolean[connected.size()]));
    Collections.fill(flipFlop, Boolean.FALSE); // Helps the program decide which column to look in
    for (LocationNodeConnections connection :
        DAOPouch.getLocationNodeDAO()
            .filter(nodeConnections, 2, currentLocation.getLocationName())
            .values()) {
      flipFlop.add(Boolean.TRUE); // Switches the column to the second one to look in
      connected.add(connection);
    }
    for (LocationNodeConnections location : connected) {
      String nodeID = location.getConnectionOne();
      if (flipFlop.get(connected.indexOf(location))) nodeID = location.getConnectionTwo();

      try { // Make sure this location is actually on the map / csv file
        if (DAOPouch.getLocationDAO().get(nodeID).getYcoord() != -1) {
          walkableNode.add(nodeID);
        }
      } catch (Exception e) {
        // In order to not overpopulate the printline with a bunch of errors, I'm not printing
        // anything out here
        // This catch is just so the above get can run and get all of the correct neighbors
      }

      //      System.out.println("Neighbor node " + nodeID); // For letting me see stuff
    }
    return walkableNode; // returns connected nodeID's
  }

  public String findClosestPathnode(Location startLocation) {
    List<Location> filteredToPathNodes =
        new ArrayList<>(
            DAOPouch.getLocationDAO().filter(6, "PATH").values()); // Gives all PATH nodes
    filteredToPathNodes =
        new ArrayList<>(
            DAOPouch.getLocationDAO()
                .filter(filteredToPathNodes, 4, startLocation.getFloor())
                .values()); // Filters out to only current floor
    Double bestDist = 10000.0;
    Location bestLocation = new Location();
    for (Location location : filteredToPathNodes) {
      if (getDistance(startLocation.getNodeID(), location.getNodeID()) < bestDist) {
        bestDist = getDistance(startLocation.getNodeID(), location.getNodeID());
        bestLocation = location;
      }
      int xDistance = location.getXcoord() - startLocation.getXcoord();
      int yDistance = location.getYcoord() - startLocation.getYcoord();
      if (xDistance > 50 && xDistance < 150 && yDistance < 50 && yDistance > -50) {
        bestLocation = location;
        break;
      }
    }
    return bestLocation.getNodeID();
  }

  /**
   * Gets the distance utilizing the Location database and the XCoord and YCoord Takes in the two
   * nodeID's of the input locations
   *
   * @param currentLocation
   * @param nextLocation
   * @return
   */
  public Double getDistance(String currentLocation, String nextLocation) {
    DAO<Location> locationDAO = DAOPouch.getLocationDAO();
    // If it can't find the position, then this is basically saying that the node parser won't let
    // it break everything
    Location current =
        new Location(
            "Unknown", -1000, -1000, "Unknown", "Unknown", "Unknown", "Unknown", "Unknown");
    Location next =
        new Location("Unknown", 1000, 1000, "Unknown", "Unknown", "Unknown", "Unknown", "Unknown");
    try {
      current =
          new ArrayList<Location>(locationDAO.filter(locations, 1, currentLocation).values())
              .get(0);
      next =
          new ArrayList<Location>(locationDAO.filter(locations, 1, nextLocation).values()).get(0);
    } catch (Exception e) {
      //      e.printStackTrace();
      //      System.out.println("Couldn't find location in table");
    }
    Double distance =
        Math.sqrt(
            Math.pow(Math.abs((current.getXcoord() - next.getXcoord())), 2)
                + Math.pow(Math.abs((current.getYcoord() - next.getYcoord())), 2));
    if (!current.getFloor().equals(next.getFloor())) {
      //      System.out.println(
      //          "Its on a separate floor, adding something"); // Comment out after it works -
      // which it
      // will first
      //      System.out.println(currentLocation + "To" + nextLocation);
      distance += Math.abs(floorDistance(current.getFloor()) - floorDistance(next.getFloor()));
    }
    return distance;
  }

  private double floorDistance(String currentFloor) {
    switch (currentFloor) {
      case "L2":
        return 0;
      case "L1":
        return 200;
    }
    return Integer.parseInt(currentFloor) * 200 + 200;
  }

  private double heuristic(String current, String next) {
    return Math.abs(floorDistance(current) - floorDistance(next)) * 2;
  }
}

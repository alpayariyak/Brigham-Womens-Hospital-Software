package edu.wpi.DapperDaemons.map;

import edu.wpi.DapperDaemons.backend.DAOFacade;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.util.List;

/** Manages general map locations */
public class PositionInfo {

  public final int IMAGE_RADIUS = 40;

  public enum RoomType {
    DEPT,
    EXIT,
    HALL,
    INFO,
    LABS,
    REST,
    BATH,
    RETL,
    SERV,
    STAI,
    ELEV,
    STOR,
    PATI,
    DIRT
  }

  private Location loc;

  public PositionInfo(Location l) {
    this.loc = l;
  }

  /** Checks if a given x and y is near this position (based on floor) */
  public boolean isNear(int x, int y, String curFloor) {
    // int locFloor = Integer.parseInt(loc.getFloor());
    // int xmax = x+40;
    // int xmin = x-40;
    // int ymax = y+40;
    // int ymin = y-40;
    return loc.getFloor().equals(curFloor)
        && // On right floor
        getX() <= (x + IMAGE_RADIUS)
        && getX() >= (x - IMAGE_RADIUS)
        && getY() <= (y + IMAGE_RADIUS)
        && getY() >= (y - IMAGE_RADIUS)
        && !loc.getNodeType().equals("PATH");
  }

  public String getId() {
    return loc.getNodeID();
  }

  public String getFloor() {
    return loc.getFloor();
  }

  public int getX() {
    return loc.getXcoord();
  }

  public int getY() {
    return loc.getYcoord();
  }

  public Location getLoc() {
    return this.loc;
  }

  public String getType() {
    return loc.getNodeType();
  }

  public String getBuilding() {
    return loc.getBuilding();
  }

  public String getLongName() {
    return loc.getLongName();
  }

  public String getShortName() {
    return loc.getShortName();
  }

  @Override
  public String toString() {
    return "("
        + getX()
        + ", "
        + getY()
        + ") "
        + loc.getLongName()
        + "\n"
        + "B: "
        + loc.getBuilding()
        + " F: "
        + loc.getFloor()
        + " T: "
        + loc.getNodeType();
  }

  public Request.Priority getHighestPriority() {
    Request.Priority highestPriority = Request.Priority.LOW;
    try {
      List<Request> filteredRequests = DAOFacade.getFilteredRequests(getId());
      for (Request r : filteredRequests) {
        if (r.getPriority().compareTo(highestPriority) > 0) {
          highestPriority = r.getPriority();
        }
      }
    } catch (Exception e) {
    }
    return highestPriority;
  }
}

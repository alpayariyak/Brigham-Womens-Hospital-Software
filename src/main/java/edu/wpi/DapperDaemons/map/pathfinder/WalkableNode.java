package edu.wpi.DapperDaemons.map.pathfinder;

public class WalkableNode implements Comparable {
  private String locationName;
  private Double nodePriority;

  public WalkableNode(String locationName, Double nodePriority) {
    this.locationName = locationName;
    this.nodePriority = nodePriority;
  }

  public String getLocationName() {
    return locationName;
  }

  public Double getNodePriority() {
    return nodePriority;
  }

  @Override
  public int compareTo(Object o) {
    WalkableNode nextNode = (WalkableNode) o;
    Double nextPriority = nextNode.getNodePriority();
    if (nodePriority < nextPriority) return 1;
    else if (nodePriority > nextPriority) return -1;
    return 0;
  }
}

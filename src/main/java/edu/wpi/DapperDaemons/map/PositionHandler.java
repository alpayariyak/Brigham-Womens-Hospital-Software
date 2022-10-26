package edu.wpi.DapperDaemons.map;

import java.util.List;

public class PositionHandler {

  private List<PositionInfo> positions;
  private PositionInfo selected;

  public PositionHandler(List<PositionInfo> positions) {
    this.positions = positions;
  }

  /**
   * Searches array list of positions for a nearby location
   *
   * @param x position to look on x-axis
   * @param y position to look on y-axis
   * @return The nearby position info, null otherwise
   */
  public PositionInfo get(int x, int y, String floor) {
    for (PositionInfo p : positions) {
      if (p.isNear(x, y, floor)) {
        selected = p;
        return p;
      }
    }
    return null;
  }

  public void setSelected(PositionInfo selected) {
    this.selected = selected;
  }

  public PositionInfo getSelected() {
    return this.selected;
  }
}

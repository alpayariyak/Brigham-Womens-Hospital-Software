package edu.wpi.DapperDaemons.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MapHandler {

  private StackPane mapAssets;
  private ImageView mapView;
  private List<Image> maps;
  private int currentMap;
  public final double ZOOM_PROP = 0.025;

  /** Handles several map icons */
  public MapHandler(StackPane mapAssets, ImageView mapView, Image... maps) {
    this.mapAssets = mapAssets;
    this.maps = new ArrayList<>(Arrays.asList(maps));
    currentMap = 0;
    this.mapView = mapView;
  }

  public void setMap(String floor) {
    mapView.setImage(maps.get(mapStringToNum(floor)));
    currentMap = mapStringToNum(floor);
  }

  public Image getCurrentMap() {
    return maps.get(currentMap);
  }

  public void resetScales() {
    mapView.setScaleX(0.5);
    mapView.setScaleY(0.5);
  }

  public String getFloor() {
    return mapNumToString(currentMap);
  }

  /**
   * Gets the floor num based on the mapNum
   *
   * @param mapNum floor int
   * @return floor name as string
   */
  private String mapNumToString(int mapNum) {
    switch (mapNum) {
      case 0:
        return "L2";
      case 1:
        return "L1";
      case 2:
        return "1";
      case 3:
        return "2";
      case 4:
        return "3";
      case 5:
        return "4";
      case 6:
        return "5";
      default:
        return "ERROR";
    }
  }

  private int mapStringToNum(String map) {
    switch (map) {
      case "L2":
        return 0;
      case "L1":
        return 1;
      case "1":
        return 2;
      case "2":
        return 3;
      case "3":
        return 4;
      case "4":
        return 5;
      case "5":
        return 6;
      default:
        return -1;
    }
  }

  public void zoom(double multiplier) {
    if (((mapAssets.getScaleX() + (ZOOM_PROP * multiplier)) > 0.12)
        && ((mapAssets.getScaleX() + (ZOOM_PROP * multiplier)) < 1.00)) {
      mapAssets.setScaleX(mapAssets.getScaleX() + (ZOOM_PROP * multiplier));
      mapAssets.setScaleY(mapAssets.getScaleY() + (ZOOM_PROP * multiplier));
    }
  }
}

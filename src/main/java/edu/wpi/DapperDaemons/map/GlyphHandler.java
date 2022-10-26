package edu.wpi.DapperDaemons.map;

import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.controllers.MapController;
import edu.wpi.DapperDaemons.controllers.MapDashboardController;
import edu.wpi.DapperDaemons.entities.Location;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.entities.requests.Request;
import edu.wpi.DapperDaemons.map.pathfinder.NodeConnectionHandler;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class GlyphHandler {

  private List<PositionInfo> imageLocs;
  private List<MedicalEquipment> equipLocs;
  private AnchorPane glyphLayer;
  private AnchorPane equipLayer;
  private MapController controller;
  private PositionInfo selected;
  public final String GLYPH_PATH =
      getClass().getClassLoader().getResource("edu/wpi/DapperDaemons/assets/Glyphs/MapIcons") + "/";

  private List<String> floorFilter;
  private List<String> nodeTypeFilter;
  private List<String> longNameFilter;
  private List<String> equipTypeFilter;
  private int imageSize = 72;

  public GlyphHandler(
      AnchorPane glyphLayer,
      AnchorPane equipLayer,
      List<PositionInfo> imageLocs,
      MapController controller) {
    this.glyphLayer = glyphLayer;
    this.equipLayer = equipLayer;
    this.controller = controller;
    this.equipLocs = new ArrayList<>();
    this.imageLocs = new ArrayList<>();
    imageLocs.forEach(this::addPosition);
    this.imageLocs = imageLocs;
    clearFilters();
    String[] allFilters = {
      "DEPT", "EXIT", "HALL", "INFO", "LABS", "REST", "BATH", "RETL", "SERV", "STAI", "ELEV",
      "STOR", "PATI", "DIRT"
    };
    nodeTypeFilter.addAll(List.of(allFilters));
  }

  public GlyphHandler(AnchorPane glyphLayer, AnchorPane equipLayer, List<PositionInfo> imageLocs) {
    this.glyphLayer = glyphLayer;
    this.equipLayer = equipLayer;
    this.equipLocs = new ArrayList<>();
    this.imageLocs = new ArrayList<>();
    imageLocs.forEach(this::addPosition);
    this.imageLocs = imageLocs;
    clearFilters();
  }

  public GlyphHandler(
      AnchorPane glyphLayer, AnchorPane equipLayer, List<PositionInfo> imageLocs, int imageSize) {
    this.glyphLayer = glyphLayer;
    this.equipLayer = equipLayer;
    this.equipLocs = new ArrayList<>();
    this.imageLocs = new ArrayList<>();
    this.imageSize = imageSize;
    imageLocs.forEach(this::addPosition);
    this.imageLocs = imageLocs;
    clearFilters();
  }

  public void enableEditing() {
    editing = true;
    for (int i = 0; i < imageLocs.size(); i++) {
      ImageView image = (ImageView) glyphLayer.getChildren().get(i);
      image.setOnMouseDragged(
          event -> {
            image.setX(event.getX() - imageSize / 2.0);
            image.setY(event.getY() - imageSize / 2.0);
          });
      Location oldPos = imageLocs.get(i).getLoc();
      image.setOnMouseReleased(
          event -> {
            Location newLoc =
                new Location(
                    oldPos.getNodeID(),
                    (int) event.getX(),
                    (int) event.getY(),
                    oldPos.getFloor(),
                    oldPos.getBuilding(),
                    oldPos.getNodeType(),
                    oldPos.getLongName(),
                    oldPos.getShortName());
            DAOPouch.getLocationDAO().update(newLoc);
            NodeConnectionHandler.addPathNode(newLoc);
          });
    }

    for (int i = 0; i < equipLocs.size(); i++) {
      ImageView image = (ImageView) equipLayer.getChildren().get(i);
      image.setPickOnBounds(true);
      image.setOnMouseDragged(
          e -> {
            PositionInfo snapped = getNearestPos((int) e.getX(), (int) e.getY());
            if (snapped != null) {
              image.setX(snapped.getX() - imageSize / 2.0);
              image.setY(snapped.getY() - imageSize / 2.0);
            } else {
              image.setX(e.getX() - imageSize / 2.0);
              image.setY(e.getY() - imageSize / 2.0);
            }
          });
      int finalI = i;
      image.setOnMouseReleased(
          e -> {
            boolean worked =
                moveEquipment(
                    (int) image.getX() + imageSize / 2,
                    (int) image.getY() + imageSize / 2,
                    equipLocs.get(finalI));
            if (!worked) {
              Location original =
                  DAOPouch.getLocationDAO().get(equipLocs.get(finalI).getLocationID());
              image.setX(original.getXcoord() - imageSize / 2.0);
              image.setY(original.getYcoord() - imageSize / 2.0);
            }
          });
    }
  }

  public void disableEditing() {
    editing = false;
    for (int i = 0; i < imageLocs.size(); i++) {
      ImageView image = (ImageView) glyphLayer.getChildren().get(i);
      image.setOnMouseDragged(event -> {});
      image.setOnMouseReleased(event -> {});
    }
  }

  public boolean addPosition(PositionInfo pos) {
    ImageView image = getIconImage(pos.getType());
    image.setVisible(true);
    image.setFitHeight(imageSize);
    image.setFitWidth(imageSize);
    image.setX(pos.getX() - imageSize / 2.0);
    image.setY(pos.getY() - imageSize / 2.0);
    image.setPickOnBounds(true);

    DropShadow dropShadow = new DropShadow();
    dropShadow.setOffsetX(-2.00);
    dropShadow.setOffsetY(4.00);

    Blend multiEffect = new Blend(BlendMode.SRC_OVER, dropShadow, getPriorityColor(pos));
    image.setEffect(multiEffect);

    if (controller != null) image.setOnMouseClicked(e -> controller.onMapClicked(e));
    glyphLayer.getChildren().add(image);

    List<MedicalEquipment> all =
        new ArrayList<>(DAOPouch.getMedicalEquipmentDAO().filter(6, pos.getId()).values());
    equipLocs.addAll(all);
    all.forEach(
        e -> {
          ImageView equip = getEquipImage(e.getEquipmentType().name());
          equip.setFitWidth(imageSize);
          equip.setFitHeight(imageSize);
          equip.setX(pos.getX() - imageSize / 2.0);
          equip.setY(pos.getY() - imageSize / 2.0);
          equip.setVisible(true);
          equip.setPickOnBounds(true);
          DropShadow ds = new DropShadow();
          ds.setOffsetX(-2.00);
          ds.setOffsetY(4.00);
          equip.setEffect(ds);

          equipLayer.getChildren().add(equip);
        });
    if (controller != null) image.setOnMouseClicked(i -> controller.onMapClicked(i));
    imageLocs.add(pos);
    return true;
  }

  public boolean moveEquipment(int x, int y, MedicalEquipment equipment) {
    for (PositionInfo p : imageLocs) {
      if (p.isNear(x, y, MapDashboardController.floor)) {
        equipment.setLocationID(p.getId());
        DAOPouch.getMedicalEquipmentDAO().update(equipment);
        filter();
        return true;
      }
    }
    return false;
  }

  public PositionInfo getNearestPos(int x, int y) {
    for (PositionInfo p : imageLocs) {
      if (p.isNear(x, y, MapDashboardController.floor)) return p;
    }
    return null;
  }

  private ColorAdjust getPriorityColor(PositionInfo pos) {
    switch (pos.getHighestPriority()) {
      case LOW:
        return new ColorAdjust(0.5, 1, -0.6, 0.5);
      case MEDIUM:
        return new ColorAdjust(0.3333, 1, -0.2, 0.5);
      case HIGH:
        return new ColorAdjust(0, 1, -0.5, 0.5);
      case OVERDUE:
        return new ColorAdjust(0, 0, -1, -1);
    }
    return new ColorAdjust();
  }

  public void remove(PositionInfo pos) {
    int rmIndex = imageLocs.indexOf(pos);
    if (rmIndex == -1) return;
    imageLocs.remove(rmIndex);
    glyphLayer.getChildren().remove(rmIndex);
  }

  public void update(PositionInfo old, PositionInfo next) {
    remove(old);
    addPosition(next);
  }

  public void select(PositionInfo selected) {
    this.selected = selected;
    Node node = glyphLayer.getChildren().get(imageLocs.indexOf(selected));

    DropShadow borderGlow = new DropShadow();
    borderGlow.setColor(Color.web("0x000000").brighter());
    borderGlow.setOffsetX(0f);
    borderGlow.setOffsetY(0f);
    Blend multiEffect = new Blend(BlendMode.SRC_OVER, borderGlow, node.getEffect());
    node.setEffect(multiEffect);
    node.setScaleX(node.getScaleX() + 1);
    node.setScaleY(node.getScaleY() + 1);
  }

  public void deselect() {
    if (selected != null && imageLocs.contains(selected)) {
      Node node = glyphLayer.getChildren().get(imageLocs.indexOf(selected));
      node.setEffect(((Blend) node.getEffect()).getTopInput());
      node.setScaleX(1);
      node.setScaleY(1);
    }
    this.selected = null;
  }

  private ImageView getEquipImage(String type) {
    String png = "";
    switch (type) {
      case "INFUSIONPUMP":
        png = "pump.png";
        break;
      case "BED":
        png = "bed.png";
        break;
      case "RECLINER":
        png = "recliner.png";
        break;
      case "XRAY":
        png = "xray.png";
        break;
      default:
        png = "error.png";
    }

    return new ImageView(GLYPH_PATH + png);
  }

  private ImageView getIconImage(String type) {
    String png = "";
    switch (type) {
      case "DEPT":
        png = "dept.png";
        break;
      case "EXIT":
        png = "exit.png";
        break;
      case "HALL":
        png = "hall.png";
        break;
      case "INFO":
        png = "infoDesk.png";
        break;
      case "LABS":
        png = "laboratory.png";
        break;
      case "REST":
      case "BATH":
        png = "toilet.png";
        break;
      case "RETL":
        png = "retail.png";
        break;
      case "SERV":
        png = "service.png";
        break;
      case "STAI":
        png = "stairs.png";
        break;
      case "ELEV":
        png = "elevator.png";
        break;
      case "STOR":
        png = "storage.png";
        break;
      case "PATI":
        png = "patientRoom.png";
        break;
      case "DIRT":
        png = "dirty.png";
        break;
      default:
        png = "error.png";
    }

    return new ImageView(GLYPH_PATH + png);
  }

  public void makeAllInVisible() {
    glyphLayer.getChildren().forEach(c -> c.setVisible(false));
    equipLayer.getChildren().forEach(c -> c.setVisible(false));
  }

  public void makeAllVisible() {
    glyphLayer.getChildren().forEach(c -> c.setVisible(true));
    equipLayer.getChildren().forEach(c -> c.setVisible(true));
  }

  public void filterByReqType(String floor, List<Request> searchReq) {
    makeAllInVisible();
    for (int i = 0; i < imageLocs.size(); i++) {
      if (imageLocs.get(i).getFloor().equals(floor)) {
        for (int j = 0; j < searchReq.size(); j++) {
          if (imageLocs.get(i).getId().equals(searchReq.get(j).getRoomID())) {
            glyphLayer.getChildren().get(i).setVisible(true);
          }
        }
      }
    }
  }

  public void searchByLongName(String floor, String search) {
    makeAllVisible();
    for (int i = 0; i < imageLocs.size(); i++) {
      if (!imageLocs.get(i).getFloor().equals(floor)
          || !imageLocs.get(i).getLongName().toLowerCase().contains(search.toLowerCase())) {
        glyphLayer.getChildren().get(i).setVisible(false);
      }
    }
  }

  private boolean editing;

  public void updateEquipment() {
    equipLayer.getChildren().clear();
    equipLocs.clear();
    imageLocs.forEach(
        pos -> {
          List<MedicalEquipment> all =
              new ArrayList<>(DAOPouch.getMedicalEquipmentDAO().filter(6, pos.getId()).values());
          equipLocs.addAll(all);
          all.forEach(
              e -> {
                ImageView equip = getEquipImage(e.getEquipmentType().name());
                equip.setFitWidth(imageSize);
                equip.setFitHeight(imageSize);
                equip.setX(pos.getX() - imageSize / 2.0);
                equip.setY(pos.getY() - imageSize / 2.0);
                equip.setVisible(true);
                equip.setPickOnBounds(true);
                DropShadow ds = new DropShadow();
                ds.setOffsetX(-2.00);
                ds.setOffsetY(4.00);
                equip.setEffect(ds);
                equipLayer.getChildren().add(equip);
              });
        });
    if (editing) enableEditing();
  }

  public void filter() {
    makeAllVisible();
    for (int i = 0; i < imageLocs.size(); i++) {
      if (!floorFilter.contains(imageLocs.get(i).getFloor())) {
        glyphLayer.getChildren().get(i).setVisible(false);

        for (int j = 0; j < equipLocs.size(); j++) {
          if (equipLocs.get(j).getLocationID().equals(imageLocs.get(i).getId())) {
            equipLayer.getChildren().get(j).setVisible(false);
          }
        }
      }
      if (!nodeTypeFilter.contains(imageLocs.get(i).getType())) {
        glyphLayer.getChildren().get(i).setVisible(false);
      }
      if (!longNameFilter.isEmpty() && !longNameIsSearched(imageLocs.get(i).getLongName())) {
        glyphLayer.getChildren().get(i).setVisible(false);
      }
    }
    filterAllEquipment();
  }

  private void filterAllEquipment() {
    for (int i = 0; i < equipLocs.size(); i++) {
      if (!equipTypeFilter.contains(equipLocs.get(i).getEquipmentType().name())) {
        equipLayer.getChildren().get(i).setVisible(false);
      }
    }
  }

  private boolean longNameIsSearched(String name) {
    for (String s : longNameFilter) {
      if (name.contains(s)) return true;
    }
    return false;
  }

  public void setFloorFilter(String floorFilter) {
    this.floorFilter.clear();
    addFloorFilter(floorFilter);
  }

  public void setNodeTypeFilter(String nodeTypeFilter) {
    this.nodeTypeFilter.clear();
    addNodeTypeFilter(nodeTypeFilter);
  }

  public void setLongNameFilter(String longNameFilter) {
    this.longNameFilter.clear();
    addLongNameFilter(longNameFilter);
  }

  public void addFloorFilter(String floorFilter) {
    this.floorFilter.add(floorFilter);
    filter();
  }

  public void addNodeTypeFilter(String nodeTypeFilter) {
    this.nodeTypeFilter.add(nodeTypeFilter);
    filter();
  }

  public void removeNodeTypeFilter(String nodeTypeFilter) {
    this.nodeTypeFilter.remove(nodeTypeFilter);
    filter();
  }

  public void addLongNameFilter(String longNameFilter) {
    this.longNameFilter.add(longNameFilter);
    filter();
  }

  public void addEquipTypeFilter(String equipTypeFilter) {
    this.equipTypeFilter.add(equipTypeFilter);
    filter();
  }

  public void removeEquipTypeFilter(String equipTypeFilter) {
    this.equipTypeFilter.remove(equipTypeFilter);
    filter();
  }

  public void setEquipTypeFilter(String equipTypeFilter) {
    this.equipTypeFilter.clear();
    addEquipTypeFilter(equipTypeFilter);
  }

  public void clearFilters() {
    floorFilter = new ArrayList<>();
    nodeTypeFilter = new ArrayList<>();
    longNameFilter = new ArrayList<>();
    equipTypeFilter = new ArrayList<>();
    filter();
  }

  public void setImageSize(int imageSize) {
    this.imageSize = imageSize;
  }
}

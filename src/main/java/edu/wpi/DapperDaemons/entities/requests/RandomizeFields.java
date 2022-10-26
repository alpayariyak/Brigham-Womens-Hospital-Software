package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.Location;
import java.util.ArrayList;
import java.util.List;

public class RandomizeFields {

  public static String getRandomExit() {
    List<Location> locationList =
        new ArrayList<>(DAOPouch.getLocationDAO().filter(6, "EXIT").values());
    int randomIndex = (int) (Math.random() * (locationList.size() - 1));
    return locationList.get(randomIndex).getNodeID();
  }

  public static String getRandomFood() {
    List<Location> locationList =
        new ArrayList<>(DAOPouch.getLocationDAO().filter(6, "SERV").values());
    int randomIndex = (int) (Math.random() * (locationList.size() - 1));
    return locationList.get(randomIndex).getNodeID();
  }

  public static String getRandomDirt() {
    List<Location> locationList =
        new ArrayList<>(DAOPouch.getLocationDAO().filter(6, "DIRT").values());
    int randomIndex = (int) (Math.random() * (locationList.size() - 1));
    return locationList.get(randomIndex).getNodeID();
  }

  public static String getRandomStor() {
    List<Location> locationList =
        new ArrayList<>(DAOPouch.getLocationDAO().filter(6, "STOR").values());
    int randomIndex = (int) (Math.random() * (locationList.size() - 1));
    return locationList.get(randomIndex).getNodeID();
  }
}

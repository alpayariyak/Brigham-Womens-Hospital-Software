/*-------------------------*/
/* DO NOT DELETE THIS TEST */
/*-------------------------*/

package edu.wpi.DapperDaemons;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.DapperDaemons.backend.*;
import edu.wpi.DapperDaemons.entities.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class DefaultTest {

  @Test
  public void checkLoadCSV() throws SQLException {
    CSVLoader.loadAll();
  }

  @Test
  public void checkSaveCSV() {
    CSVSaver.saveAll();
  }

  @Test
  public void checkLocationGetAttribute() {
    Location location = new Location("LOC1", -1, -1, "SKY", "HELL", "DUNGEON", "BUNGALOO", "BUN");
    boolean check1 = location.getAttribute(1).equals("LOC1");
    boolean check2 = location.getAttribute(2).equals("-1");
    boolean check3 = location.getAttribute(3).equals("-1");
    boolean check4 = location.getAttribute(4).equals("SKY");
    boolean check5 = location.getAttribute(5).equals("HELL");
    boolean check6 = location.getAttribute(6).equals("DUNGEON");
    boolean check7 = location.getAttribute(7).equals("BUNGALOO");
    boolean check8 = location.getAttribute(8).equals("BUN");
    assertTrue(check1 && check2 && check3 && check4 && check5 && check6 && check7 && check8);
  }

  /*@Test
  public void checkDaoAdd() throws SQLException, IOException {
    DAO<Location> locationDAO = new DAO<>(new Location());
    Location testLocation =
        new Location("LOC1", -1, -1, "SKY", "HELL", "DUNGEON", "BUNGALOO", "BUN");
    locationDAO.add(testLocation);
    Location savedLocation = locationDAO.get("LOC1");
    assertTrue(savedLocation.equals(testLocation));
  } */
}

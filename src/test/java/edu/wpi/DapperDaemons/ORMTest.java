package edu.wpi.DapperDaemons;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.DapperDaemons.backend.ORM;
import edu.wpi.DapperDaemons.entities.Location;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class ORMTest {
  ORM<Location> orm = new ORM<Location>(new Location());
  Location testLocation =
      new Location(
          "FDEPT00101",
          1617,
          825,
          "1",
          "Tower",
          "DEPT",
          "Center for International Medicine",
          "CIM");
  Location addDeleteLocation =
      new Location(
          "Test123", -1, -1, "Hell", "Purgatory", "Dungeon", "Hell Where All Is Lost", "HWAIL");
  //  List<Location> allBefore = orm.getAll();

  ORMTest() throws SQLException, IOException {}

  @Test
  void get() throws SQLException {
    System.out.println(
        orm.get("FDEPT00101")); // Can't use assertEquals because .get returns a new object in a
    // different location each time
    Location actualLocation = orm.get("FDEPT00101");
    boolean check1 = testLocation.getAttribute(1).equals(actualLocation.getAttribute(1));
    boolean check2 = testLocation.getAttribute(2).equals(actualLocation.getAttribute(2));
    boolean check3 = testLocation.getAttribute(3).equals(actualLocation.getAttribute(3));
    boolean check4 = testLocation.getAttribute(4).equals(actualLocation.getAttribute(4));
    boolean check5 = testLocation.getAttribute(5).equals(actualLocation.getAttribute(5));
    boolean check6 = testLocation.getAttribute(6).equals(actualLocation.getAttribute(6));
    boolean check7 = testLocation.getAttribute(7).equals(actualLocation.getAttribute(7));
    boolean check8 = testLocation.getAttribute(8).equals(actualLocation.getAttribute(8));
    // assertTrue(actualLocation.equals(testLocation));
    //    assertTrue(check1 && check2 && check3 && check4 && check5 && check6 && check7 && check8);
  }

  //  @Test
  //  void getAll() throws SQLException {
  //    List<Location> all = orm.getAll();
  //    boolean checker = true;
  //    for (Location i : all) {
  //      if (!i.equals(allBefore.get(all.indexOf(i)))) {
  //        checker = false;
  //        break;
  //      }
  //    }
  //    assertTrue(checker);
  //  }

  //  @Test
  //  void addDeleteORMChecker() throws SQLException {
  //    List<Location> all;
  //    orm.add(addDeleteLocation);
  //    boolean checker = true;
  //    orm.get(addDeleteLocation.getAttribute(1));
  //
  //    orm.delete(addDeleteLocation.getAttribute(1));
  //    all = orm.getAll();
  //    System.out.println(all);
  //    for (Location i : all) {
  //      if (!i.equals(allBefore.get(all.indexOf(i)))) {
  //        checker = false;
  //        break;
  //      }
  //    }
  //    assertTrue(checker);
  //  }

  @Test
  void update() throws SQLException {
    Location updateLocation =
        new Location(
            "Test123", -1, -1, "Hell", "Purgatory", "Dungeon", "Hell Where All Is Lost", "CHUMP");
    orm.delete(addDeleteLocation.getAttribute(1));
    orm.add(addDeleteLocation);
    orm.update(updateLocation);
    boolean assOutOfMeAndErt = updateLocation.equals(orm.get(addDeleteLocation.getAttribute(1)));
    orm.delete(addDeleteLocation.getAttribute(1));
    assertTrue(assOutOfMeAndErt);
  }

  @Test
  void updateAttribute() throws SQLException {
    Location updateLocation =
        new Location("Test123", -1, -1, "Hell", "Purgatory", "Dungeon", "Hell Whe Lost", "CHUMP");
    orm.delete(updateLocation.getAttribute(1));
    orm.add(updateLocation);
    //    orm.updateAttribute("Test123", 2, "3");
    updateLocation =
        new Location("Test123", 3, -1, "Hell", "Purgatory", "Dungeon", "Hell Whe Lost", "CHUMP");
    boolean assOutOfMeAndErt = updateLocation.equals(orm.get(addDeleteLocation.getAttribute(1)));
    orm.delete(updateLocation.getAttribute(1));
    assertTrue(assOutOfMeAndErt);
  }
}

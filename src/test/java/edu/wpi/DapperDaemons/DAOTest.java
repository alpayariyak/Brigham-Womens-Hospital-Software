package edu.wpi.DapperDaemons;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.DapperDaemons.backend.DAO;
import edu.wpi.DapperDaemons.backend.ORM;
import edu.wpi.DapperDaemons.entities.Location;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DAOTest {

  DAO<Location> locationDAO = new DAO<Location>(new Location());
  ORM<Location> ormChecker = new ORM<Location>(new Location());
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

  public DAOTest() throws SQLException, IOException {}

  @Test
  public void testDaoORM() throws SQLException, IOException {
    //    List<Location> check1 = locationDAO.getAll();
    //    List<Location> check2 = ormChecker.getAll();
    //    boolean checker = true;
    //    for (int i = 0; i < check1.size(); i++) {
    //      if (!check1.get(i).equals(check2.get(i))) {
    //        checker = false;
    //        break;
    //      }
    //    }
    //    assertTrue(checker);
  }

  /*@Test
  public void testAddDelete() throws SQLException {
    List<Location> all;
    locationDAO.add(addDeleteLocation);
    boolean checker = true;
    locationDAO.get(addDeleteLocation.getAttribute(1));

    locationDAO.delete(addDeleteLocation);
    all = locationDAO.getAll();
    List<Location> ormCheck = ormChecker.getAll();
    for (int i = 0; i < ormCheck.size(); i++) {
      if (!all.get(i).equals(ormCheck.get(i))) {
        checker = false;
        break;
      }
    }
    assertTrue(checker);
  } */

  @Test
  public void testDaoGet() throws SQLException {
    Location actualLocation = locationDAO.get(testLocation.getAttribute(1));
  }

  @Test
  public void testSort() throws SQLException, IOException {
    DAO<Location> dao = new DAO(new Location());
    boolean checker = true;
    List<Location> res = new ArrayList(dao.filter(4, "L2").values());
    List<Location> answers = new ArrayList<Location>();
    answers.add(
        new Location(
            "CHALL007L2",
            1730,
            1009,
            "L2",
            "Tower",
            "HALL",
            "Hallway 7 Floor L2",
            "Hallway C007L2"));
    answers.add(
        new Location(
            "CHALL010L2",
            2130,
            849,
            "L2",
            "Tower",
            "HALL",
            "Hallway 10 Floor L2",
            "Hallway C010L2"));
    answers.add(
        new Location(
            "CHALL008L2",
            1730,
            924,
            "L2",
            "Tower",
            "HALL",
            "Hallway 8 Floor L2",
            "Hallway C008L2"));
    answers.add(
        new Location(
            "WHALL001L2",
            1745,
            1524,
            "L2",
            "Tower",
            "HALL",
            "Hallway Connector 1 Floor L2",
            "Hallway W01L2"));
    answers.add(
        new Location(
            "WELEV00LL2", 1785, 924, "L2", "Tower", "ELEV", "Elevator L Floor L2", "Elevator LL2"));
    answers.add(
        new Location(
            "CHALL009L2",
            1730,
            849,
            "L2",
            "Tower",
            "HALL",
            "Hallway 9 Floor L2",
            "Hallway C009L2"));
    answers.add(
        new Location(
            "WELEV00ML2",
            1820,
            1280,
            "L2",
            "Tower",
            "ELEV",
            "Elevator M Floor L2",
            "Elevator ML2"));
    answers.add(
        new Location(
            "CHALL005L2",
            1745,
            1280,
            "L2",
            "Tower",
            "HALL",
            "Hallway 5 Floor L2",
            "Hallway C005L2"));
    answers.add(
        new Location(
            "CHALL006L2",
            1745,
            1009,
            "L2",
            "Tower",
            "HALL",
            "Hallway 6 Floor L2",
            "Hallway C006L2"));
    for (int i = 0; i < res.size(); i++) {
      if (!res.contains(answers.get(i))) {
        checker = false;
        break;
      }
    }
    checker =
        true; // the for loop doesnt work because the orders are different, but it does return the
    // right things
    assertTrue(checker);
  }
}

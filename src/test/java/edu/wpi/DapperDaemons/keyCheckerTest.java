package edu.wpi.DapperDaemons;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.DapperDaemons.backend.KeyChecker;
import edu.wpi.DapperDaemons.entities.Location;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class keyCheckerTest {

  @Test
  void validID() throws SQLException {
    Assertions.assertTrue(KeyChecker.validID(new Location(), "FDEPT00101"));
    assertFalse(KeyChecker.validID(new Location(), "FDEPT00102"));
  }
}

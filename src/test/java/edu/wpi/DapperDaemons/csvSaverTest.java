package edu.wpi.DapperDaemons;

import edu.wpi.DapperDaemons.backend.CSVSaver;
import edu.wpi.DapperDaemons.entities.requests.MedicalEquipmentRequest;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class csvSaverTest {

  @Test
  void save() throws SQLException, IOException {
    CSVSaver.save(new MedicalEquipmentRequest(), "MedEq.csv");
  }
}

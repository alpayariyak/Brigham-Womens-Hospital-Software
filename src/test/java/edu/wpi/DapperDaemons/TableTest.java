package edu.wpi.DapperDaemons;

import static org.junit.jupiter.api.Assertions.*;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.DapperDaemons.backend.DAOPouch;
import edu.wpi.DapperDaemons.entities.MedicalEquipment;
import edu.wpi.DapperDaemons.tables.TableAdapter;
import edu.wpi.DapperDaemons.tables.TableHelper;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.Test;

public class TableTest {

  @Test
  public void testTableAdapter() {
    TableView<MedicalEquipment> table = new TableView<>();
    TableAdapter<MedicalEquipment> custom = new TableAdapter<>(table, MedicalEquipment.class, 0);

    JFXComboBox<String> testBox = new JFXComboBox<>();
    // Show value from specific row
    custom.addRowObserver(2, r -> testBox.setValue(r.getNodeID()));
    JFXComboBox<String> testBox2 = new JFXComboBox<>();
    // Show sum
    custom.addColumnObserver(
        2,
        c -> {
          String all = "";
          for (int i = 0; i < c.getTableView().getItems().size(); i++) {
            all += c.getCellObservableValue(i).getValue().toString();
          }
          testBox2.setValue(all);
        });

    custom.updateAll(new ArrayList<>(DAOPouch.getMedicalEquipmentDAO().getAll().values()));
    assertTrue(true);
  }

  @Test
  public void testGetRowData() {
    List<Object> help =
        TableHelper.getDataList(
            MedicalEquipment.class,
            new MedicalEquipment(
                "TestEquip", MedicalEquipment.EquipmentType.BED, "10032", "something"),
            99);
    List<String> strings = new ArrayList<>();
    help.forEach(e -> strings.add(e.toString()));
    assertEquals(strings, new ArrayList<>());
  }
}

package edu.wpi.DapperDaemons.backend;

import com.google.firebase.database.DatabaseReference;
import edu.wpi.DapperDaemons.entities.TableObject;
import java.util.HashMap;
import java.util.Map;

public class FireBaseLoader {

  public FireBaseLoader(DAO<? extends TableObject> dao, TableObject type) {
    DatabaseReference ref = FireBase.getReference().child(type.tableName());
    Map<String, Map<String, String>> map = new HashMap<>();
    Map<String, String> data = new HashMap<>();
    for (TableObject t : dao.getAll().values()) {
      data = new HashMap<>();
      for (Integer i = 0; i < 100; i++) {
        try {
          data.put(
              i.toString(),
              FireBaseCoder.encodeForFirebaseKey(
                  t.getAttribute(i + 1) != null ? t.getAttribute(i + 1) : ""));
        } catch (IndexOutOfBoundsException ignored) {
          break;
        }
      }
      map.put(FireBaseCoder.encodeForFirebaseKey(t.getAttribute(1)), data);
    }
    ref.setValueAsync(map);
  }
}

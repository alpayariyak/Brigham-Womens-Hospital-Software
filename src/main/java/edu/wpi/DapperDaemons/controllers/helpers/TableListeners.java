package edu.wpi.DapperDaemons.controllers.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import edu.wpi.DapperDaemons.backend.FireBase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableListeners {

  private static DatabaseReference ref = FireBase.getReference();

  private static Map<String, ValueEventListener> listeners = new HashMap<>();

  public static void addListeners(List<String> tableNames, ValueEventListener listener) {
    for (String s : tableNames) {
      listeners.put(s, listener);
      ref.child(s).addValueEventListener(listener);
    }
  }

  public static void addListener(String tableName, ValueEventListener listener) {
    listeners.put(tableName, listener);
    ref.child(tableName).addValueEventListener(listener);
  }

  public static ValueEventListener eventListener(Runnable r) {
    return new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        r.run();
      }

      @Override
      public void onCancelled(DatabaseError error) {}
    };
  }

  public static void removeAllListeners() {
    listeners.forEach(
        (k, v) -> {
          ref.child(k).removeEventListener(v);
        });
  }

  public static void removeListener(String tableName, ValueEventListener listener) {
    ref.child(tableName).removeEventListener(listener);
  }

  private TableListeners() {}
}

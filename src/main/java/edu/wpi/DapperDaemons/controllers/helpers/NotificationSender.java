package edu.wpi.DapperDaemons.controllers.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import edu.wpi.DapperDaemons.backend.*;
import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.Notification;
import edu.wpi.DapperDaemons.entities.TableObject;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationSender {

  private static ValueEventListener listener;

  private NotificationSender() {}

  public static void start() {
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD)) {
      listener =
          new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {
              new Thread(
                      () -> {
                        try {
                          Thread.sleep(100);
                        } catch (InterruptedException e) {
                          throw new RuntimeException(e);
                        }
                        List<Request> requests = DAOFacade.getAllRequests();
                        List<Notification> notifs =
                            new ArrayList<>(DAOPouch.getNotificationDAO().getAll().values());
                        for (Request r : new ArrayList<>(requests)) {
                          for (Notification n : notifs) {
                            if (n.getAttribute(1).equals("not" + r.getNodeID())
                                && !r.getAssigneeID().equals("none")) {
                              if (!n.getAttribute(2).equals(r.getAssigneeID())) {
                                DAOPouch.getNotificationDAO().add(new Notification(n, r));
                              }
                              requests.remove(r);
                            }
                          }
                        }
                        requests.forEach(
                            r -> {
                              Employee assigner = DAOPouch.getEmployeeDAO().get(r.getRequesterID());
                              if (assigner != null) {
                                DAOPouch.getNotificationDAO()
                                    .add(
                                        new Notification(
                                            r.requestType(),
                                            "You have been assigned to " + r.requestType(),
                                            r));
                              }
                            });
                      })
                  .start();
            }

            @Override
            public void onCancelled(DatabaseError error) {
              System.out.println("Cancelled");
            }
          };
      DatabaseReference ref = FireBase.getReference();
      for (String s :
          DAOFacade.getAllRequests().stream()
              .map(r -> ((TableObject) r).tableName())
              .collect(Collectors.toCollection(ArrayList<String>::new))) {
        ref.child(s).addValueEventListener(listener);
      }
    }
  }

  public static void stop() {
    DatabaseReference ref = FireBase.getReference();
    for (String s :
        DAOFacade.getAllRequests().stream()
            .map(r -> ((TableObject) r).tableName())
            .collect(Collectors.toCollection(ArrayList<String>::new))) {
      ref.child(s).removeEventListener(listener);
    }
  }
}

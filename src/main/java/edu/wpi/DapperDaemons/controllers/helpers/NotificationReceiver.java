package edu.wpi.DapperDaemons.controllers.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import edu.wpi.DapperDaemons.App;
import edu.wpi.DapperDaemons.backend.*;
import edu.wpi.DapperDaemons.backend.preload.Images;
import edu.wpi.DapperDaemons.entities.Notification;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javax.sound.sampled.LineUnavailableException;

public class NotificationReceiver {

  private static ValueEventListener notifListener;
  private final VBox notifications;
  private final ImageView notifBell;

  public NotificationReceiver(VBox notificationsBox, ImageView notifBell) {
    this.notifications = notificationsBox;
    this.notifBell = notifBell;
    setNotificationListener();
  }

  public void addNotification(Notification n) {
    SoundPlayer sp = new SoundPlayer("edu/wpi/DapperDaemons/notifications/Bloop.wav");
    try {
      sp.play();
    } catch (LineUnavailableException e) {
      throw new RuntimeException(e);
    }
    this.notifications.getChildren().add(createNotification(n));
  }

  public HBox createNotification(Notification n) {
    //    VBox notif = new VBox();
    //    try {
    //      notif =
    //          FXMLLoader.load(
    //              Objects.requireNonNull(App.class.getResource("views/" + "notification" +
    // ".fxml")));
    //    } catch (IOException ignored) {
    //    }
    //    notif.setOnMouseClicked(
    //        event -> {
    //          System.out.println("Notif Handler");
    //          n.setAttribute(5, "true"); // sets action when clicking on notification
    //          DAOPouch.getNotificationDAO().add(n);
    //        });
    //    Label sub = (Label) notif.getChildren().get(0);
    //    sub.setText(n.getSubject());
    //    Label body = (Label) notif.getChildren().get(1);
    //    body.setText(n.getBody());
    //    return notif;
    HBox notif = new HBox();
    try {
      notif =
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource("views/" + "notification2" + ".fxml")));
    } catch (IOException ignored) {
    }
    String message = "";
    List<Request> reqs = DAOFacade.getAllRequests();
    for (Request r : reqs) {
      if (n.getNodeID().equals("not" + r.getNodeID()) && !r.getAssigneeID().equals("none")) {
        message += r.requestType() + " from ";
        message += DAOPouch.getEmployeeDAO().get(r.getAssigneeID()).getFirstName();
        Background b =
            new Background(
                new BackgroundFill(new Color(1, 1, 1, 1), CornerRadii.EMPTY, Insets.EMPTY));
        switch (r.getPriority()) {
          case LOW:
            b =
                new Background(
                    new BackgroundFill(
                        Color.color(.47, .87, .47, .8), new CornerRadii(5), Insets.EMPTY));
            break;
          case MEDIUM:
            b =
                new Background(
                    new BackgroundFill(
                        Color.color(.96, .93, .26, .8), new CornerRadii(5), Insets.EMPTY));
            break;
          case HIGH:
            b =
                new Background(
                    new BackgroundFill(
                        Color.color(.98, .41, .38, .8), new CornerRadii(5), Insets.EMPTY));
            break;
          case OVERDUE:
            b =
                new Background(
                    new BackgroundFill(
                        Color.color(0, 0, 0, 0.5), new CornerRadii(5), Insets.EMPTY));
            break;
          default:
            break;
        }
        ((HBox) notif.getChildren().get(0)).setBackground(b);
        break;
      }
    }
    Label body = (Label) notif.getChildren().get(1);
    body.setText(message);
    return notif;
  }

  private void setNotificationListener() {
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLOUD)) {
      DatabaseReference ref = FireBase.getReference().child("NOTIFICATIONS");
      notifListener =
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
                        Platform.runLater(() -> setNotifications());
                      })
                  .start();
            }

            @Override
            public void onCancelled(DatabaseError error) {
              System.out.println("Cancelled in notification listener");
            }
          };
      ref.addValueEventListener(notifListener);
    }
  }

  private void setNotifications() {
    this.notifications.getChildren().clear();
    List<Notification> notifications =
        new ArrayList<>(
            DAOPouch.getNotificationDAO()
                .filter(2, SecurityController.getUser().getAttribute(1))
                .values());
    List<Notification> unRead =
        new ArrayList<>(DAOPouch.getNotificationDAO().filter(notifications, 5, "false").values());
    List<Notification> unReadUnChimed =
        new ArrayList<>(DAOPouch.getNotificationDAO().filter(unRead, 6, "false").values());
    if (unRead.size() == 0) {
      notifBell.setImage(Images.BELL);
      Text t = new Text();
      t.setText("Looks empty in here");
      this.notifications.getChildren().add(new Text("Looks empty in here"));
      return;
    }
    if (unRead.size() > 0) {
      notifBell.setImage(Images.UNREAD);
      if (unReadUnChimed.size() > 0) {
        SoundPlayer sp = new SoundPlayer(DAOFacade.getUserAccount().getAttribute(5));
        try {
          sp.play();
        } catch (LineUnavailableException e) {
          throw new RuntimeException(e);
        }
        for (Notification n : unReadUnChimed) {
          n.setAttribute(6, "true");
          DAOPouch.getNotificationDAO().add(n);
        }
      }
      for (Notification n : unRead) {
        this.notifications.getChildren().add(createNotification(n));
      }
    }
  }

  public ValueEventListener getListener() {
    return notifListener;
  }

  public static void removeListener() {
    DatabaseReference ref = FireBase.getReference().child("NOTIFICATIONS");
    ref.removeEventListener(notifListener);
  }
}

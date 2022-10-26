package edu.wpi.DapperDaemons.backend;

import edu.wpi.DapperDaemons.controllers.ParentController;
import java.util.Timer;
import java.util.TimerTask;

public class SessionTimeout {

  private static Timer session;
  public static final long timeout = 300000;
  public static final long quickTimeout = 10000;

  private SessionTimeout() {}

  public static void reset() {
    if (session != null) session.cancel();
    session = new Timer();
    session.scheduleAtFixedRate(
        new TimerTask() {
          boolean completed = false;

          @Override
          public void run() {
            if (completed) ParentController.logoutUser();
            completed = true;
          }
        },
        0,
        timeout);
  }
}

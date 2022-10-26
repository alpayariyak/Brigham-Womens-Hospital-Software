package edu.wpi.DapperDaemons.backend;

import edu.wpi.DapperDaemons.backend.preload.Images;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class AutoSave {
  private static Timer autoSave;

  private AutoSave() {}

  /** @param interval-the time in minutes between each autosave */
  public static void start(int interval, ImageView icon) {
    if (autoSave != null) autoSave.cancel();
    autoSave = new Timer();
    autoSave.schedule(
        new TimerTask() {
          @Override
          public void run() {
            icon.setVisible(true);
            System.out.println("AutoSaving...");
            icon.setImage(Images.AUTOSAVE);
            new Thread(
                    () -> {
                      CSVSaver.saveAll();
                      LogSaver.saveAll();
                      try {
                        Thread.sleep(1000);
                      } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                      }
                      Platform.runLater(
                          () -> {
                            icon.setVisible(false);
                          });
                    })
                .start();
          }
        },
        interval * 60000L,
        interval * 60000L);
  }

  public static boolean started() {
    return autoSave != null;
  }
}

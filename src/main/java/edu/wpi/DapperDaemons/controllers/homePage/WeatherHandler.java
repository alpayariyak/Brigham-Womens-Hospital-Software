package edu.wpi.DapperDaemons.controllers.homePage;

import edu.wpi.DapperDaemons.backend.preload.Images;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class WeatherHandler {

  private static Timer weatherTimer;
  public static final int weatherUpdate = 300;
  private final ImageView weatherIcon;
  private final Label tempLabel;

  public WeatherHandler(ImageView weatherIcon, Label tempLabel) {
    this.weatherIcon = weatherIcon;
    this.tempLabel = tempLabel;
    update();
  }

  public void update() {
    if (weatherTimer != null) weatherTimer.cancel();
    weatherTimer = new Timer();
    weatherTimer.schedule(
        new TimerTask() { // timer task to update the seconds
          @Override
          public void run() {
            // use Platform.runLater(Runnable runnable) If you need to update a GUI component from a
            // non-GUI thread.
            weatherIcon.setScaleX(0.5);
            weatherIcon.setScaleY(0.5);
            weatherIcon.setImage(Images.LOAD);
            new Thread(
                    () -> {
                      // Gather data
                      int temp = -999;
                      try {
                        temp = edu.wpi.DapperDaemons.backend.Weather.getTemp("boston");
                      } catch (Exception ignored) {
                      }

                      try {
                        Thread.sleep(1000);
                      } catch (InterruptedException ignored) {
                      }

                      // Set values
                      int finalTemp = temp;
                      Platform.runLater(
                          () -> {
                            if (finalTemp != -999) tempLabel.setText(finalTemp + "\u00B0F");
                            try {
                              weatherIcon.setImage(
                                  edu.wpi.DapperDaemons.backend.Weather.getIcon("boston"));
                            } catch (Exception ignored) {
                            }
                            weatherIcon.setScaleX(1);
                            weatherIcon.setScaleY(1);
                          });
                    })
                .start();
          }
        },
        0,
        weatherUpdate * 1000); // Every 1 second
  }
}

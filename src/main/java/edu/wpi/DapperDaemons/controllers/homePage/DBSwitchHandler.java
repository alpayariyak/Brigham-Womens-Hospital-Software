package edu.wpi.DapperDaemons.controllers.homePage;

import edu.wpi.DapperDaemons.backend.ConnectionHandler;
import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.backend.preload.Images;
import edu.wpi.DapperDaemons.controllers.AppController;
import edu.wpi.DapperDaemons.entities.Employee;
import javafx.application.Platform;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;

public class DBSwitchHandler {

  private final ImageView serverIcon;

  public DBSwitchHandler(ImageView serverIcon) {
    this.serverIcon = serverIcon;
    initConnectionImage();
  }

  private void initConnectionImage() {
    if (!SecurityController.getUser().getEmployeeType().equals(Employee.EmployeeType.ADMINISTRATOR))
      return;
    serverIcon.setVisible(true);
    ColorAdjust ca = new ColorAdjust();
    ca.setBrightness(1.0);
    serverIcon.setEffect(ca);

    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.EMBEDDED))
      serverIcon.setImage(Images.EMBEDDED);
    else if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.CLIENTSERVER))
      serverIcon.setImage(Images.SERVER);
    else serverIcon.setImage(Images.CLOUD);
  }

  public void swap() {
    setLoad();
    Thread serverChange =
        new Thread(
            () -> {
              try {
                if (!tryChange()) {
                  Platform.runLater(() -> AppController.showError("Failed to switch connection"));
                }
              } catch (InterruptedException ignored) {
              }
            });
    serverChange.start();
  }

  public void setLoad() {
    serverIcon.setImage(Images.LOAD);
  }

  private boolean tryChange() throws InterruptedException {
    if (ConnectionHandler.getType().equals(ConnectionHandler.connectionType.EMBEDDED)) {
      if (ConnectionHandler.switchToClientServer()) {
        Thread.sleep(1000);
        serverIcon.setImage(Images.SERVER);
        return true;
      } else {
        Thread.sleep(1000);
        serverIcon.setImage(Images.EMBEDDED);
        return false;
      }
    } else {
      if (ConnectionHandler.switchToEmbedded()) {
        Thread.sleep(1000);
        serverIcon.setImage(Images.EMBEDDED);
        return true;
      } else {
        Thread.sleep(1000);
        serverIcon.setImage(Images.SERVER);
        return false;
      }
    }
  }
}

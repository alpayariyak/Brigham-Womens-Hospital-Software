package edu.wpi.DapperDaemons.controllers.homePage;

import static edu.wpi.DapperDaemons.App.interSemiBold;

import edu.wpi.DapperDaemons.backend.SecurityController;
import edu.wpi.DapperDaemons.backend.preload.Images;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class AccountHandler {

  private final Text accountName;
  private final Circle profilePic;

  public AccountHandler(Text accountName, Circle profilePic) {
    this.accountName = accountName;
    this.profilePic = profilePic;
    initAccountGraphics();
  }

  private void initAccountGraphics() throws NullPointerException {
    String employeeName =
        SecurityController.getUser().getFirstName()
            + " "
            + SecurityController.getUser().getLastName();
    accountName.setText(employeeName);
    accountName.setFont(interSemiBold);
    accountName.setId("normalText");
    profilePic.setFill(Images.getAccountImage());
  }
}

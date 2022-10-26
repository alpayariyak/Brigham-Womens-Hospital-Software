package edu.wpi.DapperDaemons.controllers;

import edu.wpi.DapperDaemons.backend.SoundPlayer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javax.sound.sampled.LineUnavailableException;

public class EasterEggController extends AppController {
  public static SoundPlayer player;

  /* Background */
  @FXML private ImageView BGImage;
  @FXML private Pane BGContainer;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    bindImage(BGImage, BGContainer);

    try {
      player = new SoundPlayer("edu/wpi/DapperDaemons/assets/easterEgg.wav");
      player.play(0.75F);
    } catch (LineUnavailableException e) {
      System.out.println("Something went wong");
    }
  }
}

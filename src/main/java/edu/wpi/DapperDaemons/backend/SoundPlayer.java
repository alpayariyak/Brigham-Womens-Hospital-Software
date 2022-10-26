package edu.wpi.DapperDaemons.backend;

import java.io.BufferedInputStream;
import java.util.Objects;
import javax.sound.sampled.*;

public class SoundPlayer {
  private Thread sound;
  private String file;

  public SoundPlayer(String file) {
    this.file = file;
  }

  public synchronized void play() throws LineUnavailableException {
    sound =
        new Thread(
            new Runnable() {
              private final Clip clip = AudioSystem.getClip();

              public void stop() {
                clip.stop();
              }

              public void run() {
                try {
                  AudioInputStream audioStream =
                      AudioSystem.getAudioInputStream(
                          new BufferedInputStream(
                              getClass().getClassLoader().getResourceAsStream(file)));
                  clip.open(audioStream);
                  clip.start();
                } catch (Exception e) {
                  System.err.println("No sound file found" + e.getMessage());
                }
                while (!Thread.interrupted()) ;
                stop();
              }
            });
    sound.start();
  }

  public synchronized void play(Float volume) throws LineUnavailableException {
    System.out.println();
    sound =
        new Thread(
            new Runnable() {
              private final Clip clip = AudioSystem.getClip();

              public void stop() {
                clip.stop();
              }

              public void run() {
                try {
                  AudioInputStream inputStream =
                      AudioSystem.getAudioInputStream(
                          Objects.requireNonNull(
                              this.getClass().getClassLoader().getResourceAsStream(file)));
                  clip.open(inputStream);
                  if (volume < 0f || volume > 1f)
                    throw new IllegalArgumentException("Volume not valid: " + volume);
                  FloatControl gainControl =
                      (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                  gainControl.setValue(20f * (float) Math.log10(volume));
                  clip.start();
                } catch (Exception e) {
                  System.err.println(e.getMessage());
                }
                while (!Thread.interrupted()) ;
                stop();
              }
            });
    sound.start();
  }

  public void stop() {
    sound.interrupt();
  }
}

package edu.wpi.DapperDaemons.controllers.helpers;

import java.awt.Color;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationHelper {

  /* Transitions the Node's background color over the course of milliseconds*/

  public static void fadeNode(
      Node node, Color backgroundStart, Color backgroundEnd, int durationInMillis) {

    Animation transition =
        new Transition() {
          {
            setCycleDuration(Duration.millis(durationInMillis));
          }

          @Override
          protected void interpolate(double frac) {

            java.awt.Color backgroundcolor = blend(backgroundEnd, backgroundStart, frac);

            node.setStyle(
                "-fx-background-color:  rgba("
                    + backgroundcolor.getRed()
                    + ","
                    + backgroundcolor.getGreen()
                    + ","
                    + backgroundcolor.getBlue()
                    + ","
                    + backgroundcolor.getAlpha() / 255.0
                    + ");");
          }
        };
    transition.play();
  }

  /* Transitions the Node's background color and text color over the course of milliseconds*/
  public static void fadeNodeWithText(
      Node node,
      Color textStart,
      Color textEnd,
      Color backgroundStart,
      Color backgroundEnd,
      int durationInMillis) {

    Animation transition =
        new Transition() {
          {
            setCycleDuration(Duration.millis(durationInMillis));
          }

          @Override
          protected void interpolate(double frac) {

            java.awt.Color backgroundcolor = blend(backgroundEnd, backgroundStart, frac);

            java.awt.Color textColor = blend(textEnd, textStart, frac);

            node.setStyle(
                "-fx-background-color:  rgba("
                    + backgroundcolor.getRed()
                    + ","
                    + backgroundcolor.getGreen()
                    + ","
                    + backgroundcolor.getBlue()
                    + ","
                    + backgroundcolor.getAlpha() / 255.0
                    + ")"
                    + "; -fx-text-fill: rgba("
                    + textColor.getRed()
                    + ","
                    + textColor.getGreen()
                    + ","
                    + textColor.getBlue()
                    + ","
                    + textColor.getAlpha() / 255.0
                    + ");");
          }
        };
    transition.play();
  }

  /* Slides  the background color across the Node and fades in the new text color*/
  public static void slideNodeWithText(
      Node node,
      Color textStart,
      Color textEnd,
      Color backgroundStart,
      Color backgroundEnd,
      int durationInMillis) {

    Animation transition =
        new Transition() {
          {
            setCycleDuration(Duration.millis(durationInMillis));
          }

          @Override
          protected void interpolate(double frac) {

            java.awt.Color textColor = blend(textEnd, textStart, frac);

            node.setStyle(
                "-fx-background-color:  linear-gradient("
                    + "from "
                    + (((int) (100 * frac)) - 0.1)
                    + "% "
                    + (((int) (100 * frac)) - 0.1)
                    + "% "
                    + "to "
                    + (int) (100 * frac)
                    + "% "
                    + (int) (100 * frac)
                    + "% "
                    + ",rgba("
                    + backgroundEnd.getRed()
                    + ","
                    + backgroundEnd.getGreen()
                    + ","
                    + backgroundEnd.getBlue()
                    + ","
                    + backgroundEnd.getAlpha() / 255.0
                    + "),"
                    + "rgba("
                    + backgroundStart.getRed()
                    + ","
                    + backgroundStart.getGreen()
                    + ","
                    + backgroundStart.getBlue()
                    + ","
                    + backgroundStart.getAlpha() / 255.0
                    + "))"
                    + "; -fx-text-fill: rgba("
                    + textColor.getRed()
                    + ","
                    + textColor.getGreen()
                    + ","
                    + textColor.getBlue()
                    + ","
                    + textColor.getAlpha() / 255.0
                    + ");");
          }
        };
    transition.play();
  }

  /* Performs an X and Y translation for Cole <3 */
  public static void ColesTrans(Node node, int xTrans, int yTrans, int durationInMillis) {

    Animation transition =
        new Transition() {
          {
            setCycleDuration(Duration.millis(durationInMillis));
          }

          @Override
          protected void interpolate(double frac) {
            node.setStyle(
                "-fx-translate-x: "
                    + ((int) frac * xTrans)
                    + "; -fx-translate-y: "
                    + ((int) frac * yTrans)
                    + ";");
          }
        };
    transition.play();
  }

  /* Finds the average color between two colors given a percentage weight */
  public static java.awt.Color blend(Color color1, Color color2, double ratio) {
    float r = (float) ratio;
    float ir = (float) 1.0 - r;

    float rgb1[] = new float[4];
    float rgb2[] = new float[4];

    color1.getComponents(rgb1);
    color2.getComponents(rgb2);

    float red = rgb1[0] * r + rgb2[0] * ir;
    float green = rgb1[1] * r + rgb2[1] * ir;
    float blue = rgb1[2] * r + rgb2[2] * ir;
    float transparency = rgb1[3] * r + rgb2[3] * ir;
    if (red < 0) {
      red = 0;
    } else if (red > 255) {
      red = 255;
    }
    if (green < 0) {
      green = 0;
    } else if (green > 255) {
      green = 255;
    }
    if (blue < 0) {
      blue = 0;
    } else if (blue > 255) {
      blue = 255;
    }
    if (transparency < 0) {
      transparency = 0;
    } else if (transparency > 255) {
      transparency = 255;
    }

    java.awt.Color color = null;
    try {
      color = new java.awt.Color(red, green, blue, transparency);
    } catch (IllegalArgumentException exp) {
      exp.printStackTrace();
    }
    return color;
  }
}

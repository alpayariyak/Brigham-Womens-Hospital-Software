package edu.wpi.DapperDaemons.APIConverters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Converter {

  /**
   * Get string formatted time
   *
   * @param minutesAfterNow minutes after the current time we want the time to be
   * @return the formatted date
   */
  public static String determineDateNeeded(int minutesAfterNow) {
    Date now = new Date();
    now.setTime(now.getTime() + (long) minutesAfterNow * 60000);
    DateFormat format = new SimpleDateFormat("MMddyyyy");
    return format.format(now);
  }
}

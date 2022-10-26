package edu.wpi.DapperDaemons.backend;

import edu.wpi.DapperDaemons.App;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class LogSaver {

  public static final boolean MAKE_MULTIPLE_LOGS = false;

  private LogSaver() {}

  public static void saveAll() {

    try {
      // Make logs folder outside of jar
      File logsFolder = new File("logs");
      if (!logsFolder.exists()) {
        logsFolder.mkdir();
      }
      File dest;
      if (MAKE_MULTIPLE_LOGS) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh;mm;ss");
        dest =
            new File(
                "logs/" + dateFormat.format(date) + ".log"); // Makes a log based on current time
      } else {
        dest = new File("logs/recentlog.log");
      }

      // Copy logs
      InputStream in =
          new BufferedInputStream(
              Objects.requireNonNull(
                  LogSaver.class.getClassLoader().getResourceAsStream("logs/app.log")));
      OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
      IOUtils.copy(in, out);
      in.close();
      out.close();
    } catch (IOException e) {
      System.out.println("FAILED: ");
      e.printStackTrace();
      App.LOG.error("Could not copy log file.");
    }
  }
}

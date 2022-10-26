package edu.wpi.DapperDaemons.backend;

import com.opencsv.CSVWriter;
import edu.wpi.DapperDaemons.entities.TableObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVSaver {
  private CSVSaver() {}

  public static void saveAll() {
    CSVLoader.filenames.forEach(
        (k, v) -> {
          try {
            save(v, k + "_save.csv");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  public static void saveAll(String path) {
    CSVLoader.filenames.forEach(
        (k, v) -> {
          try {
            save(v, path + "/" + k + ".csv");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  public static void save(TableObject type, String filename) throws IOException {

    DAO<TableObject> dao = DAOPouch.getDAO(type);
    File file = new File(filename);
    FileWriter outputFile = new FileWriter(file);
    CSVWriter writer =
        new CSVWriter(outputFile, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
    ArrayList<String> line = new ArrayList<>();
    for (TableObject t : dao.getAll().values()) {
      line = new ArrayList<>();
      for (int i = 1; i < 100; i++) {
        try {
          line.add(t.getAttribute(i));
        } catch (IndexOutOfBoundsException ignored) {
          break;
        }
      }
      writer.writeNext((String[]) line.toArray(new String[line.size()]));
    }
    writer.close();
  }
}

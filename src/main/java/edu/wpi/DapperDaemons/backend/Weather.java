package edu.wpi.DapperDaemons.backend;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.image.Image;

public class Weather {

  private Weather() {}

  public static int getTemp(String location) throws Exception {
    URL url =
        new URL(
            "https://api.openweathermap.org/data/2.5/weather?q="
                + location
                + "&appid=5214edbc18dc7cbd8c4c7abd482b6120&units=imperial");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();

    String jsonString = content.toString(); // assign your JSON String here
    JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
    return obj.getAsJsonObject("main").get("temp").getAsInt();
    //    return (int) obj.getJSONObject("main").getFloat("temp");
  }

  public static Image getIcon(String location) throws Exception {
    URL url =
        new URL(
            "https://api.openweathermap.org/data/2.5/weather?q="
                + location
                + "&appid=5214edbc18dc7cbd8c4c7abd482b6120&units=imperial");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();

    String jsonString = content.toString(); // assign your JSON String here
    JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
    String icon = obj.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
    //    JSONObject obj = new JSONObject(jsonString);
    //    String icon = obj.getJSONArray("weather").getJSONObject(0).getString("icon");

    return new Image(
        Weather.class
            .getClassLoader()
            .getResourceAsStream("edu/wpi/DapperDaemons/weatherIcons/" + icon + ".png"));
  }
}

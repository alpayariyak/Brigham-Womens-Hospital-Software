package edu.wpi.DapperDaemons.backend;

import org.junit.jupiter.api.Test;

class weatherTest {

  @Test
  void getWeather() throws Exception {
    Float res = Float.valueOf(Weather.getTemp("boston"));
    System.out.println(res);
  }
}

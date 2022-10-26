package edu.wpi.DapperDaemons.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class AccountTest {

  @Test
  void printSHA() throws NoSuchAlgorithmException {
    String password = "admin";
    System.out.println(toHexString(getSHA(password)));
  }

  /*@Test
  void testAccountFilter() throws SQLException, IOException {
    DAOPouch.init();
    DAO<Employee> employeeDAO = DAOPouch.getEmployeeDAO();
    DAO<Account> accountDAO = DAOPouch.getAccountDAO();
    List<Employee> user = employeeDAO.filter(1, accountDAO.get("tkevans").getAttribute(2));
    System.out.println(user);
  }*/

  @Test
  void checkPassword() throws NoSuchAlgorithmException {
    Account test = new Account("test", "1", "password");
    assertFalse(test.checkPassword("admin"));
    assertFalse(test.checkPassword("Password"));
    assertFalse(test.checkPassword("password "));
    assertTrue(test.checkPassword("password"));
  }

  @Test
  void testShaForFun() throws NoSuchAlgorithmException {
    String sha = toHexString(getSHA("plaspdlpal"));
    String sha1 = toHexString(getSHA("plaspdlpal "));
    System.out.println();
  }

  private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
    // Static getInstance method is called with hashing SHA
    MessageDigest md = MessageDigest.getInstance("SHA-256");

    // digest() method called
    // to calculate message digest of an input
    // and return array of byte
    return md.digest(input.getBytes(StandardCharsets.UTF_8));
  }

  private static String toHexString(byte[] hash) {
    // Convert byte array into signum representation
    BigInteger number = new BigInteger(1, hash);

    // Convert message digest into hex value
    StringBuilder hexString = new StringBuilder(number.toString(16));

    // Pad with leading zeros
    while (hexString.length() < 32) {
      hexString.insert(0, '0');
    }

    return hexString.toString();
  }
}

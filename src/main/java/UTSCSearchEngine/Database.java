package UTSCSearchEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

  private Connection connect() {

    String url = "jdbc:sqlite:database.db";
    Connection con = null;
    try {
      con = DriverManager.getConnection(url);
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    return con;
  }

  public void insertUser(String userId, String userType) {

    String sql = "INSERT INTO user(user_id, user_type) VALUES(?, ?)";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, userType);
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public void getUserById(String userId) {

    String sql = "SELECT * FROM user WHERE user_id = ?";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        System.out.println(rs.getInt("id"));
        System.out.println(rs.getString("user_id"));
        System.out.println(rs.getString("user_type"));
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}

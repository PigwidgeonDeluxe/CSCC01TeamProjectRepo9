package UTSCSearchEngine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

  private String url;

  public Database() {
    this.url = "jdbc:sqlite:database.db";
  }

  public Database(String url) {
    this.url = url;
  }

  public Connection connect() {

    Connection con = null;
    try {
      con = DriverManager.getConnection(this.url);
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    return con;
  }

  public void insertFileData(byte[] file, String fileName, String fileType, String userId,
      Long date) {

    String sql = "INSERT INTO file(file, file_name, file_type, file_size, user_id, uploaded_on) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setBytes(1, file);
      pstmt.setString(2, fileName);
      pstmt.setString(3, fileType);
      pstmt.setInt(4, file.length);
      pstmt.setString(5, userId);
      pstmt.setDate(6, date != null ? new Date(date) : new Date(System.currentTimeMillis()));
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public ResultSet getAllFiles() throws SQLException {

    String sql = "SELECT file.id, file, file_name, file_type, file_size, user_name, user_type, "
        + "user.user_id, uploaded_on FROM file INNER JOIN user ON file.user_id = user.user_id";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);
    return pstmt.executeQuery();
  }

  public ResultSet getFileById(String fileId) throws SQLException {

    String sql = "SELECT file_name, file_type, file_size, uploaded_on, user_name, user_type, "
        + "profile_image FROM file INNER JOIN user ON file.user_id = user.user_id WHERE file.id = ?";

    Connection con = connect();
    PreparedStatement pstmt =  con.prepareStatement(sql);

    pstmt.setString(1, fileId);
    return pstmt.executeQuery();
  }

  public void insertFileComment(String fileId, String comment, String userId, Long date) {
    String sql =
        "INSERT INTO comments(file_id, comment, user_id, date) VALUES (?, ?, ?, ?)";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, fileId);
      pstmt.setString(2, comment);
      pstmt.setString(3, userId);
      pstmt.setDate(4, date != null ? new Date(date) : new Date(System.currentTimeMillis()));
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public ResultSet getFileComments(String fileId) throws SQLException {
    String sql = "SELECT file_id, comment, date, user_name, user_type, user.user_id, profile_image "
        + "FROM comments INNER JOIN user ON comments.user_id = user.user_id WHERE file_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, fileId);
    return pstmt.executeQuery();
  }

  public ResultSet getFileData(String fileName, Long uploadTime) throws SQLException {

    String sql = "SELECT * FROM file WHERE file_name = ? AND uploaded_on = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, fileName);
    pstmt.setDate(2, new Date(uploadTime));
    return pstmt.executeQuery();
  }

  public ResultSet getUserFiles(String userId) throws SQLException {

    String sql = "SELECT file_name, file_type, user_type, user_name, file_size, uploaded_on, "
        + "file.id FROM file INNER JOIN user ON file.user_id = user.user_id WHERE file.user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }

  public void insertUser(String userId, String userType, String userName, String profileImage) {

    String sql = "INSERT INTO user(user_id, user_type, created_on, user_name, profile_image) "
        + "VALUES(?, ?, ?, ?, ?)";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, userType);
      pstmt.setDate(3, new Date(System.currentTimeMillis()));
      pstmt.setString(4, userName);
      pstmt.setString(5, profileImage);
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public ResultSet getUserById(String userId) throws SQLException {

    String sql = "SELECT * FROM user WHERE user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }
  
  public ResultSet getUserByName(String name) throws SQLException {

    String sql = "SELECT * FROM user WHERE user_name LIKE ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, "%" + name + "%");
    return pstmt.executeQuery();
  }

  public void followUser(String userId, String followingUserId) {

    String sql = "INSERT INTO following(user_id, following_user_id) VALUES(?, ?)";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, followingUserId);
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public void unfollowUser(String userId, String followingUserId) {

    String sql = "DELETE FROM following WHERE user_id = ? AND following_user_id = ?";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, followingUserId);
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public ResultSet getFollowing(String userId) throws SQLException {

    String sql = "SELECT user.user_id, user_name, user_type, created_on, profile_image, "
        + "following_user_id FROM user INNER JOIN following ON user.user_id = "
        + "following.following_user_id WHERE following.user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }

  public ResultSet getFileTypeStatistics() throws SQLException {

    String sql = "SELECT file_type, COUNT(file_type) FROM file GROUP BY file_type";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    return pstmt.executeQuery();
  }

  public ResultSet getFileUploaderStatistics() throws SQLException {

    String sql = "SELECT user_name, COUNT(user_name) FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id GROUP BY user_name";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    return pstmt.executeQuery();
  }

  public ResultSet getFileSizeStatistics() throws SQLException {

    String sql = "SELECT file_name, file_size FROM file";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    return pstmt.executeQuery();
  }

  public ResultSet getUserFileTypeStatistics(String userName) throws SQLException {

    String sql = "SELECT file_type, COUNT(file_type) FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id WHERE user_name = ? GROUP BY file_type";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);
    pstmt.setString(1, userName);

    return pstmt.executeQuery();
  }

  public ResultSet getUserFileSizeStatistics(String userName) throws SQLException {

    String sql = "SELECT file_name, file_size FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id WHERE user_name = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);
    pstmt.setString(1, userName);

    return pstmt.executeQuery();
  }

  public void bookmarkFile(String fileId, String userId) {

    String sql = "INSERT INTO bookmark (file_id, user_id) VALUES (?, ?)";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, fileId);
      pstmt.setString(2, userId);
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public void unbookmarkFile(String fileId, String userId) {

    String sql = "DELETE FROM bookmark WHERE file_id = ? AND user_id = ?";

    try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, fileId);
      pstmt.setString(2, userId);
      pstmt.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public ResultSet getBookmarks(String userId) throws SQLException {

    String sql = "SELECT file_name, file_type, file_size, user.user_name, user.user_type, "
        + "uploaded_on, file.id FROM bookmark INNER JOIN file ON file.id = bookmark.file_id "
        + "INNER JOIN user ON user.user_id = file.user_id WHERE bookmark.user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }

}

package UTSCSearchEngine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for handling database interactions
 */
public class Database {

  private String url;

  // database connection used for testing
  public Database() {
    this.url = "jdbc:sqlite:database.db";
  }

  public Database(String url) {
    this.url = url;
  }

  /**
   * Creates connection to SQLite database and returns said connection
   * @return Connection object representing the connection to the database
   */
  public Connection connect() {

    Connection con = null;
    try {
      con = DriverManager.getConnection(this.url);
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    return con;
  }

  /**
   * Inserts a file into the database
   * @param file the byte array of the file
   * @param fileName the name of the file
   * @param fileType the type of the file (the file extension)
   * @param userId the userId of the user uploading the file
   * @param date the date that the file was uploaded in milliseconds
   */
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

  /**
   * Returns every file stored in the database
   * @return ResultSet containing every file in the database
   * @throws SQLException if the query is invalid
   */
  public ResultSet getAllFiles() throws SQLException {

    String sql = "SELECT file.id, file, file_name, file_type, file_size, user_name, user_type, "
        + "user.user_id, uploaded_on FROM file INNER JOIN user ON file.user_id = user.user_id";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);
    return pstmt.executeQuery();
  }

  /**
   * Returns the file information of a given file given its ID
   * @param fileId the ID of the requested file
   * @return ResultSet containing the file's information
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFileById(String fileId) throws SQLException {

    String sql = "SELECT file_name, file_type, file_size, uploaded_on, user_name, "
        + "user_type, profile_image FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id WHERE file.id = ?";

    Connection con = connect();
    PreparedStatement pstmt =  con.prepareStatement(sql);

    pstmt.setString(1, fileId);
    return pstmt.executeQuery();
  }

  /**
   * Inserts a comment on a given file
   * @param fileId the ID of the file to be commented
   * @param comment the content of the comment to be added to the file
   * @param userId the ID of the user leaving the comment
   * @param date the date the comment was left on in milliseconds
   */
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

  /**
   * Returns all the comments on a given file
   * @param fileId the ID of the requested file
   * @return ResultSet containing all the comments on the requested file
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFileComments(String fileId) throws SQLException {
    String sql = "SELECT file_id, comment, date, user_name, user_type, user.user_id, profile_image "
        + "FROM comments INNER JOIN user ON comments.user_id = user.user_id WHERE file_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, fileId);
    return pstmt.executeQuery();
  }

  /**
   * Returns all the information of a requested file given its name and upload time
   * @param fileName the name of the requested file
   * @param uploadTime the time the file was uploaded in milliseconds
   * @return ResultSet containing the data of the requested file
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFileData(String fileName, Long uploadTime) throws SQLException {

    String sql = "SELECT * FROM file WHERE file_name = ? AND uploaded_on = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, fileName);
    pstmt.setDate(2, new Date(uploadTime));
    return pstmt.executeQuery();
  }

  /**
   * Returns all the files uploaded by a given user
   * @param userId the ID of the requested user
   * @return ResultSet containing all the information of the requested file
   * @throws SQLException if the query is invalid
   */
  public ResultSet getUserFiles(String userId) throws SQLException {

    String sql = "SELECT file_name, file_type, user_type, user_name, file_size, uploaded_on, "
        + "file.id FROM file INNER JOIN user ON file.user_id = user.user_id WHERE file.user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }

  /**
   * Inserts a new user into the database
   * @param userId the ID of the given user
   * @param userType the type of the given user
   * @param userName the name of the given user
   * @param profileImage the URL of the user's profile image
   */
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

  /**
   * Returns the information of a user given its ID
   * @param userId the ID of the requested user
   * @return ResultSet containing all the information of the requested user
   * @throws SQLException if the query is invalid
   */
  public ResultSet getUserById(String userId) throws SQLException {

    String sql = "SELECT * FROM user WHERE user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }

  /**
   * Returns all the users with a name similar to the given query
   * @param name the name to be used to search through the database
   * @return ResultSet of all users with a name similar to the given query
   * @throws SQLException if the query is invalid
   */
  public ResultSet getUserByName(String name) throws SQLException {

    String sql = "SELECT * FROM user WHERE user_name LIKE ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, "%" + name + "%");
    return pstmt.executeQuery();
  }

  /**
   * Creates a follow link between two given users
   * @param userId the "following" user
   * @param followingUserId the "followed" user
   */
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

  /**
   * Removes a follow link between two given users
   * @param userId the "following" user
   * @param followingUserId the "followed" user
   */
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

  /**
   * Returns all the users a given user is following
   * @param userId the ID of the requested user
   * @return ResultSet of all users the given users is following
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFollowing(String userId) throws SQLException {

    String sql = "SELECT user.user_id, user_name, user_type, created_on, profile_image, "
        + "following_user_id FROM user INNER JOIN following ON user.user_id = "
        + "following.following_user_id WHERE following.user_id = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    pstmt.setString(1, userId);
    return pstmt.executeQuery();
  }

  /**
   * Returns the statistics of file types of files stored in the database
   * @return ResultSet of the statistics of file types stored in the database
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFileTypeStatistics() throws SQLException {

    String sql = "SELECT file_type, COUNT(file_type) FROM file GROUP BY file_type";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    return pstmt.executeQuery();
  }

  /**
   * Returns the statistics of the uploaders who have uploaded to the system
   * @return ResultSet of the statistics of the uploaders who have uploaded to the system
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFileUploaderStatistics() throws SQLException {

    String sql = "SELECT user_name, COUNT(user_name) FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id GROUP BY user_name";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    return pstmt.executeQuery();
  }

  /**
   * Returns the statistics of files that have been uploaded to the system
   * @return ResultSet of the statistics of the files that have been uploaded to the system
   * @throws SQLException if the query is invalid
   */
  public ResultSet getFileSizeStatistics() throws SQLException {

    String sql = "SELECT file_name, file_size FROM file";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);

    return pstmt.executeQuery();
  }

  /**
   * Returns the file type statistics of a given user
   * @param userName the requested user's name
   * @return ResultSet of the statistics of the given user
   * @throws SQLException if the query is invalid
   */
  public ResultSet getUserFileTypeStatistics(String userName) throws SQLException {

    String sql = "SELECT file_type, COUNT(file_type) FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id WHERE user_name = ? GROUP BY file_type";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);
    pstmt.setString(1, userName);

    return pstmt.executeQuery();
  }

  /**
   * Returns the file size statistics of a given user
   * @param userName the requested user's name
   * @return ResultSet of the statistics of the given user
   * @throws SQLException if the query is invalid
   */
  public ResultSet getUserFileSizeStatistics(String userName) throws SQLException {

    String sql = "SELECT file_name, file_size FROM file INNER JOIN user ON file.user_id = "
        + "user.user_id WHERE user_name = ?";

    Connection con = connect();
    PreparedStatement pstmt = con.prepareStatement(sql);
    pstmt.setString(1, userName);

    return pstmt.executeQuery();
  }

  /**
   * Creates a bookmark link between a user and a file
   * @param fileId the file to be bookmarked
   * @param userId the user who wants to create a bookmark to said file
   */
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

  /**
   * Removes a bookmark link between a user and a bookmarked file
   * @param fileId the file to be bookmarked
   * @param userId the user who wants to create a bookmark to said file
   */
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

  /**
   * Returns all bookmarks associated to a given user
   * @param userId the ID of the requested user
   * @return ResultSet containing all the bookmarks associated to a given user
   * @throws SQLException if the query is invalid
   */
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

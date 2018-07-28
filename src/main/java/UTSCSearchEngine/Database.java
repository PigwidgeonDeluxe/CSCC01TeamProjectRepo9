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

	public void insertFileData(byte[] file, String fileName, String fileType, String userId, Long date) {

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
				+ "uploaded_on FROM file INNER JOIN user ON file.user_id = user.user_id";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);
		return pstmt.executeQuery();
	}

	public ResultSet getFileById(String fileId) throws SQLException {

		String sql = "SELECT file_name, file_type, file_size, uploaded_on, user_name, user_type, "
				+ "profile_image FROM file INNER JOIN user ON file.user_id = user.user_id WHERE file.id = ?";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);

		pstmt.setString(1, fileId);
		return pstmt.executeQuery();
	}

	public void insertFileComment(String fileId, String comment, String userId, Long date) {
		String sql = "INSERT INTO comments(file_id, comment, user_id, date) VALUES (?, ?, ?, ?)";

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
		String sql = "SELECT file_id, comment, date, user_name, user_type, profile_image FROM comments "
				+ "INNER JOIN user ON comments.user_id = user.user_id WHERE file_id = ?";

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

	public void insertUser(String userId, String userType, String userName, String profileImage) {

		String sql = "INSERT INTO user(user_id, user_type, created_on, user_name, profile_image, follow_num, update_file_name) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, userId);
			pstmt.setString(2, userType);
			pstmt.setDate(3, new Date(System.currentTimeMillis()));
			pstmt.setString(4, userName);
			pstmt.setString(5, profileImage);
			pstmt.setInt(6, 0);
			pstmt.setString(7, "none");
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

	public void insertUserFollow(String userId, String userIdFollow) {

		String sql = "INSERT INTO follow(user_id, follow_id) VALUES (?, ?)";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, userId);
			pstmt.setString(2, userIdFollow);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void updateUserFollowNum(String userId, int followNum) {

		String sql = "UPDATE users SET follow_num = ? WHERE user_id = ?";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, followNum);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ResultSet getUserFollow(String userId) throws SQLException {

		String sql = "SELECT * FROM follow WHERE user_id = ?";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);

		pstmt.setString(1, userId);
		return pstmt.executeQuery();
	}

	public void updateUserUpdate(String userId, String fileName) {

		String sql = "UPDATE user SET update_file_name = ? WHERE user_id = ?";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, fileName);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}

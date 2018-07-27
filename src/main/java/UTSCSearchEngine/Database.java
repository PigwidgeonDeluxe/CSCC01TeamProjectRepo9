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

	public void insertFileData(byte[] file, String fileName, String fileType, String uploaderName, String uploaderType,
			Long date) {

		String sql = "INSERT INTO file(file, file_name, file_type, file_size, uploader_name, "
				+ "uploader_type, uploaded_on) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setBytes(1, file);
			pstmt.setString(2, fileName);
			pstmt.setString(3, fileType);
			pstmt.setInt(4, file.length);
			pstmt.setString(5, uploaderName);
			pstmt.setString(6, uploaderType);
			pstmt.setDate(7, date != null ? new Date(date) : new Date(System.currentTimeMillis()));
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ResultSet getAllFiles() throws SQLException {

		String sql = "SELECT * FROM file";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);
		return pstmt.executeQuery();
	}

	public ResultSet getFileById(String fileId) throws SQLException {

		String sql = "SELECT * FROM file WHERE id = ?";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);

		pstmt.setString(1, fileId);
		return pstmt.executeQuery();
	}

	public void insertFileComment(String fileId, String comment, String comment_user, Long date) {
		String sql = "INSERT INTO comments(file_id, comment, comment_user, date) VALUES (?, ?, ?, ?)";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, fileId);
			pstmt.setString(2, comment);
			pstmt.setString(3, comment_user);
			pstmt.setDate(4, date != null ? new Date(date) : new Date(System.currentTimeMillis()));
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ResultSet getFileComments(String fileId) throws SQLException {
		String sql = "SELECT * FROM comments WHERE file_id = ?";

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

	public void insertUser(String userId, String userType) {

		String sql = "INSERT INTO user(user_id, user_type, created_on, follow_num) VALUES(?, ?, ?, ?)";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, userId);
			pstmt.setString(2, userType);
			pstmt.setDate(3, new Date(System.currentTimeMillis()));
			pstmt.setInt(4, 0);
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

	public void updateUserFollow(String userId, int followNum) {

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

		String sql = "SELECT * FROM comments WHERE user_id = ?";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);

		pstmt.setString(1, userId);
		return pstmt.executeQuery();
	}

	public void insertUserUpdate(String userId, String fileId) {

		String sql = "INSERT INTO update(user_id, file_id) VALUES (?, ?)";

		try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, userId);
			pstmt.setString(2, fileId);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ResultSet getUserUpdate(String userId) throws SQLException {

		String sql = "SELECT * FROM comments WHERE user_id = ?";

		Connection con = connect();
		PreparedStatement pstmt = con.prepareStatement(sql);

		pstmt.setString(1, userId);
		return pstmt.executeQuery();
	}
}

package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import UTSCSearchEngine.Database;

public class FollowTest {

	private String url = "jdbc:sqlite:test-database.db";

	@Before
	public void setUp() throws SQLException {
		Database db = new Database(this.url);
		Connection con = db.connect();

		String dropTableUser = "DROP TABLE IF EXISTS user";
		PreparedStatement pstmt1 = con.prepareStatement(dropTableUser);
		pstmt1.execute();

		String createTableUser = "CREATE TABLE user(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, "
				+ "user_type TEXT, created_on INTEGER. follow_num INTEGER, update_file_id TEXT)";
		PreparedStatement pstmt2 = con.prepareStatement(createTableUser);
		pstmt2.execute();

		String dropTableFollow = "DROP TABLE IF EXISTS follow";
		PreparedStatement pstmt3 = con.prepareStatement(dropTableFollow);
		pstmt3.execute();

		String createTableFollow = "CREATE TABLE follow(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, "
				+ "follow_id TEXT)";
		PreparedStatement pstmt4 = con.prepareStatement(createTableFollow);
		pstmt4.execute();
	}

	@Test
	public void testDatabaseFollow() throws SQLException {
		Database db = new Database(this.url);
		db.insertUser("1234", "student");
		db.insertUser("5678", "student");
		db.insertUserFollow("1234", "5678");
		db.updateUserFollowNum("1234", 1);

		ResultSet user1 = db.getUserById("1234");
		ResultSet user2 = db.getUserById("5678");
		ResultSet follow = db.getUserFollow("1234");

		assertEquals(1, user1.getInt("follow_num"));
		assertEquals(0, user2.getInt("follow_num"));
		assertEquals("5678", follow.getString("follow_id"));
		user1.close();
		user2.close();
		follow.close();
	}

	@Test
	public void testDatabaseUpdate() throws SQLException {
		Database db = new Database(this.url);
		db.insertUser("1234", "student");
		db.insertUser("5678", "student");
		db.updateUserUpdate("1234", "file");

		ResultSet user1 = db.getUserById("1234");
		ResultSet user2 = db.getUserById("5678");

		assertEquals("file", user1.getString("file_id"));
		assertEquals("none", user2.getString("file_id"));
		user1.close();
		user2.close();
	}
}

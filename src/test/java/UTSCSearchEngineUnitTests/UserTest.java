package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import UTSCSearchEngine.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

  private String url = "jdbc:sqlite:test-database.db";

  @Before
  public void setUp() throws SQLException {
    Database db = new Database(this.url);
    Connection con = db.connect();

    String dropTable = "DROP TABLE IF EXISTS user";
    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
    pstmt1.execute();

    String createTable = "CREATE TABLE user(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, "
        + "user_type TEXT, created_on INTEGER, user_name TEXT, profile_image TEXT)";
    PreparedStatement pstmt2 = con.prepareStatement(createTable);
    pstmt2.execute();
  }

  @Test
  public void testDatabaseInsertStudent() throws SQLException {

    Database db = new Database(this.url);
    db.insertUser("1234", "student", "test_user", "testurl");
    ResultSet rs = db.getUserById("1234");
    assertEquals("1234", rs.getString("user_id"));
    assertEquals("student", rs.getString("user_type"));
    rs.close();
  }

  @Test
  public void testDatabaseInsertInstructor() throws SQLException {

    Database db = new Database(this.url);
    db.insertUser("1234", "instructor", "test_user", "testurl");
    ResultSet rs = db.getUserById("1234");
    assertEquals("1234", rs.getString("user_id"));
    assertEquals("instructor", rs.getString("user_type"));
    rs.close();
  }

}

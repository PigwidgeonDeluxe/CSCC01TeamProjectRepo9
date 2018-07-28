package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import UTSCSearchEngine.Database;
import UTSCSearchEngine.UserSearch;

public class UserSearchTest {

  private String url = "jdbc:sqlite:test-database.db";

  /**
   * Set up the database for testing **do not use real database, all data will be erased**
   * 
   * @throws SQLException
   * @throws IOException
   */
  @Before
  public void setUp() throws SQLException, IOException {

    Database db = new Database(this.url);
    Connection con = db.connect();

    // cleanup
    String dropTable = "DROP TABLE IF EXISTS user";
    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
    pstmt1.executeUpdate();
    pstmt1.close();

    // create table
    String createTable =
        "CREATE TABLE user (id integer primary key autoincrement, user_id integer, user_type text,"
            + " created_on integer, user_name text, profile_image text)";
    PreparedStatement pstmt2 = con.prepareStatement(createTable);
    pstmt2.executeUpdate();
    pstmt2.close();

    // insert test user
    db.insertUser("0", "Instructor", "miles baby", "200x");
    db.insertUser("0", "Instructor", "miles driver", "200x");
    con.close();
  }

  @Test
  public void testDoGet() throws IOException {
    Database db = new Database(this.url);

    UserSearch search = new UserSearch(db);
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    when(req.getParameter("userName")).thenReturn("miles");
    when(resp.getWriter()).thenReturn(writer);

    search.doGet(req, resp);

    writer.flush(); // ensure writer is flushed
    // ensure the response to the front end has the designed formatting
    assertEquals("miles baby\nmiles driver\n", stringWriter.toString());
  }

}

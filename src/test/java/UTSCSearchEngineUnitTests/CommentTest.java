package UTSCSearchEngineUnitTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import UTSCSearchEngine.Comment;
import UTSCSearchEngine.Database;

public class CommentTest {

  private String url = "jdbc:sqlite:test-database.db";

  @Before
  public void setUp() throws SQLException {

    Database db = new Database(this.url);
    Connection con = db.connect();

    // cleanup
    String dropTable = "DROP TABLE IF EXISTS comments";
    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
    pstmt1.executeUpdate();
    pstmt1.close();

    // create table
    String createTable =
        "CREATE TABLE comments (id INTEGER PRIMARY KEY AUTOINCREMENT, file_id INTEGER, "
            + "user_id INTEGER, comment TEXT, date INTEGER)";
    PreparedStatement pstmt2 = con.prepareStatement(createTable);
    pstmt2.executeUpdate();
    pstmt2.close();

    // insert test comment
    db.insertUser("comment_user", "student", "test user", "test.jpg");
    db.insertFileComment("0", "test comment", "comment_user", null);
    con.close();
  }

  @Test
  public void getComment() throws IOException {
    Database db = new Database(this.url);
    Comment comment = new Comment(db);

    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    when(req.getParameter("docId")).thenReturn("0");
    when(resp.getWriter()).thenReturn(writer);
    comment.doGet(req, resp);

    JSONObject json = new JSONObject(stringWriter.toString());
    assertTrue(json.getString("comments").contains("test comment"));
    assertTrue(json.getString("comments").contains("comment_user"));
  }
}

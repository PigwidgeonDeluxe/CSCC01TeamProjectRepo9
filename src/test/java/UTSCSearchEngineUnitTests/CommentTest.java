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

import org.junit.Before;
import org.junit.Test;

import UTSCSearchEngine.Comment;
import UTSCSearchEngine.Database;

public class CommentTest {

//  private String url = "jdbc:sqlite:test-database.db";
//
//  /**
//   * Set up the database for testing **do not use real database, all data will be erased**
//   *
//   * @throws SQLException
//   * @throws IOException
//   */
//  @Before
//  public void setUp() throws SQLException, IOException {
//
//    Database db = new Database(this.url);
//    Connection con = db.connect();
//
//    // cleanup
//    String dropTable = "DROP TABLE IF EXISTS comments";
//    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
//    pstmt1.executeUpdate();
//    pstmt1.close();
//
//    // create table
//    String createTable =
//        "CREATE TABLE comments (id integer primary key autoincrement, file_id integer, "
//            + "comment text, comment_user text, date integer)";
//    PreparedStatement pstmt2 = con.prepareStatement(createTable);
//    pstmt2.executeUpdate();
//    pstmt2.close();
//
//    // insert test comment
//    db.insertFileComment("0", "test comment", "comment_user", (long) 100);
//    con.close();
//  }
//
//  @Test
//  public void testDoGet() throws IOException {
//    Database db = new Database(this.url);
//    Comment comment = new Comment(db);
//    HttpServletRequest req = mock(HttpServletRequest.class);
//    HttpServletResponse resp = mock(HttpServletResponse.class);
//
//    StringWriter stringWriter = new StringWriter();
//    PrintWriter writer = new PrintWriter(stringWriter);
//
//    when(req.getParameter("docId")).thenReturn("0");
//    when(resp.getWriter()).thenReturn(writer);
//
//    comment.doGet(req, resp);
//
//    writer.flush(); // ensure writer is flushed
//    // ensure the response to the front end has the designed formatting
//    assertEquals("Ensure the response to the front end has the designed formatting.",
//        "{\"comments\":\"0~test comment~comment_user~100\\n\"}", stringWriter.toString());
//  }
//
//  @Test
//  public void testDoPost() throws SQLException {
//    Database db = new Database(this.url);
//    Comment comment = new Comment(db);
//    HttpServletRequest req = mock(HttpServletRequest.class);
//    HttpServletResponse resp = mock(HttpServletResponse.class);
//
//    // mock user requests
//    when(req.getParameter("docId")).thenReturn("0");
//    when(req.getParameter("comment")).thenReturn("test comment 2");
//    when(req.getParameter("commentUser")).thenReturn("test user 2");
//
//    comment.doPost(req, resp);
//
//    ResultSet result = db.getFileComments("0");
//    ArrayList<String> idList = new ArrayList<String>();
//    ArrayList<String> commentList = new ArrayList<String>();
//    ArrayList<String> userList = new ArrayList<String>();
//    ArrayList<String> dateList = new ArrayList<String>();
//
//    while (result.next()) {
//      idList.add(result.getString("file_id"));
//      commentList.add(result.getString("comment"));
//      userList.add(result.getString("comment_user"));
//      dateList.add(result.getString("date"));
//    }
//
//    // check that both comments exist and are correct
//    assertEquals(2, idList.size());
//    assertEquals("0", idList.get(0));
//    assertEquals("test comment", commentList.get(0));
//    assertEquals("comment_user", userList.get(0));
//    assertEquals("0", idList.get(1));
//    assertEquals("test comment 2", commentList.get(1));
//    assertEquals("test user 2", userList.get(1));
//
//    result.close();
//  }
}

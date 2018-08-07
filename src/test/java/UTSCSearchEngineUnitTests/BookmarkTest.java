package UTSCSearchEngineUnitTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import UTSCSearchEngine.Bookmark;
import UTSCSearchEngine.Database;

public class BookmarkTest {

  private String url = "jdbc:sqlite:test-database.db";

  @Before
  public void setUp() throws SQLException {

    Database db = new Database(this.url);
    Connection con = db.connect();

    // cleanup
    String dropTable = "DROP TABLE IF EXISTS bookmark";
    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
    pstmt1.executeUpdate();
    pstmt1.close();

    String dropFile = "DROP TABLE IF EXISTS file";
    PreparedStatement pstmtf = con.prepareStatement(dropFile);
    pstmtf.executeUpdate();
    pstmtf.close();

    // create table
    String createTable =
        "CREATE TABLE bookmark (id INTEGER PRIMARY KEY AUTOINCREMENT, file_id INTEGER, "
            + "user_id INTEGER)";
    PreparedStatement pstmt2 = con.prepareStatement(createTable);
    pstmt2.executeUpdate();
    pstmt2.close();

    // create table
    String createFileTable =
        "CREATE TABLE file (id INTEGER PRIMARY KEY AUTOINCREMENT, file BLOB, file_name TEXT, file_type TEXT, file_size INTEGER, user_id INTEGER, uploaded_on INTEGER)";
    PreparedStatement pstmt3 = con.prepareStatement(createFileTable);
    pstmt3.executeUpdate();
    pstmt3.close();

    // insert test bookmark
    db.insertUser("comment_user", "student", "test_user", "test.jpg");
    byte[] testFile = new byte[2];
    db.insertFileData(testFile, "filename", "fileType", "comment_user", (long) 1);
    db.bookmarkFile("1", "comment_user");
    con.close();
  }

  @Test
  public void testDoGet() throws IOException {
    Database db = new Database(this.url);
    Bookmark bookmark = new Bookmark(db);

    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    when(req.getParameter("userId")).thenReturn("comment_user");
    when(resp.getWriter()).thenReturn(writer);

    bookmark.doGet(req, resp);
    String[] output = stringWriter.toString().split("~");
    assertTrue(output[0].contains("filename"));
    assertTrue(output[3].contains("test_user"));
  }

}

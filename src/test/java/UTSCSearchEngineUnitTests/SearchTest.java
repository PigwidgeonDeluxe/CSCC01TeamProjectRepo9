package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import UTSCSearchEngine.Database;
import UTSCSearchEngine.Search;
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

public class SearchTest {

  private String url = "jdbc:sqlite:test-database.db";

  @Before
  public void setUp() throws SQLException {

    Database db = new Database(this.url);
    Connection con = db.connect();

    String dropTable = "DROP TABLE IF EXISTS file";
    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
    pstmt1.execute();

    String createTable = "CREATE TABLE file (id integer primary key autoincrement, file blob, "
        + "file_name text, file_type text, file_size integer, uploader_name text, uploader_type "
        + "text, uploaded_on integer)";
    PreparedStatement pstmt2 = con.prepareStatement(createTable);
    pstmt2.execute();
  }

  @Test
  public void testSearchNonExistentFile() throws IOException {

    // instantiate search controller
    Search search = new Search();

    // mock servlet
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    // mock servlet methods
    when(mockRequest.getParameter("fileName")).thenReturn("test");
    when(mockResponse.getWriter()).thenReturn(printWriter);

    // call servlet
    search.callIndexing();
    search.doGet(mockRequest, mockResponse);

    // assert
    stringWriter.flush();
    assertEquals("", stringWriter.toString());
  }

  @Test
  public void testSearchExistentFile() throws IOException {

    Search search = new Search();

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    Database db = new Database(this.url);
    byte[] file = new byte[0];
    String fileName = "test file.txt";
    String fileType = "txt";
    String uploaderName = "test user";
    String uploaderType = "student";

    db.insertFileData(file, fileName, fileType, uploaderName, uploaderType);

    // test search by file name
    when(mockRequest.getParameter("fileName")).thenReturn("test");
    when(mockResponse.getWriter()).thenReturn(printWriter);
    search.callIndexing(this.url);
    search.doGet(mockRequest, mockResponse);
    stringWriter.flush();
    assertTrue(stringWriter.toString().contains("test file.txt~txt~student~test user"));

    // test search by file type
    when(mockRequest.getParameter("fileType")).thenReturn("txt");
    when(mockResponse.getWriter()).thenReturn(printWriter);
    search.doGet(mockRequest, mockResponse);
    stringWriter.flush();
    assertTrue(stringWriter.toString().contains("test file.txt~txt~student~test user"));

    // test search by user type
    when(mockRequest.getParameter("userType")).thenReturn("student");
    when(mockResponse.getWriter()).thenReturn(printWriter);
    search.doGet(mockRequest, mockResponse);
    stringWriter.flush();
    assertTrue(stringWriter.toString().contains("test file.txt~txt~student~test user"));

    // test search by user name
    when(mockRequest.getParameter("userName")).thenReturn("test");
    when(mockResponse.getWriter()).thenReturn(printWriter);
    search.doGet(mockRequest, mockResponse);
    stringWriter.flush();
    assertTrue(stringWriter.toString().contains("test file.txt~txt~student~test user"));
  }
}

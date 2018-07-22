package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import UTSCSearchEngine.Database;
import UTSCSearchEngine.FileDownload;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;

public class FileDownloadTest {

//  private String url = "jdbc:sqlite:test-database.db";
//
//  @Before
//  public void setUp() throws SQLException {
//
//    Database db = new Database(this.url);
//    Connection con = db.connect();
//
//    // cleanup
//    String dropTable = "DROP TABLE IF EXISTS file";
//    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
//    pstmt1.executeUpdate();
//    pstmt1.close();
//
//    // create table
//    String createTable = "CREATE TABLE file (id integer primary key autoincrement, file blob, "
//        + "file_name text, file_type text, file_size integer, uploader_name text, uploader_type "
//        + "text, uploaded_on integer)";
//    PreparedStatement pstmt2 = con.prepareStatement(createTable);
//    pstmt2.executeUpdate();
//    pstmt2.close();
//
//    // insert a sample file
//    byte[] fileContent = "this is some sample text".getBytes(Charset.forName("UTF-8"));
//    String txtFileName = "test file.txt";
//    String txtFileType = "txt";
//    String uploaderName = "test user";
//    String uploaderType = "student";
//    db.insertFileData(fileContent, txtFileName, txtFileType, uploaderName, uploaderType, 1234L);
//  }
//
//  @Test
//  public void testFileDownload() throws IOException {
//
//    FileDownload download = new FileDownload();
//
//    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
//    StringWriter stringWriter = new StringWriter();
//    PrintWriter printWriter = new PrintWriter(stringWriter);
//
//    when(mockRequest.getParameter("fileName")).thenReturn("test");
//    when(mockRequest.getParameter("uploadTime")).thenReturn("1234");
//    when(mockResponse.getWriter()).thenReturn(printWriter);
//
//    download.doGet(mockRequest, mockResponse);
//
//    stringWriter.flush();
//    System.out.println(stringWriter.toString());
//    assertTrue(stringWriter.toString().contains(new String("this is some sample text".getBytes())));
//  }
}

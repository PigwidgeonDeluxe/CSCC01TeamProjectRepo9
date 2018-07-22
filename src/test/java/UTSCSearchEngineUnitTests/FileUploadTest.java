package UTSCSearchEngineUnitTests;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import UTSCSearchEngine.Database;
import UTSCSearchEngine.FileUpload;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.Test;

public class FileUploadTest {

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
//  }
//
//  @Test
//  public void testFileUpload() throws IOException {
//
//    FileUpload upload = new FileUpload();
//
//    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
//    FileItem mockFileItem = mock(FileItem.class);
//    StringWriter stringWriter = new StringWriter();
//    PrintWriter printWriter = new PrintWriter(stringWriter);
//
//    when(mockRequest.getParameter("userName")).thenReturn("test user");
//    when(mockRequest.getParameter("userType")).thenReturn("student");
//    when(mockResponse.getWriter()).thenReturn(printWriter);
//    when(mockFileItem.isFormField()).thenReturn(false);
//    when(mockFileItem.get()).thenReturn("this is a test".getBytes());
//    when(mockFileItem.getName()).thenReturn("test file.txt");
//    when(mockFileItem.getName().substring(mockFileItem.getName().lastIndexOf('.')
//        + 1)).thenReturn("txt");
//
//    upload.doPost(mockRequest, mockResponse);
//
//    stringWriter.flush();
//    assertTrue(stringWriter.toString().contains("SUCCESS"));
//  }
}
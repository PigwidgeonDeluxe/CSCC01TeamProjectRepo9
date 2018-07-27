package UTSCSearchEngine;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import UTSCSearchEngineUnitTests.TestUtils;

public class CommentTest {

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
    String dropTable = "DROP TABLE IF EXISTS file";
    PreparedStatement pstmt1 = con.prepareStatement(dropTable);
    pstmt1.executeUpdate();
    pstmt1.close();

    // create table
    String createTable = "CREATE TABLE file (id integer primary key autoincrement, file blob, "
        + "file_name text, file_type text, file_size integer, uploader_name text, uploader_type "
        + "text, uploaded_on integer)";
    PreparedStatement pstmt2 = con.prepareStatement(createTable);
    pstmt2.executeUpdate();
    pstmt2.close();

    // insert a sample txt file
    byte[] fileContent = "this is some sample text".getBytes(Charset.forName("UTF-8"));
    String txtFileName = "test file.txt";
    String txtFileType = "txt";
    String uploaderName = "test user";
    String uploaderType = "student";
    db.insertFileData(fileContent, txtFileName, txtFileType, uploaderName, uploaderType, null);

    // insert a sample doc file
    byte[] docContent = TestUtils.createDocFile("this is some sample text").toByteArray();
    String docFileName = "test file.docx";
    String docFileType = "docx";
    db.insertFileData(docContent, docFileName, docFileType, uploaderName, uploaderType, null);

    // insert an HTML file
    byte[] htmlContent = ("<!DOCTYPE html>\n" + "<html>\n" + "<body>\n" + "\n"
        + "<h1>My First Heading</h1>\n<p>my first paragraph</p>\n" + "\n" + "</body>\n" + "</html>")
            .getBytes(Charset.forName("UTF-8"));
    String htmlFileName = "test file.html";
    String htmlFileType = "html";
    db.insertFileData(htmlContent, htmlFileName, htmlFileType, uploaderName, uploaderType, null);

    // insert a PDF file
    byte[] pdfContent = TestUtils.createPdfFile("this is some sample text").toByteArray();
    String pdfFileName = "test file.pdf";
    String pdfFileType = "pdf";
    db.insertFileData(pdfContent, pdfFileName, pdfFileType, uploaderName, uploaderType, null);
  }

  @Test
  public void testDoGetHttpServletRequestHttpServletResponse() {
    fail("Not yet implemented");
  }

  @Test
  public void testDoPostHttpServletRequestHttpServletResponse() {
    fail("Not yet implemented");
  }

}

package UTSCSearchEngineUnitTests;

import UTSCSearchEngine.Database;
import UTSCSearchEngine.Search;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;
import UTSCSearchEngine.Indexing;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndexingTest {

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
  public void testDoIndexing() throws IOException {

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

    when(mockRequest.getParameter("fileName")).thenReturn("test");
    when(mockResponse.getWriter()).thenReturn(printWriter);

    search.callIndexing(this.url);
    search.doGet(mockRequest, mockResponse);

    stringWriter.flush();
    assertTrue(stringWriter.toString().contains("test file.txt~txt~test user~0~student"));
  }


  @Test
  public void testGetIndex() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    Directory testDir = new RAMDirectory();
    Field field = indexer.getClass().getDeclaredField("index");
    field.setAccessible(true);
    field.set(indexer, testDir);
    final Directory result = indexer.getIndex();

    assertEquals("Directory wasn't retrieved properly", testDir, result);

  }

  @Test
  public void testGetAnalyzer() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    StandardAnalyzer testAnalyzer  = new StandardAnalyzer();
    Field field = indexer.getClass().getDeclaredField("analyzer");
    field.setAccessible(true);
    field.set(indexer, testAnalyzer);
    final StandardAnalyzer result = indexer.getAnalyzer();

    assertEquals("Analyzer wasn't retrieved properly", testAnalyzer, result);
  }


}

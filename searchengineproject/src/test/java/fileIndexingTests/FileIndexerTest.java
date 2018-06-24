package fileIndexingTests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

/*
 * Unit testing for FileIndexer. No Mock objects were used because FileIndexer has no missing
 * dependencies.
 */
public class FileIndexerTest {

  // create temporary folder for testing
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testEndToEnd() {
    try {

      // this folder gets cleaned up automatically by JUnit
      File file1 = folder.newFile("test file 1.txt");
      File file2 = folder.newFile("test file 2.txt");
      File file3 = folder.newFile("test file 3.txt");
      File folder1 = folder.newFolder("test folder");

      List<String> expectedTitles = Arrays.asList("test file 1", "test file 2", "test file 3");
      List<String> expectedPath = Arrays.asList(file1.getPath(), file2.getPath(), file3.getPath());

      try {

        MockFrontEnd mockFront = new MockFrontEnd(folder.getRoot().toString(), "test");

        List<String> titleList = mockFront.getTitles();
        List<String> pathList = mockFront.getPaths();
        // List<String> modifiedList = mockFront.getModified();

        assertTrue("all titles must be accounted for and correct",
            titleList.containsAll(expectedTitles));
        assertTrue("all titles must be accounted for and correct",
            expectedTitles.containsAll(titleList));

        assertTrue("all paths must be correct", pathList.containsAll(expectedPath));
        assertTrue("all paths must be accounted for", expectedPath.containsAll(pathList));


      } catch (ParseException e) {
        fail("Unexpected ParesException occurred");
        e.printStackTrace();
      }

    } catch (IOException e) {
      System.out.println(e);
    }

  }
  
  @Test
  public void testNewFileReindexing() {
    try {

      // this folder gets cleaned up automatically by JUnit
      File file1 = folder.newFile("test file 1.txt");
      File file2 = folder.newFile("test file 2.txt");
      File file3 = folder.newFile("test file 3.txt");
      File folder1 = folder.newFolder("test folder");

      List<String> expectedTitles = Arrays.asList("test file 1", "test file 2", "test file 3");
      List<String> expectedTitles2 = Arrays.asList("test file 1", "test file 2", "test file 3", "test file 4");
      List<String> expectedPath = Arrays.asList(file1.getPath(), file2.getPath(), file3.getPath());

      try {

        MockFrontEnd mockFront = new MockFrontEnd(folder.getRoot().toString(), "test");

        List<String> titleList = mockFront.getTitles();
        List<String> pathList = mockFront.getPaths();
        // List<String> modifiedList = mockFront.getModified();

        assertTrue("all titles must be accounted for and correct",
            titleList.containsAll(expectedTitles));
        assertTrue("all titles must be accounted for and correct",
            expectedTitles.containsAll(titleList));

        assertTrue("all paths must be correct", pathList.containsAll(expectedPath));
        assertTrue("all paths must be accounted for", expectedPath.containsAll(pathList));
        

      } catch (ParseException e) {
        fail("Unexpected ParesException occurred");
        e.printStackTrace();
      }

    } catch (IOException e) {
      System.out.println(e);
    }

  }
}

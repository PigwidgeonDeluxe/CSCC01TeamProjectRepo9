package fileIndexingTests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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

      List<String> expectedTitles = Arrays.asList("test file 1.txt", "test file 2.txt", "test file 3.txt");
      List<String> expectedPath = Arrays.asList(file1.getParent(), file2.getParent(), file3.getParent());

      try {

        MockFrontEnd mockFront = new MockFrontEnd(folder.getRoot().toString(), "test");

        List<String> titleList = mockFront.getTitles();
        List<String> pathList = mockFront.getPaths();

        Collections.sort(titleList);
        Collections.sort(pathList);
        // List<String> modifiedList = mockFront.getModified();

        assertEquals("all titles must be accounted for and correct",expectedTitles, titleList);
        assertEquals("all paths must be correct", expectedPath, pathList);

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

      List<String> expectedTitles =
          Arrays.asList("test file 1.txt", "test file 2.txt", "test file 3.txt");
      List<String> expectedPath = Arrays.asList(file1.getParent(), file2.getParent(), file3.getParent());


      try {

        MockFrontEnd mockFront = new MockFrontEnd(folder.getRoot().toString(), "test");

        List<String> titleList = mockFront.getTitles();
        List<String> pathList = mockFront.getPaths();
        Collections.sort(titleList);
        Collections.sort(pathList);

        // List<String> modifiedList = mockFront.getModified();
        assertEquals("all titles must be accounted for and correct",expectedTitles, titleList);
        assertEquals("all paths must be correct", expectedPath, pathList);

        // create a new file
        File file4 = folder.newFile("new test file 4.txt");

        mockFront.reIndexing();
        
        List<String> titleList2 = mockFront.getTitles();
        List<String> pathList2 = mockFront.getPaths();

        List<String> expectedTitles2 = Arrays.asList( "new test file 4.txt", "test file 1.txt", "test file 2.txt",
            "test file 3.txt");
        List<String> expectedPath2 =
            Arrays.asList(file1.getParent(), file2.getParent(), file3.getParent(), file4.getParent());

        Collections.sort(titleList2);
        Collections.sort(pathList2);

        // List<String> modifiedList = mockFront.getModified();

        assertEquals("all new titles must be accounted for and correct",expectedTitles2, titleList2);
        assertEquals("all new paths must be correct", expectedPath2, pathList2);

      } catch (ParseException e) {
        fail("Unexpected ParesException occurred");
        e.printStackTrace();
      }

    } catch (IOException e) {
      System.out.println(e);
    }

  }
}

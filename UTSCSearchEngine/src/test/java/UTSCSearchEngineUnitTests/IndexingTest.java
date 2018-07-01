package UTSCSearchEngineUnitTests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import UTSCSearchEngine.Indexing;
import static org.junit.Assert.*;

public class IndexingTest {

  // create temporary folder for testing
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testDoIndexing() throws ParseException {
    try {

      // this folder gets cleaned up automatically by JUnit
      File file1 = folder.newFile("test file 1.txt");
      File file2 = folder.newFile("test file 2.txt");
      File file3 = folder.newFile("test file 3.txt");
      File folder1 = folder.newFolder("test folder");

      List<String> expectedFileNames =
          Arrays.asList("test file 1.txt", "test file 2.txt", "test file 3.txt");

      Indexing indexer = new Indexing();

      indexer.setDocsPath(folder.getRoot().toString());
      indexer.doIndexing();

      //search for all test files
      Query q = new QueryParser("fileName", indexer.getAnlyzer()).parse("test");
      IndexReader reader = DirectoryReader.open(indexer.getIndex());
      IndexSearcher searcher = new IndexSearcher(reader);
      TopDocs docs = searcher.search(q, 10);
      ScoreDoc[] hits = docs.scoreDocs;
      System.out.println(reader.toString());
      List<String> titleList = new ArrayList<String>();
      for (int i = 0; i < hits.length; ++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        titleList.add(d.get("fileName"));
      }
      
      Collections.sort(titleList);
      assertEquals("all files must be accounted for and correct", expectedFileNames, titleList);

    } catch (IOException e) {
      System.out.println(e);
    }

  }

  /*
   * @Test void testSetDocsPath() { fail("Not yet implemented"); }
   * 
   * @Test void testGetIndex() { fail("Not yet implemented"); }
   * 
   * @Test void testGetAnlyzer() { fail("Not yet implemented"); }
   * 
   * @Test void testGetDocDir() { fail("Not yet implemented"); }
   */

}

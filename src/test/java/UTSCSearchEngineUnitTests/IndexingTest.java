package UTSCSearchEngineUnitTests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
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
  public void testDoIndexing() throws ParseException, NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException, IOException {
    // this folder gets cleaned up automatically by JUnit
    File file1 = folder.newFile("test file 1.txt");
    File file2 = folder.newFile("test file 2.txt");
    File file3 = folder.newFile("test file 3.txt");
    File folder1 = folder.newFolder("test folder");

    List<String> expectedFileNames =
        Arrays.asList("test file 1.txt", "test file 2.txt", "test file 3.txt");

    Indexing indexer = new Indexing();

    // indexer.setDocsPath(folder.getRoot().toString());
    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    // search for all test files
    Query q = new QueryParser("fileName", indexer.getAnalyzer()).parse("test");
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

  }


  @Test
  public void testSetDocsPath() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    indexer.setDocsPath("examplepath");

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    assertEquals("Docs path didn't match", field.get(indexer), "examplepath");
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
  public void testGetAnlyzer() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    StandardAnalyzer testAnalyzer  = new StandardAnalyzer();
    Field field = indexer.getClass().getDeclaredField("analyzer");
    field.setAccessible(true);
    field.set(indexer, testAnalyzer);
    final StandardAnalyzer result = indexer.getAnalyzer();

    assertEquals("Analyzer wasn't retrieved properly", testAnalyzer, result);
  }

  @Test
  public void testGetDocDir() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    Path testPath  =  null;
    Field field = indexer.getClass().getDeclaredField("analyzer");
    field.setAccessible(true);
    field.set(indexer, testPath);
    final Path result = indexer.getDocDir();

    assertEquals("Path wasn't retrieved properly", testPath, result);
  }


}

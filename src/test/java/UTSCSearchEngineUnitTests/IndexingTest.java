package UTSCSearchEngineUnitTests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
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
    File file3 = folder.newFile("test file 3.doc");

    // set up expected lines
    String file1Line1 = "Line 1 foo";
    String file1Line2 = "Line 2 fighters";
    String file2Line1 = "Line 1 foo";
    String file2Line2 = "Line 2 bar";

    List<String> expectedContents = new ArrayList<String>();
    expectedContents.add("Line 1 foo\nLine 2 fighters\n");
    expectedContents.add("Line 1 foo\nLine 2 fighters\n");
    expectedContents.add("Line 1 foo\nLine 2 bar\n");


    // set up contents for the test files
    List<String> file1Contents = new ArrayList<String>();
    List<String> file2Contents = new ArrayList<String>();

    file1Contents.add(file1Line1);
    file1Contents.add(file1Line2);
    file2Contents.add(file2Line1);
    file2Contents.add(file2Line2);

    writeToFile(file1, file1Contents);
    writeToFile(file2, file2Contents);
    writeToFile(file3, file1Contents);

    List<String> expectedFileNames =
        Arrays.asList("test file 1.txt", "test file 2.txt", "test file 3.doc");

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
    // System.out.println(reader.toString());

    // get all the file names and contents
    List<String> titleList = new ArrayList<String>();
    List<String> contentsList = new ArrayList<String>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      titleList.add(d.get("fileName"));
      contentsList.add(d.get("contents"));
    }

    Collections.sort(titleList);
    assertEquals("all files must be accounted for and correct", expectedFileNames, titleList);
    assertEquals("all contents must be accounted for and correct", expectedContents, contentsList);

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
  public void testGetAnlyzer() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    StandardAnalyzer testAnalyzer = new StandardAnalyzer();
    Field field = indexer.getClass().getDeclaredField("analyzer");
    field.setAccessible(true);
    field.set(indexer, testAnalyzer);
    final StandardAnalyzer result = indexer.getAnalyzer();

    assertEquals("Analyzer wasn't retrieved properly", testAnalyzer, result);
  }

  @Test
  public void testGetDocDir() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Indexing indexer = new Indexing();
    Path testPath = null;
    Field field = indexer.getClass().getDeclaredField("analyzer");
    field.setAccessible(true);
    field.set(indexer, testPath);
    final Path result = indexer.getDocDir();

    assertEquals("Path wasn't retrieved properly", testPath, result);
  }

  /**
   * Method that writes to given file for given contents
   * 
   * @param file
   * @param contents
   */
  private void writeToFile(File file, List<String> contents) {
    BufferedWriter br = null;
    try {
      br = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    PrintWriter out = new PrintWriter(br);

    // setup contents of temp file
    for (String line : contents) {
      out.println(line);
    }
    out.close();
  }

  /**
   * Method that reads from doc files
   * 
   * @param file
   * @return
   */
  private String[] parseDocContents(File file) {
    WordExtractor extractor = null;
    String[] fileData = null;
    try {
      FileInputStream fis = new FileInputStream(file.getAbsolutePath());
      HWPFDocument document = new HWPFDocument(fis);
      extractor = new WordExtractor(document);
      fileData = extractor.getParagraphText();
    } catch (Exception exep) {
      exep.printStackTrace();
    }

    return fileData;
  }

}

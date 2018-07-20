package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import UTSCSearchEngine.Indexing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SearchTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  /**
   * @Test public void testSearchByFilename() throws NoSuchFieldException, IllegalAccessException,
   *       ParseException, IOException {
   * 
   *       File txtFile1 = folder.newFile("test file1.txt"); File txtFile2 = folder.newFile("test
   *       file2.txt"); File pdfFile = folder.newFile("sample file.pdf"); File docxFile =
   *       folder.newFile("word document.docx"); File testFolder = folder.newFolder();
   * 
   *       List<String> case1 = Arrays.asList("test file1.txt", "test file2.txt"); List<String>
   *       case2 = Arrays.asList("sample file.pdf"); List<String> case3 = Arrays.asList("word
   *       document.docx");
   * 
   *       Indexing indexer = new Indexing();
   * 
   *       Field field = indexer.getClass().getDeclaredField("docsPath"); field.setAccessible(true);
   *       field.set(indexer, folder.getRoot().toString()); indexer.doIndexing();
   * 
   *       Query q = new QueryParser("fileName", indexer.getAnalyzer()).parse("test"); IndexReader
   *       reader = DirectoryReader.open(indexer.getIndex()); IndexSearcher searcher = new
   *       IndexSearcher(reader); TopDocs docs = searcher.search(q, 10); ScoreDoc[] hits =
   *       docs.scoreDocs; List<String> case1List = new ArrayList<>(); for (int i = 0; i <
   *       hits.length; ++i) { int docId = hits[i].doc; Document d = searcher.doc(docId);
   *       case1List.add(d.get("fileName")); } Collections.sort(case1List); assertEquals("improperly
   *       matched files", case1, case1List);
   * 
   *       q = new QueryParser("fileName", indexer.getAnalyzer()).parse("sample"); docs =
   *       searcher.search(q, 10); hits = docs.scoreDocs; List<String> case2List = new
   *       ArrayList<>(); for (int i = 0; i < hits.length; ++i) { int docId = hits[i].doc; Document
   *       d = searcher.doc(docId); case2List.add(d.get("fileName")); } Collections.sort(case2List);
   *       assertEquals("improperly matched files", case2, case2List);
   * 
   *       q = new QueryParser("fileName", indexer.getAnalyzer()).parse("word"); docs =
   *       searcher.search(q, 10); hits = docs.scoreDocs; List<String> case3List = new
   *       ArrayList<>(); for (int i = 0; i < hits.length; ++i) { int docId = hits[i].doc; Document
   *       d = searcher.doc(docId); case3List.add(d.get("fileName")); } Collections.sort(case3List);
   *       assertEquals("improperly matched files", case3, case3List); }
   * 
   * @Test public void testSearchByFileType() throws NoSuchFieldException, IllegalAccessException,
   *       ParseException, IOException {
   * 
   *       File txtFile1 = folder.newFile("test file1.txt"); File txtFile2 = folder.newFile("test
   *       file2.txt"); File pdfFile = folder.newFile("sample file.pdf"); File docxFile =
   *       folder.newFile("word document.docx"); File testFolder = folder.newFolder();
   * 
   *       List<String> case1 = Arrays.asList("txt", "txt"); List<String> case2 =
   *       Arrays.asList("pdf"); List<String> case3 = Arrays.asList("docx");
   * 
   *       Indexing indexer = new Indexing();
   * 
   *       Field field = indexer.getClass().getDeclaredField("docsPath"); field.setAccessible(true);
   *       field.set(indexer, folder.getRoot().toString()); indexer.doIndexing();
   * 
   *       Query q = new QueryParser("fileType", indexer.getAnalyzer()).parse("txt"); IndexReader
   *       reader = DirectoryReader.open(indexer.getIndex()); IndexSearcher searcher = new
   *       IndexSearcher(reader); TopDocs docs = searcher.search(q, 10); ScoreDoc[] hits =
   *       docs.scoreDocs; List<String> case1List = new ArrayList<>(); for (int i = 0; i <
   *       hits.length; ++i) { int docId = hits[i].doc; Document d = searcher.doc(docId);
   *       case1List.add(d.get("fileType")); } Collections.sort(case1List); assertEquals("improperly
   *       matched files", case1, case1List);
   * 
   *       q = new QueryParser("fileType", indexer.getAnalyzer()).parse("pdf"); docs =
   *       searcher.search(q, 10); hits = docs.scoreDocs; List<String> case2List = new
   *       ArrayList<>(); for (int i = 0; i < hits.length; ++i) { int docId = hits[i].doc; Document
   *       d = searcher.doc(docId); case2List.add(d.get("fileType")); } Collections.sort(case2List);
   *       assertEquals("improperly matched files", case2, case2List);
   * 
   *       q = new QueryParser("fileType", indexer.getAnalyzer()).parse("docx"); docs =
   *       searcher.search(q, 10); hits = docs.scoreDocs; List<String> case3List = new
   *       ArrayList<>(); for (int i = 0; i < hits.length; ++i) { int docId = hits[i].doc; Document
   *       d = searcher.doc(docId); case3List.add(d.get("fileType")); } Collections.sort(case3List);
   *       assertEquals("improperly matched files", case3, case3List); }
   * 
   * @Test public void testSearchByUserType() throws NoSuchFieldException, IllegalAccessException,
   *       ParseException, IOException {
   * 
   *       File txtFile1 = folder.newFile("test file1.txt"); File txtFile2 = folder.newFile("test
   *       file2.txt"); File pdfFile = folder.newFile("sample file.pdf"); File docxFile =
   *       folder.newFile("word document.docx"); File testFolder = folder.newFolder();
   * 
   *       List<String> testCase = Arrays.asList("sample file.pdf", "test file1.txt", "test
   *       file2.txt", "word document.docx");
   * 
   *       Indexing indexer = new Indexing();
   * 
   *       Field field = indexer.getClass().getDeclaredField("docsPath"); field.setAccessible(true);
   *       field.set(indexer, folder.getRoot().toString()); indexer.doIndexing();
   * 
   *       Query q = new QueryParser("userType", indexer.getAnalyzer()).parse("student");
   *       IndexReader reader = DirectoryReader.open(indexer.getIndex()); IndexSearcher searcher =
   *       new IndexSearcher(reader); TopDocs docs = searcher.search(q, 10); ScoreDoc[] hits =
   *       docs.scoreDocs; List<String> case1List = new ArrayList<>(); for (int i = 0; i <
   *       hits.length; ++i) { int docId = hits[i].doc; Document d = searcher.doc(docId);
   *       case1List.add(d.get("fileName")); } Collections.sort(case1List); assertEquals("improperly
   *       matched files", testCase, case1List); }
   * 
   * @Test public void testSearchByUserName() throws NoSuchFieldException, IllegalAccessException,
   *       ParseException, IOException {
   * 
   *       File txtFile1 = folder.newFile("test file1.txt"); File txtFile2 = folder.newFile("test
   *       file2.txt"); File pdfFile = folder.newFile("sample file.pdf"); File docxFile =
   *       folder.newFile("word document.docx"); File testFolder = folder.newFolder();
   * 
   *       List<String> testCase = Arrays.asList("sample file.pdf", "test file1.txt", "test
   *       file2.txt", "word document.docx");
   * 
   *       Indexing indexer = new Indexing();
   * 
   *       Field field = indexer.getClass().getDeclaredField("docsPath"); field.setAccessible(true);
   *       field.set(indexer, folder.getRoot().toString()); indexer.doIndexing();
   * 
   *       Query q = new QueryParser("userName", indexer.getAnalyzer()).parse("user"); IndexReader
   *       reader = DirectoryReader.open(indexer.getIndex()); IndexSearcher searcher = new
   *       IndexSearcher(reader); TopDocs docs = searcher.search(q, 10); ScoreDoc[] hits =
   *       docs.scoreDocs; List<String> case1List = new ArrayList<>(); for (int i = 0; i <
   *       hits.length; ++i) { int docId = hits[i].doc; Document d = searcher.doc(docId);
   *       case1List.add(d.get("fileName")); } Collections.sort(case1List); assertEquals("improperly
   *       matched files", testCase, case1List); }
   * 
   * @Test public void testSearchByContents() { // To-DO }
   */

  @Test
  public void testSearchByContentDOCX()
      throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {


    // set up paragraphs
    String content1 = "The quick brown fox jumped over the lazy dog.";
    String content2 =
        "This section gives instructions on how to format and generate a Microsoft Word file.";
    String content3 =
        "Notice that we hard-code the contents of both the title and subtitle as these statements are too short to justify the use of a helper method.";

    // create the docx files
    CreateDOCX.create(folder.getRoot().toString(), "test file 1.docx", content1);
    CreateDOCX.create(folder.getRoot().toString(), "test file 2.docx", content2);
    CreateDOCX.create(folder.getRoot().toString(), "test file 3.docx", content3);


    List<String> testCase = Arrays.asList("test file 1.docx");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("contents", indexer.getAnalyzer()).parse("fox");
    IndexReader reader = DirectoryReader.open(indexer.getIndex());
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, 10);
    ScoreDoc[] hits = docs.scoreDocs;
    List<String> case1List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case1List.add(d.get("fileName"));
    }
    Collections.sort(case1List);
    assertEquals("improperly matched files", testCase, case1List);
  }

  @Test
  public void testSearchByContentTXT()
      throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {
    File file1 = folder.newFile("test file 1.txt");
    File file2 = folder.newFile("test file 2.txt");
    File file3 = folder.newFile("test file 3.txt");

    // set up expected lines
    String file1Line1 = "Line 1 foo";
    String file1Line2 = "Line 2 fighters";
    String file2Line1 = "Line 1 foo";
    String file2Line2 = "Line 2 bar";


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

    List<String> testCase = Arrays.asList("test file 1.txt", "test file 3.txt");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("contents", indexer.getAnalyzer()).parse("fighters");
    IndexReader reader = DirectoryReader.open(indexer.getIndex());
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, 10);
    ScoreDoc[] hits = docs.scoreDocs;
    List<String> case1List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case1List.add(d.get("fileName"));
    }
    Collections.sort(case1List);
    assertEquals("improperly matched files", testCase, case1List);
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
}

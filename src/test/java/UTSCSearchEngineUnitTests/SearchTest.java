package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import UTSCSearchEngine.Indexing;
import java.io.File;
import java.io.IOException;
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
/**
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testSearchByFilename()
      throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

    File txtFile1 = folder.newFile("test file1.txt");
    File txtFile2 = folder.newFile("test file2.txt");
    File pdfFile = folder.newFile("sample file.pdf");
    File docxFile = folder.newFile("word document.docx");
    File testFolder = folder.newFolder();

    List<String> case1 = Arrays.asList("test file1.txt", "test file2.txt");
    List<String> case2 = Arrays.asList("sample file.pdf");
    List<String> case3 = Arrays.asList("word document.docx");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("fileName", indexer.getAnalyzer()).parse("test");
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
    assertEquals("improperly matched files", case1, case1List);

    q = new QueryParser("fileName", indexer.getAnalyzer()).parse("sample");
    docs = searcher.search(q, 10);
    hits = docs.scoreDocs;
    List<String> case2List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case2List.add(d.get("fileName"));
    }
    Collections.sort(case2List);
    assertEquals("improperly matched files", case2, case2List);

    q = new QueryParser("fileName", indexer.getAnalyzer()).parse("word");
    docs = searcher.search(q, 10);
    hits = docs.scoreDocs;
    List<String> case3List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case3List.add(d.get("fileName"));
    }
    Collections.sort(case3List);
    assertEquals("improperly matched files", case3, case3List);
  }

  @Test
  public void testSearchByFileType()
      throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

    File txtFile1 = folder.newFile("test file1.txt");
    File txtFile2 = folder.newFile("test file2.txt");
    File pdfFile = folder.newFile("sample file.pdf");
    File docxFile = folder.newFile("word document.docx");
    File testFolder = folder.newFolder();

    List<String> case1 = Arrays.asList("txt", "txt");
    List<String> case2 = Arrays.asList("pdf");
    List<String> case3 = Arrays.asList("docx");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("fileType", indexer.getAnalyzer()).parse("txt");
    IndexReader reader = DirectoryReader.open(indexer.getIndex());
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, 10);
    ScoreDoc[] hits = docs.scoreDocs;
    List<String> case1List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case1List.add(d.get("fileType"));
    }
    Collections.sort(case1List);
    assertEquals("improperly matched files", case1, case1List);

    q = new QueryParser("fileType", indexer.getAnalyzer()).parse("pdf");
    docs = searcher.search(q, 10);
    hits = docs.scoreDocs;
    List<String> case2List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case2List.add(d.get("fileType"));
    }
    Collections.sort(case2List);
    assertEquals("improperly matched files", case2, case2List);

    q = new QueryParser("fileType", indexer.getAnalyzer()).parse("docx");
    docs = searcher.search(q, 10);
    hits = docs.scoreDocs;
    List<String> case3List = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      case3List.add(d.get("fileType"));
    }
    Collections.sort(case3List);
    assertEquals("improperly matched files", case3, case3List);
  }

  @Test
  public void testSearchByUserType()
      throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

    File txtFile1 = folder.newFile("test file1.txt");
    File txtFile2 = folder.newFile("test file2.txt");
    File pdfFile = folder.newFile("sample file.pdf");
    File docxFile = folder.newFile("word document.docx");
    File testFolder = folder.newFolder();

    List<String> testCase =
        Arrays.asList("sample file.pdf", "test file1.txt", "test file2.txt", "word document.docx");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("userType", indexer.getAnalyzer()).parse("student");
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
  public void testSearchByUserName()
      throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

    File txtFile1 = folder.newFile("test file1.txt");
    File txtFile2 = folder.newFile("test file2.txt");
    File pdfFile = folder.newFile("sample file.pdf");
    File docxFile = folder.newFile("word document.docx");
    File testFolder = folder.newFolder();

    List<String> testCase =
        Arrays.asList("sample file.pdf", "test file1.txt", "test file2.txt", "word document.docx");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docsPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("userName", indexer.getAnalyzer()).parse("user");
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
  public void testSearchByContents() {
    // To-DO
  }
  */
}

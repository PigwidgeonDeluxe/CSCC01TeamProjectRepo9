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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SearchTest {

  private TemporaryFolder folder;
  private File txtFile1;
  private File txtFile2;
  private File pdfFile;
  private File docxFile;
  private File testFolder;

  @BeforeClass
  public void setup() throws IOException {
    folder = new TemporaryFolder();
    txtFile1 = folder.newFile("test file1.txt");
    txtFile2 = folder.newFile("test file2.txt");
    pdfFile = folder.newFile("sample file.pdf");
    docxFile = folder.newFile("word document.docx");
    testFolder = folder.newFolder();
  }


  @Test
  public void testSearchByFilename() throws NoSuchFieldException, IllegalAccessException,
      ParseException, IOException {

    List<String> case1 = Arrays.asList("test file1.txt", "test file2.txt");
    List<String> case2 = Arrays.asList("sample file.pdf");
    List<String> case3 = Arrays.asList("word document.docx");

    Indexing indexer = new Indexing();

    Field field = indexer.getClass().getDeclaredField("docPath");
    field.setAccessible(true);
    field.set(indexer, folder.getRoot().toString());
    indexer.doIndexing();

    Query q = new QueryParser("fileName", indexer.getAnalyzer()).parse("test");
    IndexReader reader = DirectoryReader.open(indexer.getIndex());
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, 10);
    ScoreDoc[] hits = docs.scoreDocs;
    List<String> titleList = new ArrayList<>();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      titleList.add(d.get("fileName"));
    }
    Collections.sort(titleList);
    assertEquals("improperly matched files", case1, titleList);

    q = new QueryParser("fileName", indexer.getAnalyzer()).parse("sample");
    docs = searcher.search(q, 10);
    hits = docs.scoreDocs;
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      titleList.add(d.get("fileName"));
    }
    Collections.sort(titleList);
    assertEquals("improperly matched files", case2, titleList);

    q = new QueryParser("fileName", indexer.getAnalyzer()).parse("word");
    docs = searcher.search(q, 10);
    hits = docs.scoreDocs;
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      titleList.add(d.get("fileName"));
    }
    Collections.sort(titleList);
    assertEquals("improperly matched files", case3, titleList);
  }
}

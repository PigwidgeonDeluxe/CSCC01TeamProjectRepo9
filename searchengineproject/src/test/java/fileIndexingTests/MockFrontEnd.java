package fileIndexingTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import fileIndexing.FileIndexer;

public class MockFrontEnd {

  public List<String> titleList = new ArrayList<>();
  public List<String> pathList = new ArrayList<>();
  // public List<String> modifiedList = new ArrayList<>();
  public static FileIndexer indexer = new FileIndexer();
  public String path = null;
  public String querystr = null;

  public MockFrontEnd(String path, String querystr) throws IOException, ParseException {
    this.path = path;
    this.querystr = querystr;
    reIndexing();
  }
  
  public void reIndexing() throws IOException, ParseException{
    titleList = new ArrayList<>();
    pathList = new ArrayList<>();
    // modifiedList = new ArrayList<>()
        
    indexer.setDocsPath(path);

    indexer.doIndexing();
    Directory index = indexer.getIndex();
    StandardAnalyzer analyzer = indexer.getAnalyzer();

    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = new QueryParser("title", analyzer).parse(querystr);

    TopDocs docs = searcher.search(q, 10);
    ScoreDoc[] hits = docs.scoreDocs;
    StringBuilder responseBackToUser = new StringBuilder();
    responseBackToUser.append("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      titleList.add(d.get("title"));
      pathList.add(d.get("path"));
      // modifiedList.add(d.get("modified"));
    }
  }

  public List<String> getTitles() {
    return titleList;
  }

  public List<String> getPaths() {
    return pathList;
  }

  /*
  public List<String> getModified() {
    return modifiedList;
  }*/
}

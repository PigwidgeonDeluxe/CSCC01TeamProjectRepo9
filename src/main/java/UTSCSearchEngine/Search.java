package UTSCSearchEngine;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

@WebServlet("/search")
public class Search extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private StandardAnalyzer analyzer = null;
  private static Directory index = null;
  private Indexing indexer = new Indexing();

  public void init(ServletConfig config1) throws ServletException {
    super.init(config1);
    System.out.println("Started: init");
    callIndexing();
    System.out.println("Finished: init");
  }

  public void callIndexing() {
    this.indexer.doIndexing();
    this.analyzer = indexer.getAnalyzer();
    index = indexer.getIndex();
  }

  public void callIndexing(String url) {
    this.indexer.doIndexing(url);
    this.analyzer = indexer.getAnalyzer();
    index = indexer.getIndex();
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String fileNameQuery = req.getParameter("fileName");
    if (fileNameQuery != null) {
      try {
        Query q = new QueryParser("fileName", analyzer).parse(fileNameQuery);
        search(q, resp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String fileTypeQuery = req.getParameter("fileType");
    if (fileTypeQuery != null) {
      try {
        Query q = new QueryParser("fileType", analyzer).parse(fileTypeQuery);
        search(q, resp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String userNameQuery = req.getParameter("userName");
    if (userNameQuery != null) {
      try {
        Query q = new QueryParser("userName", analyzer).parse(userNameQuery);
        search(q, resp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String userTypeQuery = req.getParameter("userType");
    if (userTypeQuery != null) {
      try {
        Query q = new QueryParser("userType", analyzer).parse(userTypeQuery);
        search(q, resp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String userContentQuery = req.getParameter("contents");
    if (userContentQuery != null) {
      try {
        Query q = new QueryParser("contents", analyzer).parse(userContentQuery);
        search(q, resp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Search for Query q and return a response to the user containing the requested information
   * 
   * @param q
   * @param resp
   * @throws IOException
   */
  private static void search(Query q, HttpServletResponse resp) throws IOException {
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;
    StringBuilder responseBackToUser = new StringBuilder();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      responseBackToUser.append(d.get("fileName") + "~"
          + d.get("fileType") + "~"
          + d.get("userType") + "~"
          + d.get("userName") + "~"
          + d.get("fileSize") + "~"
          + d.get("uploadDate") + "~"
          + "\"" + d.get("contents").substring(0, Math.min(d.get("contents").length(), 160))
          + "\"\n");
    }
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());
  }
}

package fileIndexing;

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
public class BasicFrontEnd extends HttpServlet {

  private static StandardAnalyzer analyzer = null;
  private static Directory index = null;
  private static final long serialVersionUID = 1L;
  private static FileIndexer indexer = new FileIndexer();

  public void init(ServletConfig config1) throws ServletException {
    super.init(config1);
  }

  /*
   * Modified from Abbas's example (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    indexer.doIndexing();
    // get the index
    index = indexer.getIndex();
    analyzer = indexer.getAnalyzer();
    resp.setContentType("text/plain");
    String querystr = req.getParameter("query");
    try {
      /*
       * 1. Query object, created that encapusulates the user query
       * 
       * 2. IndexReader that allows you to read the index.
       * 
       * 3. IndexSearcher that allows you to take the query and search the index.
       */
      Query q = new QueryParser("title", analyzer).parse(querystr);
      int hitsPerPage = 10;
      IndexReader reader = DirectoryReader.open(index);
      IndexSearcher searcher = new IndexSearcher(reader);
      TopDocs docs = searcher.search(q, hitsPerPage);
      ScoreDoc[] hits = docs.scoreDocs;
      StringBuilder responseBackToUser = new StringBuilder();
      responseBackToUser.append("Found " + hits.length + " hits.");
      for (int i = 0; i < hits.length; ++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        responseBackToUser.append("<br />" + (i + 1) + ". " + "\t" + d.get("title") + " @"
            + d.get("path") + " time:" + d.get("modified"));
      }
      resp.getWriter().write(responseBackToUser.toString());

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}

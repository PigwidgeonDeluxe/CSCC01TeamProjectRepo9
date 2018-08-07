package UTSCSearchEngine;

import java.io.IOException;

import java.util.HashSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;

/**
 * Class for handling searching of files
 */
@WebServlet("/search")
public class Search extends HttpServlet {

  private static StandardAnalyzer analyzer = null;
  private static Directory index = null;
  private static Indexing indexer = new Indexing();

  /**
   * Initializes and starts the main App
   * @param config1 configuration to start the app
   * @throws ServletException if the app encounters an error and cannot be initialized
   */
  public void init(ServletConfig config1) throws ServletException {
    super.init(config1);
    System.out.println("Started: init");
    callIndexing();
    System.out.println("Finished: init");
  }

  /**
   * Indexes the files in the system
   */
  public static void callIndexing() {
    indexer.doIndexing();
    analyzer = indexer.getAnalyzer();
    index = indexer.getIndex();
  }

  /**
   * Overloaded index call for testing purposes
   * @param url test database URL
   */
  public void callIndexing(String url) {
    indexer.doIndexing(url);
    analyzer = indexer.getAnalyzer();
    index = indexer.getIndex();
  }

  /**
   * Handles GET requests -- (searching for files in the system)
   * @param req HttpServletRequest -- expects one or more of the following optional query
   *            parameters: "fileName", "fileType", "userName", "userType", "contents"
   * @param resp HttpServletResponse
   * @throws IOException if database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String responseCollector = "";

    String fileNameQuery = req.getParameter("fileName");
    if (fileNameQuery != null) {
      try {
        Query q = new QueryParser("fileName", analyzer).parse(fileNameQuery);
        String result = search(q);
        responseCollector += result;
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String fileTypeQuery = req.getParameter("fileType");
    if (fileTypeQuery != null) {
      try {
        Query q = new QueryParser("fileType", analyzer).parse(fileTypeQuery);
        String result = search(q);
        responseCollector += result;
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String userNameQuery = req.getParameter("userName");
    if (userNameQuery != null) {
      try {
        Query q = new QueryParser("userName", analyzer).parse(userNameQuery);
        String result = search(q);
        responseCollector += result;
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String userTypeQuery = req.getParameter("userType");
    if (userTypeQuery != null) {
      try {
        Query q = new QueryParser("userType", analyzer).parse(userTypeQuery);
        String result = search(q);
        responseCollector += result;
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String userContentQuery = req.getParameter("contents");
    if (userContentQuery != null) {
      try {
        Query q = new QueryParser("contents", analyzer).parse(userContentQuery);
        String result = search(q);
        responseCollector += result;
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String[] responseTokens = responseCollector.split("\n");
    HashSet<String> responseSet = new HashSet<>(responseTokens.length);

    for (String token : responseTokens) {
      responseSet.add(token);
    }

    String responseBackToUser = "";
    for (String response : responseSet) {
      responseBackToUser += response + "\n";
    }

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser);

  }

  /**
   * Search for Query q and return a response to the user containing the requested information
   * @param q query for file in the system
   * @throws IOException if the database return is invalid
   */
  private String search(Query q) throws IOException {
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;
    StringBuilder responseBackToUser = new StringBuilder();

    // result highlighter
    Formatter formatter = new SimpleHTMLFormatter();
    QueryScorer scorer = new QueryScorer(q);
    Highlighter highlighter = new Highlighter(formatter, scorer);
    Fragmenter fragmenter = new SimpleFragmenter(10);
    highlighter.setTextFragmenter(fragmenter);

    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      String contents = d.get("contents");

      String contentResult = "";
      TokenStream stream = TokenSources.getAnyTokenStream(reader, docId, "contents", analyzer);

      try {
        String[] frags = highlighter.getBestFragments(stream, contents, 10);
        for (String frag : frags) {
          contentResult += (frag + "...");
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      contents = contents != null ? contents.substring(0, Math.min(contents.length(), 160)) : "";

      responseBackToUser.append(d.get("fileName") + "~"
          + d.get("fileType") + "~"
          + d.get("userType") + "~"
          + d.get("userName") + "~"
          + d.get("userId") + "~"
          + d.get("fileSize") + "~"
          + d.get("uploadDate") + "~"
          + d.get("fileId") + "~"
          + "\"" + (contentResult.length() > 0 ? contentResult : contents + "...") + "\"\n");
    }
    return responseBackToUser.toString();
  }
}

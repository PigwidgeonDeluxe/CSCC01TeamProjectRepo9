package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * Search for a user
 */

@WebServlet("/usersearch")
public class UserSearch extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static StandardAnalyzer analyzer = null;
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String responseBackToUser = "";

    String userNameQuery = req.getParameter("userName");
    if (userNameQuery != null) {
      try {
        Query q = new QueryParser("userName", analyzer).parse(userNameQuery);
        String result = search(q);
        if (!responseBackToUser.contains(result)) {
          responseBackToUser += result;
        }
      } catch (ParseException | SQLException e) {
        e.printStackTrace();
      }
    }

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser);

  }

  /**
   * Search for Query q and return a response to the user containing the requested information
   * 
   * @param q
   * @throws IOException
   * @throws SQLException 
   */
  private String search(Query q) throws IOException, SQLException {
    Database db = new Database();
    ResultSet allNames = db.getUserByName(q.toString());
    StringBuilder responseBackToUser = new StringBuilder();
    
    while(allNames.next()) {
      responseBackToUser.append(allNames.getString("user_name") + "\n"); // string list using newline character
    }
    return responseBackToUser.toString();
  }
}

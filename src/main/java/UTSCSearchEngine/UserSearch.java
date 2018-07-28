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

@WebServlet("/userSearch")
public class UserSearch extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String userName = req.getParameter("userName");
    Database db = new Database();
    StringBuilder responseBackToUser = new StringBuilder();

    try {
      ResultSet allNames = db.getUserByName(userName);
      while(allNames.next()) {
        responseBackToUser.append(allNames.getString("user_name") + "~"
            + allNames.getString("user_type") + "~"
            + allNames.getString("user_id") + "~"
            + allNames.getString("created_on") + "~"
            + allNames.getString("profile_image") + "\n");
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());

  }
}

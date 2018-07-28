package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Search for a user
 */

@WebServlet("/usersearch")
public class UserSearch extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  private Database db;
  
  public UserSearch() {
    this.db = new Database();
  }
  
  public UserSearch(Database db) {
    this.db = db;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String responseBackToUser = "";

    String userNameQuery = req.getParameter("userName");
    if (userNameQuery != null) {
      try {
        String result = search(userNameQuery);
        if (!responseBackToUser.contains(result)) {
          responseBackToUser += result;
        }
      } catch (SQLException e) {
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
  private String search(String name) throws IOException, SQLException {
    ResultSet allNames = db.getUserByName(name);
    StringBuilder responseBackToUser = new StringBuilder();
    
    while(allNames.next()) {
      responseBackToUser.append(allNames.getString("user_name") + "\n"); // string list using newline character
    }
    return responseBackToUser.toString();
  }
}

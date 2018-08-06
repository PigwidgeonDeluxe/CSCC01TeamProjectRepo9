package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class for searching for users
 */
@WebServlet("/userSearch")
public class UserSearch extends HttpServlet {

  private Database db;

  /**
   * Creates a new UserSearch instance
   */
  public UserSearch() {
    this.db = new Database();
  }

  /**
   * Overloaded method for test purposes
   * @param db test database connection
   */
  public UserSearch(Database db) {
    this.db = db;
  }

  /**
   * Handles GET requests -- (search for given user)
   * @param req HttpServletRequest -- expects query parameter "userName"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String userName = req.getParameter("userName");
    StringBuilder responseBackToUser = new StringBuilder();
    ResultSet allNames = null;

    try {
      // get all like users from the database
      allNames = this.db.getUserByName(userName);
      while(allNames.next()) {
        responseBackToUser.append(allNames.getString("user_name") + "~"
            + allNames.getString("user_type") + "~"
            + allNames.getString("user_id") + "~"
            + allNames.getString("created_on") + "~"
            + allNames.getString("profile_image") + "\n");
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      // close open connections
      if (allNames != null) {
        try {
          allNames.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }

    // package and send request to user
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());

  }
}

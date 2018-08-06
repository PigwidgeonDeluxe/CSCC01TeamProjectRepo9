package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class for handling the files attributed to a given user
 */
@WebServlet("/userFiles")
public class UserFiles extends HttpServlet {

  /**
   * Handles GET requests -- (getting user files)
   * @param req HttpServletRequest -- expects query parameter "userId"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    Database db = new Database();
    String userId = req.getParameter("userId");
    StringBuilder responseBackToUser = new StringBuilder();
    ResultSet rs = null;

    try {
      // returns the files attributed to a given user
      rs = db.getUserFiles(userId);
      while(rs.next()) {
        responseBackToUser.append(rs.getString("file_name") + "~"
            + rs.getString("file_type") + "~"
            + rs.getString("user_type") + "~"
            + rs.getString("user_name") + "~"
            + rs.getString("file_size") + "~"
            + rs.getString("uploaded_on") + "~"
            + rs.getString("id") + "\n");
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      // close open connection
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }

    // package and send response to user
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());
  }

}

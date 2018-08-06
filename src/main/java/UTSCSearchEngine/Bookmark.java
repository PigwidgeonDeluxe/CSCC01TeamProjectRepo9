package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Class for handling the bookmarking files
 */
@WebServlet("/bookmark")
public class Bookmark extends HttpServlet {

  /**
   * Handles GET requests (returning user bookmarks)
   * @param req HttpServletRequest -- expects query parameter "userId"
   * @param resp HttpServletResponse
   * @throws IOException if database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    // get query parameter "userId"
    String userId = req.getParameter("userId");
    StringBuilder responseBackToUser = new StringBuilder();
    ResultSet rs = null;

    try {
      // call database
      rs = db.getBookmarks(userId);
      // collect information for user response string
      while (rs.next()) {
        responseBackToUser.append(rs.getString("file_name") + "~"
          + rs.getString("file_type") + "~"
          + rs.getString("file_size") + "~"
          + rs.getString("user_name") + "~"
          + rs.getString("user_type") + "~"
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

    // set headers and write response to user
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());
  }

  /**
   * Handles POST requests (creating user bookmarks)
   * @param req HttpServletRequest -- expects query parameters "userId" and "fileId"
   * @param resp HttpServletResponse
   * @throws IOException if database return is invalid
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    // get query parameters "userId" and "fileId"
    String userId = req.getParameter("userId");
    String fileId = req.getParameter("fileId");
    JSONObject response = new JSONObject();
    ResultSet rs = null;
    boolean bookmarkedFile = false;

    // set headers
    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    try {
      rs = db.getBookmarks(userId);
      while (rs.next()) {
        // if the file is already bookmarked, remove the bookmark
        if (rs.getString("id").equals(fileId)) {
          rs.close();
          db.unbookmarkFile(fileId, userId);
          response.put("status", "SUCCESS");
          response.put("message", "Successfully unbookmarked file");
          resp.getWriter().write(response.toString());
          bookmarkedFile = true;
        }
      }
      // if the file is not bookmarked, add a bookmark
      if (!bookmarkedFile) {
        db.bookmarkFile(fileId, userId);
        response.put("status", "SUCCESS");
        response.put("message", "Successfully bookmarked file");
        resp.getWriter().write(response.toString());
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
  }
}

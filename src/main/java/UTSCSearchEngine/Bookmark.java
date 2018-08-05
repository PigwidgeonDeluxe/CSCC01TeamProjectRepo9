package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/bookmark")
public class Bookmark extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    StringBuilder responseBackToUser = new StringBuilder();
    ResultSet rs = null;

    try {
      rs = db.getBookmarks(userId);
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
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    String fileId = req.getParameter("fileId");
    JSONObject response = new JSONObject();
    ResultSet rs = null;
    boolean bookmarkedFile = false;

    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    try {
      rs = db.getBookmarks(userId);
      while (rs.next()) {
        if (rs.getString("id").equals(fileId)) {
          rs.close();
          db.unbookmarkFile(fileId, userId);
          response.put("status", "SUCCESS");
          response.put("message", "Successfully unbookmarked file");
          resp.getWriter().write(response.toString());
          bookmarkedFile = true;
        }
      }
      if (!bookmarkedFile) {
        db.bookmarkFile(fileId, userId);
        response.put("status", "SUCCESS");
        response.put("message", "Successfully bookmarked file");
        resp.getWriter().write(response.toString());
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
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

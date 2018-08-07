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
 * Class for handling the commenting of files
 *
 */
@WebServlet("/comment")
public class Comment extends HttpServlet {
  
  private Database db;
  public Comment() {
    this.db = new Database();
  }
  public Comment(Database db) {
    this.db = db;
  }

  /**
   * Handles GET requests (returning user comments)
   * @param req HttpServletRequest -- expects query parameter "docId"
   * @param resp HttpServletResponse
   * @throws IOException if database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    // get query parameter "docId"
    String docId = req.getParameter("docId");
    StringBuilder responseBackToUser = new StringBuilder();

    JSONObject response = new JSONObject();
    ResultSet rs = null;

    // package file data
    try {
      rs = db.getFileById(docId);
      while (rs.next()) {
        response.put("fileName", rs.getString("file_name"));
        response.put("fileType", rs.getString("file_type"));
        response.put("fileSize", rs.getString("file_size"));
        response.put("uploadedOn", rs.getString("uploaded_on"));
        response.put("uploaderName", rs.getString("user_name"));
        response.put("uploaderType", rs.getString("user_type"));
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

    ResultSet comments = null;

    // package comment data
    try {
      comments = db.getFileComments(docId);
      while (comments.next()) {
        responseBackToUser.append(comments.getString("file_id") + "~"
            + comments.getString("comment") + "~"
            + comments.getString("user_name") + "~"
            + comments.getString("user_type") + "~"
            + comments.getString("user_id") + "~"
            + comments.getString("profile_image") + "~"
            + comments.getString("date")+ "\n");
      }
      response.put("comments", responseBackToUser.toString());
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // close open connection
      if (comments != null) {
        try {
          comments.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }

    // write response back to user
    resp.getWriter().write(response.toString());
  }

  /**
   * Handles POST requests (commenting on file)
   * @param req HttpServletRequest -- expects query parameters "docId", "comment", and "commentUser"
   * @param resp HttpServletResponse
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    // get query parameters "docId", "comment", and "commentUser"
    String docId = req.getParameter("docId");
    String comment = req.getParameter("comment");
    String commentUser = req.getParameter("commentUser");
    Long date = System.currentTimeMillis();

    // add comment to database
    db.insertFileComment(docId, comment, commentUser, date);
  }
}

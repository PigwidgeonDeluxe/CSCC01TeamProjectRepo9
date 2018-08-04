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
 * Class for retrieving all comments for a given docID
 *
 */
@WebServlet("/comment")
public class Comment extends HttpServlet {
  
  private static final long serialVersionUID = 1L;

  private Database db;
  public Comment() {
    this.db = new Database();
  }
  public Comment(Database db) {
    this.db = db;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

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
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }

    ResultSet comments = null;

    // package comments
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

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    String docId = req.getParameter("docId");
    String comment = req.getParameter("comment");
    String commentUser = req.getParameter("commentUser");
    Long date = System.currentTimeMillis(); // current time (system)

    db.insertFileComment(docId, comment, commentUser, date);
  }
}

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

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    String docId = req.getParameter("docId");
    StringBuilder responseBackToUser = new StringBuilder();

    Database db = new Database();
    JSONObject response = new JSONObject();

    // package file data
    try {
      ResultSet rs = db.getFileById(docId);
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
    }

    // package comments
    try {
      ResultSet comments = db.getFileComments(docId);
      while (comments.next()) {
        responseBackToUser.append(comments.getString("file_id") + "~"
            + comments.getString("comment") + "~"
            + comments.getString("user_name") + "~"
            + comments.getString("user_type") + "~"
            + comments.getString("profile_image") + "~"
            + comments.getString("date")+ "\n");
      }
      response.put("comments", responseBackToUser.toString());
    } catch (SQLException e) {
      e.printStackTrace();
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

    Database db = new Database();
    db.insertFileComment(docId, comment, commentUser, date);
  }
}

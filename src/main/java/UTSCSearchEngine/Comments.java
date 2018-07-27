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
@WebServlet("/comments")
public class Comments extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    JSONObject response = new JSONObject();
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    String docId = req.getParameter("docId");
    StringBuilder responseBackToUser = new StringBuilder();

    Database db = new Database();

    // get all comments for a given document ID
    try {
      ResultSet comments = db.getFileComments(docId);
      while (comments.next()) {
        responseBackToUser.append(comments.getString("file_id") + "~"
            + comments.getString("comment") + "~"
            + comments.getString("comment_user") + "~"
            + comments.getString("date") + "\"\n");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // write response back to user
    resp.getWriter().write(responseBackToUser.toString());

  }
}

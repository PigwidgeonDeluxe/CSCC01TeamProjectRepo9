package UTSCSearchEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/comments")
public class Commenting extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    JSONObject response = new JSONObject();
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    String docId = req.getParameter("docId");
    StringBuilder responseBackToUser = new StringBuilder();

    Database db = new Database();

    try {
      ResultSet comments = db.getFileComments(docId);
      while (comments.next()) {
        responseBackToUser.append(comments.getString("file_id") + "~"
            + comments.getString("comment") + "~"
            + comments.getString("commenter") + "~"
            + comments.getString("comment_user") + "~"
            + comments.getString("date") + "\"\n");
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }
}

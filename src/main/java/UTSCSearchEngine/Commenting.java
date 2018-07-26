package UTSCSearchEngine;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Class for handling comment submissions. Takes in docId, comment, and comment_users
 *
 */
@WebServlet("/commenting")
public class Commenting extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    JSONObject response = new JSONObject();
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    String docId = req.getParameter("docId");
    String comment = req.getParameter("comment");
    String comment_user = req.getParameter("comment_user");
    Long date = System.currentTimeMillis(); // current time (system)

    Database db = new Database();

    db.insertFileComment(docId, comment, comment_user, date);
  }
}

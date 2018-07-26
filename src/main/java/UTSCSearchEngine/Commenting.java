package UTSCSearchEngine;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Class for handling comment submissions
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

  }
}

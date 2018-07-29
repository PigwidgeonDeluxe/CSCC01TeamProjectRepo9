package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/follow")
public class Follow extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    StringBuilder responseBackToUser = new StringBuilder();

    try {
      ResultSet rs = db.getFollowing(userId);
      while (rs.next()) {
        responseBackToUser.append(rs.getString("user_id") + "~"
          + rs.getString("user_name") + "~"
          + rs.getString("user_type") + "~"
          + rs.getString("profile_image") + "~"
          + rs.getString("created_on") + "\n");
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    String followUserId = req.getParameter("followUserId");
    JSONObject response = new JSONObject();

    boolean followingUser = false;

    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    try {
      ResultSet rs = db.getFollowing(userId);
      while (rs.next()) {
        if (rs.getString("user_id").equals(userId) &&
            rs.getString("following_user_id").equals(followUserId)) {
          db.unfollowUser(userId, followUserId);
          response.put("status", "SUCCESS");
          response.put("message", "Successfully unfollowed user");
          resp.getWriter().write(response.toString());
          followingUser = true;
        }
      }

      if (!followingUser) {
        db.followUser(userId, followUserId);
        response.put("status", "SUCCESS");
        response.put("message", "Successfully following user");
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}

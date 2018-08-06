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
 * Class for handling user follows
 */
@WebServlet("/follow")
public class Follow extends HttpServlet {

  /**
   * Handles GET requests -- (getting users a given user is following)
   * @param req HttpServletRequest -- expects query parameter "userId"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    StringBuilder responseBackToUser = new StringBuilder();
    ResultSet rs = null;

    try {
      // get the user information of the following users
      rs = db.getFollowing(userId);
      while (rs.next()) {
        responseBackToUser.append(rs.getString("user_id") + "~"
          + rs.getString("user_name") + "~"
          + rs.getString("user_type") + "~"
          + rs.getString("profile_image") + "~"
          + rs.getString("created_on") + "\n");
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

    // send response back to user
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseBackToUser.toString());
  }

  /**
   * Handles POST requests -- (following another user)
   * @param req HttpServletRequest -- expects query parameters "userId" and "followUserId"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    String followUserId = req.getParameter("followUserId");
    JSONObject response = new JSONObject();
    ResultSet rs = null;
    boolean followingUser = false;

    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    try {
      rs = db.getFollowing(userId);
      while (rs.next()) {
        // if the user is already being followed, unfollow that user
        if (rs.getString("following_user_id").equals(followUserId)) {
          rs.close();
          db.unfollowUser(userId, followUserId);
          response.put("status", "SUCCESS");
          response.put("message", "Successfully unfollowed user");
          resp.getWriter().write(response.toString());
          followingUser = true;
        }
      }
      // if the user is not being followed, follow that user
      if (!followingUser) {
        db.followUser(userId, followUserId);
        response.put("status", "SUCCESS");
        response.put("message", "Successfully following user");
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

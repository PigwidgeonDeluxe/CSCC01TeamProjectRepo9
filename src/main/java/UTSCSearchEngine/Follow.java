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
 * Class for Retrieving Followed User Updates
 *
 */
@WebServlet("/follow")
public class Follow extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("multipart/form-data");
		resp.setHeader("Access-Control-Allow-Origin", "*");

		String userId = req.getParameter("userId");
		StringBuilder responseBackToUser = new StringBuilder();

		Database db = new Database();
		JSONObject response = new JSONObject();

		// package user follower data
		try {
			ResultSet user = db.getUserFollow(userId);
			while (user.next()) {
				String userIdFollow = user.getString("follow_id");
				ResultSet userFollow = db.getUserById(userIdFollow);
				responseBackToUser
						.append(userFollow.getString("user_id") + "~" + userFollow.getString("update_file_id") + "\n");
				userFollow.close();
			}
			user.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		// write response back to user
		resp.getWriter().write(response.toString());
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("multipart/form-data");
		resp.setHeader("Access-Control-Allow-Origin", "*");

		String userId = req.getParameter("userId");
		String userIdFollow = req.getParameter("userIdFollow");

		Database db = new Database();

		int followNum = 0;
		try {
			ResultSet user = db.getUserById(userId);
			while (user.next()) {
				followNum = user.getInt("follow_num");
			}
			user.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		db.insertUserFollow(userId, userIdFollow);
		db.updateUserFollowNum(userId, followNum);
	}
}

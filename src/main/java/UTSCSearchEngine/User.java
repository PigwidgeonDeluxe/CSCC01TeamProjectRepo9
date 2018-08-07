package UTSCSearchEngine;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import org.json.JSONObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Class for handling user creation and login
 */
@WebServlet("/user")
public class User extends HttpServlet {

  /**
   * Processes user request as either a login or user creation
   * @param req HttpServletRequest -- accepts one of optional query parameter "create" or "login"
   * @param resp HttpServletResponse
   * @param postBody body of POST request; expects POST body containing JSON with object parameters
   *                 "token", "userName", "userType", and "profileImage"
   */
  private void processRequest(HttpServletRequest req, HttpServletResponse resp, String postBody) {
    // setup google id token verifier
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
        new JacksonFactory())
        .setAudience(Collections.singletonList("262727309060-6bg972cbt6p1k27318l7ubk8saflsski.apps.googleusercontent.com"))
        .build();

    Database db = new Database();
    JSONObject response = new JSONObject();

    // handle user creation request
    String createUserQuery = req.getParameter("create");
    if (createUserQuery != null) {
      JSONObject json = new JSONObject(postBody);

      try {
        // verify the token is valid
        GoogleIdToken idToken = verifier.verify(json.getString("token"));
        if (idToken != null) {
          Payload payload = idToken.getPayload();
          String userId = payload.getSubject();
          ResultSet rs = null;

          try {
            rs = db.getUserById(userId);
            if (rs.next()) {
              // user already exists; return a failure structure to the frontend
              response.put("status", "FAILURE");
              response.put("message", "User already exists");
              resp.getWriter().write(response.toString());
            } else {
              // user doesn't exist; create a new user
              db.insertUser(userId, json.getString("userType"),
                  json.getString("userName"), json.getString("profileImage"));
              response.put("status", "SUCCESS");
              response.put("message", "Successfully created new " + json.getString("userType"));
              response.put("createdOn", System.currentTimeMillis());
              resp.getWriter().write(response.toString());
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
        } else {
          // token is invalid; return a failure structure to the frontend
          response.put("status", "FAILURE");
          response.put("message", "Unable to create new user");
          resp.getWriter().write(response.toString());
        }
      } catch (GeneralSecurityException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    // handle user login request
    String loginUserQuery = req.getParameter("login");
    if (loginUserQuery != null) {
      JSONObject json = new JSONObject(postBody);

      try {
        // verify token
        GoogleIdToken idToken = verifier.verify(json.getString("token"));
        if (idToken != null) {
          Payload payload = idToken.getPayload();
          String userId = payload.getSubject();
          ResultSet rs = null;

          try {
            rs = db.getUserById(userId);
            if (rs.next()) {
              // user exists; login the user
              response.put("status", "SUCCESS");
              response.put("userType", rs.getString("user_type"));
              response.put("createdOn", rs.getString("created_on"));
              response.put("message", "Successfully logged in");
              resp.getWriter().write(response.toString());
            } else {
              // user doesn't exist; send a failure structure to the frontend
              response.put("status", "FAILURE");
              response.put("message", "User does not exist");
              resp.getWriter().write(response.toString());
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
        } else {
          // the token is invaild; send a failure structure to the frontend
          response.put("status", "FAILURE");
          response.put("message", "Unable to login user");
          resp.getWriter().write(response.toString());
        }
      } catch (GeneralSecurityException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Handles GET requests -- (getting user information)
   * @param req HttpServletRequest -- expects query parameter "userId"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    JSONObject response = new JSONObject();
    ResultSet rs = null;

    // get user information from database
    try {
      rs = db.getUserById(userId);
      while (rs.next()) {
        response.put("userId", rs.getString("user_id"));
        response.put("userName", rs.getString("user_name"));
        response.put("userType", rs.getString("user_type"));
        response.put("profileImage", rs.getString("profile_image"));
        response.put("createdOn", rs.getString("created_on"));
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

    // package and send request to user
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(response.toString());
  }

  /**
   * Handles and sends POST requests; passes request and response headers to processRequest
   * @param req HttpServletRequest
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String postBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    processRequest(req, resp, postBody);
  }
}

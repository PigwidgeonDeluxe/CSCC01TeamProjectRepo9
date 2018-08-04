package UTSCSearchEngine;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@WebServlet("/user")
public class User extends HttpServlet {

  private void processRequest(HttpServletRequest req, HttpServletResponse resp, String postBody) {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
        new JacksonFactory())
        .setAudience(Collections.singletonList("262727309060-6bg972cbt6p1k27318l7ubk8saflsski.apps.googleusercontent.com"))
        .build();

    Database db = new Database();
    JSONObject response = new JSONObject();

    String createUserQuery = req.getParameter("create");
    if (createUserQuery != null) {
      JSONObject json = new JSONObject(postBody);

      try {
        GoogleIdToken idToken = verifier.verify(json.getString("token"));
        if (idToken != null) {
          Payload payload = idToken.getPayload();
          String userId = payload.getSubject();
          ResultSet rs = null;

          try {
            rs = db.getUserById(userId);
            if (rs.next()) {
              // user already exists
              response.put("status", "FAILURE");
              response.put("message", "User already exists");
              resp.getWriter().write(response.toString());
            } else {
              // user doesn't exist
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
            if (rs != null) {
              try {
                rs.close();
              } catch (SQLException ex) {
                ex.printStackTrace();
              }
            }
          }
        } else {
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

    String loginUserQuery = req.getParameter("login");
    if (loginUserQuery != null) {
      JSONObject json = new JSONObject(postBody);

      try {
        GoogleIdToken idToken = verifier.verify(json.getString("token"));
        if (idToken != null) {
          Payload payload = idToken.getPayload();
          String userId = payload.getSubject();
          ResultSet rs = null;

          try {
            rs = db.getUserById(userId);
            if (rs.next()) {
              // user exists
              response.put("status", "SUCCESS");
              response.put("userType", rs.getString("user_type"));
              response.put("createdOn", rs.getString("created_on"));
              response.put("message", "Successfully logged in");
              resp.getWriter().write(response.toString());
            } else {
              // user doesn't exist
              response.put("status", "FAILURE");
              response.put("message", "User does not exist");
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
        } else {
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

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Database db = new Database();
    String userId = req.getParameter("userId");
    JSONObject response = new JSONObject();
    ResultSet rs = null;

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

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(response.toString());
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String postBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    processRequest(req, resp, postBody);
  }
}

package UTSCSearchEngine;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.security.GeneralSecurityException;
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

  private static String docsPath = "./src/main/resources/";

  public void setDocsPath(String docsPath) {
    this.docsPath = docsPath;
  }

  private void processRequest(HttpServletRequest req, HttpServletResponse resp, String postBody) {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
        new JacksonFactory())
        .setAudience(Collections.singletonList("262727309060-6bg972cbt6p1k27318l7ubk8saflsski.apps.googleusercontent.com"))
        .build();
    JSONObject response = new JSONObject();

    String createUserQuery = req.getParameter("create");
    if (createUserQuery != null) {
      JSONObject json = new JSONObject(postBody);
      Path path = Paths.get(docsPath + "users.csv");

      try {
        GoogleIdToken idToken = verifier.verify(json.getString("token"));
        if (idToken != null) {
          Payload payload = idToken.getPayload();
          boolean userExists = false;

          try {
            Scanner scanner = new Scanner(new File(docsPath + "users.csv"));
            while (scanner.hasNext()) {
              String[] user = scanner.next().split(",");
              String userId = payload.getSubject();
              if (user[0].equals(userId)) {
                userExists = true;
              }
            }
          } catch (FileNotFoundException ex) {
            ex.printStackTrace();
          }

          // package client response
          if (!userExists) {
            response.put("status", "SUCCESS");
            response.put("message", "Successfully created new " + json.getString("userType"));

            // save user data
            String userId = payload.getSubject();
            List<String> lines = Arrays.asList(userId + "," + json.getString("userType"));
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            resp.getWriter().write(response.toString());
          } else {
            response.put("status", "FAILURE");
            response.put("message", "User already exists");
            resp.getWriter().write(response.toString());
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
          boolean userExists = false;

          // get client data
          try {
            Scanner scanner = new Scanner(new File(docsPath + "users.csv"));

            while (scanner.hasNext()) {
              String[] user = scanner.next().split(",");
              String userId = payload.getSubject();
              if (user[0].equals(userId)) {
                response.put("userType", user[1]);
                userExists = true;
              }
            }
          } catch (FileNotFoundException ex) {
            ex.printStackTrace();
          }

          // package client response
          if (userExists) {
            response.put("status", "SUCCESS");
            response.put("message", "Successfully logged in");
            resp.getWriter().write(response.toString());
          } else {
            response.put("status", "FAILURE");
            response.put("message", "User does not exist");
            resp.getWriter().write(response.toString());
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
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/x-www-form-urlencoded");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String postBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    processRequest(req, resp, postBody);
  }
}

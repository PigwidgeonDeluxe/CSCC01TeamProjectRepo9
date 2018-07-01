package UTSCSearchEngine;

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        String postBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject json = new JSONObject(postBody);

        String createUserQuery = req.getParameter("create");
        if (createUserQuery != null) {
            List<String> lines = Arrays.asList(json.getString("userName") + "," +
                    json.getString("userType") + "," + json.getString("password"));
            Path path = Paths.get(docsPath + "users.csv");
            File file = new File(path.toString());
            boolean userExists = false;

            if (file.exists()) {
                try {
                    Scanner scanner = new Scanner(file);
                    String username = json.getString("userName");
                    while(scanner.hasNext()) {
                        String[] user = scanner.next().split(",");
                        if (username.equals(user[0])) {
                            resp.getWriter().write("User: " + username + " already exists");
                            resp.setStatus(400);
                            userExists = true;
                        }
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (!userExists) {
                Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.getWriter().write("Successfully created new user");
            }

        }

        String loginUserQuery = req.getParameter("login");
        if (loginUserQuery != null) {
            String username = json.getString("userName");
            String password = json.getString("password");
            resp.setHeader("Access-Control-Allow-Origin", "*");

            try {
                Scanner scanner = new Scanner(new File(docsPath + "users.csv"));
                boolean login = false;
                while (scanner.hasNext()) {
                    String[] user = scanner.next().split(",");
                    if (username.equals(user[0])) {
                        if (password.equals(user[2])) {
                            resp.getWriter().write("Successfully logged in user: " + username);
                            login = true;
                        } else {
                            resp.getWriter().write("Incorrect password for user: " + username);
                            resp.setStatus(400);
                            login = true;
                        }
                    }
                }

                if (!login) {
                    resp.getWriter().write("Username: " + username + " not found");
                    resp.setStatus(400);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
}

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
 * Class for handling statistics
 */
@WebServlet("/statistics")
public class Statistics extends HttpServlet {

  /**
   * Handles GET requests -- (getting user or general statistics)
   * @param req HttpServletRequest -- can use optional query parameter "userName"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    Database db = new Database();
    JSONObject responseJSON = new JSONObject();

    // general statistics
    JSONObject fileTypeStats = new JSONObject();
    JSONObject fileUploaderStats = new JSONObject();
    JSONObject fileSizeStats = new JSONObject();

    // user statistics
    JSONObject userFileTypeStats = new JSONObject();
    JSONObject userFileSizeStats = new JSONObject();

    String userName = req.getParameter("userName");

    if (userName != null) {
      // handle getting general system-wide statistics
      try {
        ResultSet fileTypeRs = db.getUserFileTypeStatistics(userName);
        while(fileTypeRs.next()) {
          userFileTypeStats.put(fileTypeRs.getString("file_type"),
              fileTypeRs.getInt("count(file_type)"));
        }
        fileTypeRs.close();

        ResultSet fileSizeRs = db.getUserFileSizeStatistics(userName);
        while(fileSizeRs.next()) {
          userFileSizeStats.put(fileSizeRs.getString("file_name"),
              fileSizeRs.getInt("file_size"));
        }
        fileSizeRs.close();

      } catch (SQLException ex) {
        ex.printStackTrace();
      }

      // package statistics in response JSON
      responseJSON.put("fileType", userFileTypeStats);
      responseJSON.put("fileSize", userFileSizeStats);

      resp.setHeader("Access-Control-Allow-Origin", "*");
      resp.getWriter().write(responseJSON.toString());
    } else {
        // handle getting user statistics
        try {
          ResultSet fileTypeRs = db.getFileTypeStatistics();
          while(fileTypeRs.next()) {
            fileTypeStats.put(fileTypeRs.getString("file_type"),
                fileTypeRs.getInt("count(file_type)"));
          }
          fileTypeRs.close();

          ResultSet fileUploaderRs = db.getFileUploaderStatistics();
          while(fileUploaderRs.next()) {
            fileUploaderStats.put(fileUploaderRs.getString("user_name"),
                fileUploaderRs.getInt("count(user_name)"));
          }
          fileUploaderRs.close();

          ResultSet fileSizeRs = db.getFileSizeStatistics();
          while(fileSizeRs.next()) {
            fileSizeStats.put(fileSizeRs.getString("file_name"),
                fileSizeRs.getInt("file_size"));
          }
          fileSizeRs.close();

        } catch (SQLException ex) {
          ex.printStackTrace();
        }

        // package statistics in response JSON
        responseJSON.put("fileType", fileTypeStats);
        responseJSON.put("uploaderStats", fileUploaderStats);
        responseJSON.put("fileSize", fileSizeStats);

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().write(responseJSON.toString());

      }
    }

}

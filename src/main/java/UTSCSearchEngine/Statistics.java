package UTSCSearchEngine;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/statistics")
public class Statistics extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    Database db = new Database();
    JSONObject responseJSON = new JSONObject();

    JSONObject fileTypeStats = new JSONObject();
    JSONObject fileUploaderStats = new JSONObject();
    JSONObject fileSizeStats = new JSONObject();

    try {
      ResultSet fileTypeRs = db.getFileTypeStatistics();
      while(fileTypeRs.next()) {
        fileTypeStats.put(fileTypeRs.getString("file_type"),
            fileTypeRs.getInt("count(file_type)"));
      }

      ResultSet fileUploaderRs = db.getFileUploaderStatistics();
      while(fileUploaderRs.next()) {
        fileUploaderStats.put(fileUploaderRs.getString("uploader_name"),
            fileUploaderRs.getInt("count(uploader_name)"));
      }

      ResultSet fileSizeRs = db.getFileSizeStatistics();
      while(fileSizeRs.next()) {
        fileSizeStats.put(fileSizeRs.getString("file_name"),
            fileSizeRs.getInt("file_size"));
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    // package statistics in response JSON
    responseJSON.put("fileType", fileTypeStats.toString());
    responseJSON.put("uploaderStats", fileUploaderStats.toString());
    responseJSON.put("fileSize", fileSizeStats.toString());

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.getWriter().write(responseJSON.toString());

  }
}

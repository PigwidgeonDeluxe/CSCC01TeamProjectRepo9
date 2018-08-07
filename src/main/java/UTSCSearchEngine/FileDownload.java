package UTSCSearchEngine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class for handling file download of files in the database
 */
@WebServlet("/download")
public class FileDownload extends HttpServlet {

  // byte limit for byte array buffer
  private final int BUFFER_LIMIT = 4096;

  /**
   * Handles GET requests (downloading a file)
   * @param req HttpServletRequest -- expects query parameters "fileName" and "uploadTime"
   * @param resp HttpServletResponse
   * @throws IOException if the database return is invalid
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String fileName = req.getParameter("fileName");
    String uploadTime = req.getParameter("uploadTime");
    resp.setHeader("Content-disposition", "attachment; filename=" + fileName);

    Database db = new Database();
    ResultSet rs = null;

    try {
      // get data from database
      rs = db.getFileData(fileName, Long.parseLong(uploadTime));
      if (rs.next()) {
        // package file data into byte array
        OutputStream out = resp.getOutputStream();
        InputStream in = new ByteArrayInputStream(rs.getBytes("file"));
        byte[] buffer = new byte[BUFFER_LIMIT];
        int length;
        while ((length = in.read(buffer)) > 0) {
          out.write(buffer, 0, length);
        }
        // send file data to response stream
        in.close();
        out.flush();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      // close open connections
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

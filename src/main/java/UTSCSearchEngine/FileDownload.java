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

@WebServlet("/download")
public class FileDownload extends HttpServlet {

  private final int BUFFER_LIMIT = 4096;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String fileName = req.getParameter("fileName");
    String uploadTime = req.getParameter("uploadTime");
    resp.setHeader("Content-disposition", "attachment; filename=" + fileName);

    Database db = new Database();
    try {
      ResultSet rs = db.getFileData(fileName, Long.parseLong(uploadTime));
      if (rs.next()) {

        OutputStream out = resp.getOutputStream();
        InputStream in = new ByteArrayInputStream(rs.getBytes("file"));
        byte[] buffer = new byte[BUFFER_LIMIT];
        int length;
        while ((length = in.read(buffer)) > 0) {
          out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}

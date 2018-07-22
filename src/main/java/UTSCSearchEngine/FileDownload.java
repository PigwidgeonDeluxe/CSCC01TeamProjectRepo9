package UTSCSearchEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/download")
public class FileDownload extends HttpServlet {

  private String docsPath = "./src/main/resources/";
  private final int BUFFER_LIMIT = 4096;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String fileName = req.getParameter("fileName");
    resp.setHeader("Content-disposition", "attachment; filename=" + fileName);

    File file = new File(docsPath + fileName);

    OutputStream out = resp.getOutputStream();
    FileInputStream in = new FileInputStream(file);
    byte[] buffer = new byte[BUFFER_LIMIT];
    int length;
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
    }
    in.close();
    out.flush();
  }
}

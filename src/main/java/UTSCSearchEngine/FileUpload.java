package UTSCSearchEngine;

import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

@WebServlet("/upload")
public class FileUpload extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private Indexing indexer = new Indexing();

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    JSONObject response = new JSONObject();
    Database db = new Database();
    resp.setContentType("multipart/form-data");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    String uploaderName = req.getParameter("userName");
    String uploaderType = req.getParameter("userType");

    try {
      // parse files
      ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
      List<FileItem> multiFiles = sf.parseRequest(req);

      // save file data
      for (FileItem item : multiFiles) {
        if (!item.isFormField()) {
          db.insertFileData(item.get(),
              item.getName(),
              item.getName().substring(item.getName().lastIndexOf('.') + 1),
              uploaderName,
              uploaderType);
        }
      }

      // package client response
      response.put("status", "SUCCESS");
      response.put("message", "Successfully uploaded file(s)");
      resp.getWriter().write(response.toString());
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // reindex
    indexer.doIndexing();
  }
}

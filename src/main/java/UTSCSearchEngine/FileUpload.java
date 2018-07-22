package UTSCSearchEngine;

import java.util.List;
import java.io.File;
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
	
	// implement into database at later point
	private String docsPath = "./src/main/resources/"; // default path
	// to call indexer
	private static Indexing indexer = new Indexing();

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		/**
		 * makes the request object into
		 * a file object to upload
		 */
    response.setContentType("multipart/form-data");
    response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
			List<FileItem> multifiles = sf.parseRequest(request);
			JSONObject resp = new JSONObject();
			resp.put("status", "SUCCESS");
			resp.put("message", "Successfully uploaded files");
			response.getWriter().write(resp.toString());
			upload(multifiles);
		} catch (Exception e){
			System.out.println(e);
		}
		// call indexer for every uploaded file
		indexer.doIndexing();
		Search.refreshIndexer();
	}
	
	public void upload (List<FileItem> multifiles) throws Exception{
		for (FileItem item : multifiles) {
			item.write(new File (docsPath + item.getName()));
		}
	}
	
	public void setDocsPath(String docsPath) {
		this.docsPath = docsPath;
	}
	
	public String getDocsPath() {
		return this.docsPath;
	}
}

package FileUploader

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.httop.HttpServlet;
import javax.servlet.httop.HttpServletRequest;
import javax.servlet.httop.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class fileUpload extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			// request object has all data
			ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
			// to send multiple files
			List<FileItem> multifiles = sf.parseRequest(request);
			
			for(FileItem item : multifiles) {
				item.write(new File("/cmshome/laiedgar/eclipse-workspace/FileUploader/file_DIR/" + item.getName()));
			}
			
			System.out.println("file uploaded");
			
		} catch (Exception e) {
			System.out.println("unsucessful upload");
		}
		
	}
}

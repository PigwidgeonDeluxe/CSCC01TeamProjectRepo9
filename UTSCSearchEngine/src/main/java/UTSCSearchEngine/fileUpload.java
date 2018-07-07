package UTSCSearchEngine;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;

@WebServlet ("/upload")
// @MultipartConfig
public class fileUpload extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// implement into database at later point
	private static String docsPath = "./src/main/resources/"; // default path
	// to call indexer
	private static Indexing indexer = new Indexing();	
	  
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		/**
		 * upload files to database/file
		 */
		try {
			ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
			List<FileItem> multifiles = sf.parseRequest(request);
			for (FileItem item : multifiles) {
				item.write(new File (docsPath + item.getName()));
				// call indexer for every uploaded file
				indexer.doIndexing();
			}
		} catch (Exception e){
			System.out.println(e);
		}
	}
}

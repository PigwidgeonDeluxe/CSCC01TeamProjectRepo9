package FileUploader

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.httop.HttpServlet;
import javax.servlet.httop.HttpServletRequest;
import javax.servlet.httop.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet (name = "fileUpload", urlPatterns ={"/upload"})
@MultipartConfig
public class fileUpload extends HttpServlet {
	// class variable to hold canonical (standard) name of class
	// for trouble shooting
	private final static Logger LOGGER = Logger.getLogger(fileUpload.class.getCanonicalName());
	
	
	protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
		throws ServletException, IOException {
		 /* upload request to certain directory (on server or on instance of app)
		 * recieves any kind of information and uploads to dir
		 * 		- later should restrict to only docs or any user reqs
		 * 
		 * parameter info (brief summary from API)
		 * request is client request information to servlet
		 * 		provides name, values, attributes, and input stream
		 * response assists in sending response to client
		 * 		to send binary data, use ServletOutputStream returned by getOutputStream()
		 * 		send character data with PrintWriter object (getWriter())
		 */
		
		// response opject to client
		response.setContentType("text/html;charset=UTF-8");
		// Create path components to save file
		final String path = "C:\#temp"
		// request.getParameter("destination") // which directory file will be uploaded to
		final Part filePart = request.getPart("file"); // 
		final String fileName = getFileName(filePart);
		
		// Byte Streams calls
		OutputStream out = null; // to write data to destination
		InputStream filecontent = null; // to read data from source
		final PrintWriter writer = response.getWriter(); // 
		
		try {
			// build path to where file will be uploaded to
			out = new FileOutputStream(new File(path + File.separator + fileName));
			filecontent = filePart.getInputStream(); // object to get file contents
			
			int read = 0; // counter for how much to read
			final byte[] bytes = new byte [1024]; // maximum bytes to read
			
			// upload file to output
			while((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			// extra print (print then terminates line)
			writer.println("New file" + fileName +" created at " + path);
			// for reporting errors
			LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", new Object[], {fileName, path});
		// return error if file to non permissioned location
		} catch (FileNotFoundException fne) {
			writer.println("file not specified or are trying to upload file to protected or nonexistent location.");
			writer.println("<br/> ERROR: " + fne.getMessage());
			LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{fne.getMessage()});
		} finally {
			// close streams
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
			if (writer != null) {
				writer.close();
			}	
		}
		// first attempt code to be put in try
		/*// request object has all data
		ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
		// to send multiple files
		List<FileItem> multifiles = sf.parseRequest(request);
		
		// writing to dir(database) which will store documents
		for(FileItem item : multifiles) {
			item.write(new File("C:\Users\edgar_000\Documents\CSCC01 files\eclipse-workspace\temp\" + item.getName()));
		}
		// will need to index every file with indexer
		
		System.out.println("file uploaded");*/
	}
}

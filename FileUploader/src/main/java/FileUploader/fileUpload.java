package FileUploader;

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
import javax.servlet.http.Part;

@WebServlet ("/upload")
// @MultipartConfig
public class fileUpload extends HttpServlet {
	/**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;
	// class variable to hold canonical (standard) name of class
	// for trouble shooting
	private final static Logger LOGGER = Logger.getLogger(fileUpload.class.getCanonicalName());
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		 /* upload request to certain directory (on server or on instance of app)
		 * Receives any kind of information and uploads to dir
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
		// need to make it so that it uploads to project file resource directory
		final String path = "C:/#temp";
		// request.getParameter("destination") // which directory file will be uploaded to
		final Part filePart = request.getPart("file"); // 
		final String fileName = getFileName(filePart);
		
		// Byte Streams calls
		OutputStream out = null; // to write data to destination
		InputStream filecontent = null; // to read data from source
		final PrintWriter writer = response.getWriter(); // write logs
		
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
			LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", new Object[] {fileName, path});
			
			// add indexer call
			
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
	}
	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("file");
	    LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
	    for (String content : part.getHeader("file").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
}

package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;

import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import UTSCSearchEngine.FileUpload;

public class FileUploadTest extends Mockito{

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void UploadFileTest() throws IOException {
		
		// mock upload files and upload destination	
		File testFile = folder.newFile ("testFile.txt");
		File testFolder = folder.newFolder ("destination");
		// ??? is what boundary is set as
		String fileContents = "contents of testing file???";
		// to test if file exists
		File expect = new File(testFolder.toString() + "testFile.txt");
		// allow access to read and write
		testFile.setWritable(true);
		testFile.setReadable(true);
		testFolder.setWritable(true);
		testFolder.setReadable(true);
		// write some contents of testFile
		Writer writer = new FileWriter(testFile.toString());
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(fileContents);
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();
		
		// mock input stream for request
		InputStream in = new ByteArrayInputStream (fileContents.getBytes());
		ServletInputStream servletInputStream=mock(ServletInputStream.class);
		// mock objects
		HttpServletResponse res = mock(HttpServletResponse.class);
		// HttpServletRequest req = mock(HttpServletRequest.class);
		MockMultipartHttpServletRequest req = mock(MockMultipartHttpServletRequest.class);
		MultipartFile contents = new MockMultipartFile(testFile.getName(),testFile.getName(),"text/*", in);
		req.addFile(contents);
		req.setContent(createFileContent(fileContents.getBytes(),"???","text/*",testFile.getName()));
		req.setMethod("POST");
		// mock request behaviour
		when(req.getContentType()).thenReturn("multipart/form-data; boundary=???");
		when(req.getContentLength()).thenReturn(in.available());
		when(req.getCharacterEncoding()).thenReturn("UTF-8");
		when(req.getInputStream()).thenReturn(servletInputStream);
		when(servletInputStream.read()).thenReturn(in.read());
		
		// set upload destination in class
		FileUpload testUpload = new FileUpload();
		testUpload.setDocsPath(testFolder.toString());
		
		// test upload document with method
		testUpload.doPost((HttpServletRequest)req, res);
		// close input stream
		in.close();
		// check if file exist in test folder
		assertEquals("unsuccessful upload", true, expect.exists());
	}
	
    public byte[] createFileContent(byte[] data, String boundary, String contentType, String fileName){
        String start = "--" + boundary + "\r\n Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\"\r\n"
                 + "Content-type: "+contentType+"\r\n\r\n";;

        String end = "\r\n--" + boundary + "--"; // correction suggested @butfly 
        return ArrayUtils.addAll(start.getBytes(),ArrayUtils.addAll(data,end.getBytes()));
    }
}
package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import UTSCSearchEngine.FileUpload;

public class FileUploadTest  {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void UploadFileTest() throws IOException {	
		String outcome = "upload to be done"; //default to fail
		
		// mock upload files and upload destination	
		File testFile = folder.newFile();
		File testFolder = folder.newFolder();
		
		// mock response object
		HttpServletResponse res = Mockito.mock(HttpServletResponse.class);
		
		// mock multipart file
		Path path = testFile.toPath();
        byte[] data = Files.readAllBytes(path);
        MockMultipartFile testMultipartFile = new MockMultipartFile(testFile.getName(), testFile.getName(), "application/txt", data);
       
        //mock multipart request
        MockMultipartHttpServletRequest req = Mockito.mock(MockMultipartHttpServletRequest.class);
		String boundary = "contents of the test file";
		req.setContentType("multipart/form-data; boundary="+boundary);
		req.setContent(createFileContent(data,boundary,"application/txt","Listening Essay (Rough).docx"));
		req.addFile(testMultipartFile);
		req.setMethod("POST");
		req.setParameter("variant", "php");
		req.setParameter("os", "windows");
		req.setParameter("version", "10");
		
		// set upload destination in class
		FileUpload testUpload = new FileUpload();
		testUpload.setDocsPath(testFolder.toString() + "/");
		
		// test upload document with method
		testUpload.doPost(req, res);
		
		// check if file is in upload destination
		for (File file : testFolder.listFiles()){
			if (testFile.getName() == file.getName()){
				outcome = file.getName();
			}
		}
		assertEquals("unsuccessful upload", testFile.getName(), outcome);
		// assertEquals("not finished", true, false);
	}

	public byte[] createFileContent(byte[] data, String boundary, String contentType, String fileName){
        String start = "--" + boundary + "\r\n Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\"\r\n"
                 + "Content-type: "+contentType+"\r\n\r\n";;

        String end = "\r\n--" + boundary + "--";
        String content = start + end;
        return content.getBytes();
	}
}

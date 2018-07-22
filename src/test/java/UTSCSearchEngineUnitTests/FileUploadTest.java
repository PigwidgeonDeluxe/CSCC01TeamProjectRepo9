package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

import UTSCSearchEngine.FileUpload;

public class FileUploadTest extends Mockito{

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void UploadFileTest() throws Exception {
		String testName = "testFile.txt";
		String fileContents = "contents of testing file";
		
		// mock upload files and upload destination	
		File testFile = folder.newFile (testName);
		// allow access to read and write
		testFile.setWritable(true);
		testFile.setReadable(true);
		// write some contents of testFile
		Writer writer = new FileWriter(testFile.toString());
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(fileContents);
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();
		
		// mock objects
		List<FileItem> multifiles = new ArrayList<FileItem>();
		DiskFileItem file = new DiskFileItem("fileData", "text/plain", true, testName, fileContents.length(), testFile.getParentFile());
		InputStream input =  new FileInputStream(testFile);
		OutputStream os = file.getOutputStream();
		IOUtils.copy(input, os);
		multifiles.add(file);

		// set upload destination in class
		FileUpload testUpload = new FileUpload();
		testUpload.setDocsPath("./");
		
		// test upload document with method
		testUpload.upload(multifiles);

		// check if file exist in test folder
		File expect = new File("./" + testName);
		boolean result = expect.exists();
		// only delete if unsuccessful upload
		if(!result) Files.deleteIfExists(Paths.get(expect.toString()));
		assertEquals("unsuccessful upload", true, result);
		// check contents of file are the same
		if(result){
			// read file and check if last line has
			String line = null;
			String content_result = "";
			BufferedReader br = new BufferedReader(new FileReader(expect.toString()));
			while ((line = br.readLine()) != null) {
				content_result = content_result + line;
			}
			br.close();

			// test if file contents in right location
			Files.deleteIfExists(Paths.get(expect.toString()));
			assertEquals("Contents of file are not the same", fileContents, content_result);
		} else {
			Files.deleteIfExists(Paths.get(expect.toString()));
			fail("testFile.txt does not exist");
		}
	}
}
package UTSCSearchEngineUnitTests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import UTSCSearchEngine.FileDownload;

public class DownloadTest extends Mockito{

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void test() throws IOException {
		String testName = "testFile.txt";
		String fileContents = "contents of testing file";
		// mock upload files and upload destination
		File testFile = folder.newFile(testName);
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
		
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		ServletOutputStreamEx outResult = new ServletOutputStreamEx();
		when(req.getParameter("fileName")).thenReturn(testFile.getName());
		when(res.getOutputStream()).thenReturn(outResult);
		// set location of download
		FileDownload testDownload = new FileDownload();
		testDownload.setDocsPath(testFile.getParent().toString() + File.separator);
		// test upload document with method
		testDownload.doGet(req, res);
		assertEquals("downloaded content file is not the same", true, outResult.toString().contains(fileContents));
	}
	
	/** FOR TESTING ONLY
	 * class to write OutputStream as string to compare file contents
	 */
	public class ServletOutputStreamEx extends ServletOutputStream {

		StringBuilder stringBuilder;

		public ServletOutputStreamEx() {
			this.stringBuilder = new StringBuilder();
		}

		@Override
		public void write(int b) throws IOException {
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			stringBuilder.append(new String(b, off, len, "UTF-8"));
		}

		@Override
		/**
		 * this string method outputs extra new lines
		 */
		public String toString() {
			return stringBuilder.toString();
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			// not used for test
			return false;
		}

		@Override
		public void setWriteListener(WriteListener arg0) {
			// TODO Auto-generated method stub
			// does nothing for test
		}
	}
}

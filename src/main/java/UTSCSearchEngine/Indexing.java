package UTSCSearchEngine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.EmptyFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.jsoup.Jsoup;

public class Indexing {

	private StandardAnalyzer analyzer = null;
	private Directory index = null;
	private Path docDir = null;

	/**
	 * Initialize and perform indexing with a given path, analyzer, to index
	 * (RAMDirectory)
	 */
	public void doIndexing() {
		this.analyzer = new StandardAnalyzer();
		this.index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try {
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			IndexWriter w = new IndexWriter(index, config);
			indexDocuments(w);
			w.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void doIndexing(String url) {
		this.analyzer = new StandardAnalyzer();
		this.index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try {
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			IndexWriter w = new IndexWriter(index, config);
			indexDocuments(url, w);
			w.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * Index all the documents for a given Path
	 * 
	 * @param w
	 * @throws IOException
	 */
	private void indexDocuments(final IndexWriter w) throws IOException {
		Database db = new Database();

		try {
			ResultSet rs = db.getAllFiles();
			while (rs.next()) {
				addDoc(w, rs.getString("id"), rs.getString("file_name"), rs.getString("file_type"),
						rs.getString("uploader_name"), rs.getString("uploader_type"), rs.getString("file_size"),
						rs.getString("uploaded_on"), rs.getBytes("file"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void indexDocuments(String url, final IndexWriter w) throws IOException {
		Database db = new Database(url);

		try {
			ResultSet rs = db.getAllFiles();
			while (rs.next()) {
				addDoc(w, rs.getString("id"), rs.getString("file_name"), rs.getString("file_type"),
						rs.getString("uploader_name"), rs.getString("uploader_type"), rs.getString("file_size"),
						rs.getString("uploaded_on"), rs.getBytes("file"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Add all the documents' attributes to the index
	 * 
	 * @param w
	 * @param fileName
	 * @param fileType
	 * @param userName
	 * @param userType
	 * @throws IOException
	 */
	private static void addDoc(IndexWriter w, String fileId, String fileName, String fileType, String userName,
			String userType, String fileSize, String uploadDate, byte[] fileContents) throws IOException {
		Document doc = new Document();
		InputStream in = new ByteArrayInputStream(fileContents);

		// add the values to the index
		doc.add(new StringField("fileId", fileId, Field.Store.YES));
		doc.add(new TextField("fileName", fileName, Field.Store.YES));
		doc.add(new TextField("fileType", fileType, Field.Store.YES));
		doc.add(new TextField("userName", userName.replaceAll("%20", " "), Field.Store.YES));
		doc.add(new StringField("userType", userType, Field.Store.YES));
		doc.add(new TextField("fileSize", fileSize, Field.Store.YES));
		doc.add(new TextField("uploadDate", uploadDate, Field.Store.YES));

		// restrict content analysis
		if (fileType.contains("docx") || fileType.contains("html") || fileType.contains("txt")
				|| fileType.contains("pdf")) {
			// if the file is a docx
			if (fileType.contains("docx")) {
				List<String> docContents = parseDocContents(fileContents);
				String contentString = "";
				// add all contents to a single string, ensure the contents of the file is not
				// empty
				if (docContents != null) {
					for (String content : docContents) {
						if (content != null) {
							// store all content to a single string
							contentString = contentString.concat(content + " ");
						}
					}
				}
				// add the doc contents
				doc.add(new TextField("contents", contentString, Field.Store.YES));

			} else if (fileType.contains("html")) { // if the file is an html
				org.jsoup.nodes.Document html = Jsoup.parse(in, "UTF-8", "/");
				String contentsString = html.body().text();
				// add the txt contents
				doc.add(new TextField("contents", contentsString, Field.Store.YES));

			} else if (fileType.contains("pdf")) { // if the file is a pdf
				PDDocument pdf = PDDocument.load(fileContents);
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setLineSeparator("\n");
				String contentsString = stripper.getText(pdf).replace("\n", " ").replace("\r", "");
				pdf.close();
				doc.add(new TextField("contents", contentsString, Field.Store.YES));

			} else { // otherwise if its a generic text file
				Scanner contentsScanner = new Scanner(in);
				String contentsString = "";
				if (contentsScanner.hasNextLine()) {
					contentsString = contentsScanner.useDelimiter("\\A").next().replace("\n", " ").replace("\r", "");
				}
				// add the txt contents
				doc.add(new TextField("contents", contentsString, Field.Store.YES));
				contentsScanner.close();
			}
		}

		w.addDocument(doc);
		w.commit();
	}

	public Directory getIndex() {
		return this.index;
	}

	public StandardAnalyzer getAnalyzer() {
		return this.analyzer;
	}

	public Path getDocDir() {
		return this.docDir;
	}

	/**
	 * Method that reads from doc files
	 * 
	 * @param file
	 * @return
	 */
	private static List<String> parseDocContents(byte[] file) {
		List<XWPFParagraph> paragraphs;
		List<String> fileData = new ArrayList<>();
		try {
			// get all paragraphs of the documents
			InputStream in = new ByteArrayInputStream(file);
			XWPFDocument document = new XWPFDocument(in);
			paragraphs = document.getParagraphs();
			document.close();
			// add all the text of the document to the List of strings
			for (XWPFParagraph paragraph : paragraphs) {
				fileData.add(paragraph.getText());
			}
		} catch (EmptyFileException e) {
			// if the file is empty, who cares
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return fileData;
	}

}

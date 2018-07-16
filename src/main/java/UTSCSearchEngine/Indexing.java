package UTSCSearchEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class Indexing {

  private String docsPath = "./src/main/resources/"; // default index directory
  private StandardAnalyzer analyzer = null;
  private Directory index = null;
  private Path docDir = null;

  /**
   * Initialize and perform indexing with a given path, analyzer, to index (RAMDirectory)
   */
  public void doIndexing() {
    this.docDir = Paths.get(docsPath);
    this.analyzer = new StandardAnalyzer();
    this.index = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    try {
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
      IndexWriter w = new IndexWriter(index, config);
      indexDocuments(w, docDir);
      w.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /**
   * Index all the documents for a given Path
   * 
   * @param w
   * @param currentPath
   * @throws IOException
   */
  private static void indexDocuments(final IndexWriter w, Path currentPath) throws IOException {
    // Directory?
    if (Files.isDirectory(currentPath)) {
      // Iterate directory
      for (final File fileEntry : currentPath.toFile().listFiles()) {
        if (fileEntry.isDirectory()) {
          indexDocuments(w, fileEntry.toPath());
        } else {

          // Index this file
          addDoc(w, fileEntry);
        }
      }
    } else {
      // Index this file
      addDoc(w, currentPath.toFile());
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
  private static void addDoc(IndexWriter w, File file) throws IOException {
    Document doc = new Document();
    FileReader fr = new FileReader(file);
    // get required values from the file
    String fileName = file.getName();
    String fileType = file.getName().substring(file.getName().lastIndexOf('.') + 1);
    String userName = "user";
    String userType = "student";

    // add the values to the index
    doc.add(new TextField("fileName", fileName, Field.Store.YES));
    doc.add(new TextField("fileType", fileType, Field.Store.YES));
    doc.add(new TextField("userName", userName, Field.Store.YES));
    doc.add(new StringField("userType", userType, Field.Store.YES));
    Scanner contentsScanner = new Scanner(fr);
    if (fileType.contains("doc")) {
      String[] docContents = parseDocContents(file);
      String contentString = "";
      // add all contents to a single string, ensure the contents of the file is not empty
      if (docContents != null) {
        for (Integer i = 0; i < docContents.length; i++) {
          if (docContents[i] != null) {
            contentString.concat(docContents[i]);
          }
        }
      }
      // add the doc contents
      doc.add(new TextField("contents", contentString, Field.Store.YES));
    } else {
      String contentsString = "";
      if (contentsScanner.hasNextLine()) {
        contentsString = contentsScanner.useDelimiter("\\A").next();
      }
      // add the txt contents
      doc.add(new TextField("contents", contentsString, Field.Store.YES));
      contentsScanner.close();
    }

    w.addDocument(doc);
    w.commit();
  }

  public void setDocsPath(String docsPath) {
    this.docsPath = docsPath;
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
  private static String[] parseDocContents(File file) {
    WordExtractor extractor = null;
    String[] fileData = null;
    try {
      FileInputStream fis = new FileInputStream(file.getAbsolutePath());
      HWPFDocument document = new HWPFDocument(fis);
      extractor = new WordExtractor(document);
      fileData = extractor.getParagraphText();
    } catch (EmptyFileException e) {
      // if the file is empty, who cares
    } catch (Exception exep) {
      exep.printStackTrace();
    }

    return fileData;
  }

}

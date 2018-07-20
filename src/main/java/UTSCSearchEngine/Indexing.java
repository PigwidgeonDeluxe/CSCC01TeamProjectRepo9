package UTSCSearchEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Indexing {

  private StandardAnalyzer analyzer = null;
  private Directory index = null;
  private Path docDir = null;

  /**
   * Initialize and perform indexing with a given path, analyzer, to index (RAMDirectory)
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
        addDoc(w,
            rs.getString("file_name"),
            rs.getString("file_type"),
            rs.getString("file_size"),
            rs.getString("uploader_name"),
            rs.getString("uploader_type"),
            rs.getString("uploaded_on"));
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
        addDoc(w,
            rs.getString("file_name"),
            rs.getString("file_type"),
            rs.getString("file_size"),
            rs.getString("uploader_name"),
            rs.getString("uploader_type"),
            rs.getString("uploaded_on"));
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
  private static void addDoc(IndexWriter w, String fileName, String fileType, String userName,
      String userType, String fileSize, String uploadDate) throws IOException {
    Document doc = new Document();

    doc.add(new TextField("fileName", fileName, Field.Store.YES));
    doc.add(new TextField("fileType", fileType, Field.Store.YES));
    doc.add(new TextField("userName", userName, Field.Store.YES));
    doc.add(new StringField("userType", userType, Field.Store.YES));
    doc.add(new TextField("fileSize", fileSize, Field.Store.YES));
    doc.add(new TextField("uploadDate", uploadDate, Field.Store.YES));

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

}

package UTSCSearchEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
          addDoc(w, fileEntry.getName(),
              fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.') + 1), "user",
              "student");
        }
      }
    } else {
      // Index this file
      addDoc(w, currentPath.getFileName().toString(), currentPath.getFileName().toString()
          .substring(currentPath.getFileName().toString().lastIndexOf('.') + 1), "user", "student");
    }
  }
  
  /**
   * Add all the documents' attributes to the index
   * @param w
   * @param fileName
   * @param fileType
   * @param userName
   * @param userType
   * @throws IOException
   */
  private static void addDoc(IndexWriter w, String fileName, String fileType, String userName,
      String userType) throws IOException {
    Document doc = new Document();

    doc.add(new TextField("fileName", fileName, Field.Store.YES));
    doc.add(new TextField("fileType", fileType, Field.Store.YES));
    doc.add(new TextField("userName", userName, Field.Store.YES));
    doc.add(new StringField("userType", userType, Field.Store.YES));

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

}

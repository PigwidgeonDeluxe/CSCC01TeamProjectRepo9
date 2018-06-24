package fileIndexing;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class FileIndexer {
  
  private static StandardAnalyzer analyzer = null;
  private static Directory index = null;
  private static String docsPath = "file_DIR"; // folder where files to be indexed are stored
  // private static String indexPath = "index_DIR"; // folder to store indexes
  private static Path docDir = null;

  /*
   * Indexing files from docsPath and storing the to the directory
   */
  public void doIndexing() {
    docDir = Paths.get(docsPath);
    analyzer = new StandardAnalyzer(); // analyzer w/ default stop words
    index = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(analyzer); // default config
    try {
      // Directory dir = FSDirectory.open(Paths.get(indexPath));
      config.setOpenMode(OpenMode.CREATE_OR_APPEND); // set config to full modify
      IndexWriter w = new IndexWriter(index, config); // write new index files to index
      indexDocuments(w, docDir);
      w.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
  
  /*
   * Recursively go through each file and folder inside docsPath and add their
   * attributes to the index
   */
  private static void indexDocuments(final IndexWriter w, Path currentPath) throws IOException {
    
    
  }
}

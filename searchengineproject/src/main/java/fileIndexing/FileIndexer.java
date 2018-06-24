package fileIndexing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Field;

/*
 * A basic indexer that indexes stored files at a given location, docsPath, and stores the index to RAM
 */
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
        // Directory?
        if (Files.isDirectory(currentPath)) {
            // Iterate directory
            for (final File fileEntry : currentPath.toFile().listFiles()) {
                if (fileEntry.isDirectory()) {
                    indexDocuments(w, fileEntry.toPath());
                } else {
                    // Index this file
                    addDoc(w, fileEntry.getName(), currentPath, Files.getLastModifiedTime(currentPath));
                }
            }
        } else {
            // Index this file
            addDoc(w, currentPath.getFileName().toString(), currentPath, Files.getLastModifiedTime(currentPath));
        }
    }

    /*
     * Add given attributes to the index: title, path, lastmodified
     */
    private static void addDoc(IndexWriter w, String title, Path file, FileTime lastModified) throws IOException {
        Document doc = new Document();
        System.out.println(title + " " + file.toString() + " " + lastModified);
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("path", file.toString(), Field.Store.YES));
        doc.add(new StringField("modified", lastModified.toString(), Field.Store.YES));
        // doc.add(new TextField("contents", new String(Files.readAllBytes(file)),
        // Field.Store.YES));
        w.addDocument(doc);
        w.commit();
    }

    /*
     * Get method that returns the Directory "index" i.e. RAMDirectory
     */
    public Directory getIndex() {
        return index;
    }

    /*
     * Get method that returns the analyzer i.e. StandardAnalyzer
     */
    public StandardAnalyzer getAnalyzer() {
        return analyzer;
    }

    /*
     * Set the file directory path with a given string path name
     * 
     * @param String newPath
     */
    public void setDocsPath(String newPath) {
        docsPath = newPath;
    }
}
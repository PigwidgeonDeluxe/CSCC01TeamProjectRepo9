package UTSCSearchEngine;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileIndexer {

    private static StandardAnalyzer analyzer = null;
    private static Directory index = null;
    private static String docsPath = "./src/main/java/resources";
    private static Path docDir = null;

    public void doIndexing() {
        docDir = Paths.get(docsPath);
        analyzer = new StandardAnalyzer();
        index = new RAMDirectory();
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

    private static void indexDocuments(final IndexWriter w, Path currentPath) throws IOException {
        // Directory?
        if (Files.isDirectory(currentPath)) {
            // Iterate directory
            for (final File fileEntry : currentPath.toFile().listFiles()) {
                if (fileEntry.isDirectory()) {
                    indexDocuments(w, fileEntry.toPath());
                } else {
                    // Index this file
                    addDoc(w,
                        fileEntry.getName(),
                        fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.') + 1),
                        "user",
                        "student");
                }
            }
        } else {
            // Index this file
            addDoc(w,
                currentPath.getFileName().toString(),
                currentPath.getFileName().toString().substring(currentPath.getFileName().toString().lastIndexOf('.') + 1),
                "user",
                "student");
        }
    }

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
}

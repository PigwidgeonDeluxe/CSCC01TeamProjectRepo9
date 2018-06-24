package fileIndexingTests;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fileIndexing.FileIndexer;

public class FileIndexerUnitTests {
    
    public static FileIndexer indexer = new FileIndexer();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void myTest() {
        try {
            // this folder gets cleaned up automatically by JUnit
            File file = folder.newFile("testfile1.txt");
            
            indexer.setDocsPath(folder.getRoot().toString());
            
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}

package apacheLucene;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Field;

@WebServlet("/search")
public class Search extends HttpServlet {

    private static StandardAnalyzer analyzer = null;
    private static Directory index = null;
    private static final long serialVersionUID = 1L;

    private static void addDoc(IndexWriter w, String fileName, String fileType,
                               String userType, String userName) throws IOException {
        Document doc = new Document();

        doc.add(new TextField("fileName", fileName, Field.Store.YES));
        doc.add(new TextField("fileType", fileType, Field.Store.YES));
        doc.add(new TextField("userName", userName, Field.Store.YES));
        doc.add(new StringField("userType", userType, Field.Store.YES));
        w.addDocument(doc);
    }


    public void init(ServletConfig config1) throws ServletException {
        super.init(config1);
        System.out.println("Started: init");
        startIndexing();
        System.out.println("Finished: init");
    }


    private void startIndexing() {
        /*
         * 1. analyzer is used for parsing the data out.
         * the standardAnalyzer will remove the stop words.
         *
         * 2. RamDirectory is the indexer that will be maintained in
         * the RAM
         */
        analyzer = new StandardAnalyzer();
        index = new RAMDirectory();

        /*
         * IndexWriterConfig is the configuration that will be used
         * when creating the config. I am choosing the default one.
         */
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try {
            /*
             * IndexWriter is used to write into the index.
             */
            IndexWriter w = new IndexWriter(index, config);
            addDoc(w, "CSCC01 Midterm Solutions.pdf", "PDF","instructor", "abbas");
            addDoc(w, "CSCC01 study notes.pdf", "PDF","student", "zhangke");
            addDoc(w, "CSCC01 week 5 lecture notes.docx", "DOCX","student", "zhangke");
            w.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain");

        String fileNameQuery = req.getParameter("fileName");
        if (fileNameQuery != null) {
            try {
                /*
                 * 1. Query object, created that encapusulates the user query
                 *
                 * 2. IndexReader that allows you to read the index.
                 *
                 * 3. IndexSearcher that allows you to take the query and search the
                 *    index.
                 */
                Query q = new QueryParser("fileName", analyzer).parse(fileNameQuery);
                int hitsPerPage = 10;
                IndexReader reader = DirectoryReader.open(index);
                IndexSearcher searcher = new IndexSearcher(reader);
                TopDocs docs = searcher.search(q, hitsPerPage);
                ScoreDoc[] hits = docs.scoreDocs;
                StringBuilder responseBackToUser = new StringBuilder();
                for (int i = 0; i < hits.length; ++i) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType") + "-" + d.get("userName") + "\n");
                }
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.getWriter().write(responseBackToUser.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String fileTypeQuery = req.getParameter("fileType");
        if (fileTypeQuery != null) {
            try {
                Query q = new QueryParser("fileType", analyzer).parse(fileTypeQuery);
                int hitsPerPage = 10;
                IndexReader reader = DirectoryReader.open(index);
                IndexSearcher searcher = new IndexSearcher(reader);
                TopDocs docs = searcher.search(q, hitsPerPage);
                ScoreDoc[] hits = docs.scoreDocs;
                StringBuilder responseBackToUser = new StringBuilder();
                for (int i = 0; i < hits.length; ++i) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType") + "-" + d.get("userName") + "\n");
                }
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.getWriter().write(responseBackToUser.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        String userQuery = req.getParameter("userName");
        if(userQuery != null){
        	try{
        		Query q = new QueryParser("userName", analyzer).parse(fileTypeQuery);
                int hitsPerPage = 10;
                IndexReader reader = DirectoryReader.open(index);
                IndexSearcher searcher = new IndexSearcher(reader);
                TopDocs docs = searcher.search(q, hitsPerPage);
                ScoreDoc[] hits = docs.scoreDocs;
                StringBuilder responseBackToUser = new StringBuilder();
                for (int i = 0; i < hits.length; ++i) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType") + "-" + d.get("userName") + "\n");
                }
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.getWriter().write(responseBackToUser.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
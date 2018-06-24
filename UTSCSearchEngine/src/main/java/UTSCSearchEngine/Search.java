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

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
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
            addDoc(w, "Lucene in Action", "193398817");
            addDoc(w, "Lucene for Dummies", "55320055Z");
            addDoc(w, "Managing Gigabytes", "55063554A");
            addDoc(w, "The Art of Computer Science", "9900333X");
            w.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain");
        String querystr = req.getParameter("query");
        try {
            /*
             * 1. Query object, created that encapusulates the user query
             *
             * 2. IndexReader that allows you to read the index.
             *
             * 3. IndexSearcher that allows you to take the query and search the
             *    index.
             */
            Query q = new QueryParser("title", analyzer).parse(querystr);
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            StringBuilder responseBackToUser = new StringBuilder();
            responseBackToUser.append("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                responseBackToUser.append((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
            }
            resp.getWriter().write(responseBackToUser.toString());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
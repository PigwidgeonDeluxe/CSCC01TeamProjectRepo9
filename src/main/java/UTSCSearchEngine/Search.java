package UTSCSearchEngine;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

@WebServlet("/search")
public class Search extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static StandardAnalyzer analyzer = null;
	private static Directory index = null;
	private static Indexing indexer = new Indexing();

	public void init(ServletConfig config1) throws ServletException {
		super.init(config1);
		System.out.println("Started: init");
		callIndexing();
		System.out.println("Finished: init");
	}

	public static void callIndexing() {
		indexer.doIndexing();
		analyzer = indexer.getAnalyzer();
		index = indexer.getIndex();
	}

	public void callIndexing(String url) {
		indexer.doIndexing(url);
		analyzer = indexer.getAnalyzer();
		index = indexer.getIndex();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");

		String fileNameQuery = req.getParameter("fileName");
		String fileTypeQuery = req.getParameter("fileType");
		String userNameQuery = req.getParameter("userName");
		String userTypeQuery = req.getParameter("userType");
		String userContentQuery = req.getParameter("contents");

		if (fileNameQuery != null || fileTypeQuery != null || userNameQuery != null || userTypeQuery != null) {
			BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();

			if (fileNameQuery != null) {
				Query fileNameQ = new TermQuery(new Term("fileName", fileNameQuery));
				boolQuery.add(fileNameQ, BooleanClause.Occur.MUST);
			}
			if (fileTypeQuery != null) {
				Query fileTypeQ = new TermQuery(new Term("fileType", fileTypeQuery));
				boolQuery.add(fileTypeQ, BooleanClause.Occur.MUST);
			}
			if (userNameQuery != null) {
				Query userNameQ = new TermQuery(new Term("userName", userNameQuery));
				boolQuery.add(userNameQ, BooleanClause.Occur.MUST);
			}
			if (userTypeQuery != null) {
				Query userTypeQ = new TermQuery(new Term("userType", userTypeQuery));
				boolQuery.add(userTypeQ, BooleanClause.Occur.MUST);
			}
			if (userContentQuery != null) {
				Query userContentQ = new TermQuery(new Term("contents", userContentQuery));
				boolQuery.add(userContentQ, BooleanClause.Occur.MUST);
			}
			Query q = boolQuery.build();
			search(q, resp);
		}
	}

	/**
	 * Search for Query q and return a response to the user containing the requested
	 * information
	 * 
	 * @param q
	 * @param resp
	 * @throws IOException
	 */
	private static void search(Query q, HttpServletResponse resp) throws IOException {
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(q, hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;
		StringBuilder responseBackToUser = new StringBuilder();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			String contents = d.get("contents");
			contents = contents != null ? contents.substring(0, Math.min(contents.length(), 160)) : "";
			responseBackToUser.append(
					d.get("fileName") + "~" + d.get("fileType") + "~" + d.get("userType") + "~" + d.get("userName")
							+ "~" + d.get("fileSize") + "~" + d.get("uploadDate") + "~" + "\"" + contents + "\"\n");
		}
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.getWriter().write(responseBackToUser.toString());
	}
}

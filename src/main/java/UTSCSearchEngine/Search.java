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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

@WebServlet("/search")
public class Search extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static String docsPath = "./src/main/resources/";
	private static StandardAnalyzer analyzer = null;
	private static Directory index = null;
	private static Indexing indexer = new Indexing();

	public void init(ServletConfig config1) throws ServletException {
		super.init(config1);
		System.out.println("Started: init");
		callIndexing();
		System.out.println("Finished: init");
	}

	private static void callIndexing() {
		indexer.doIndexing();
		analyzer = indexer.getAnalyzer();
		index = indexer.getIndex();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");

		String fileNameQuery = req.getParameter("fileName");
		String fileTypeQuery = req.getParameter("fileType");
		String userNameQuery = req.getParameter("userName");
		String userTypeQuery = req.getParameter("userType");

		String query = "";
		String field = "fileName";

		if (userTypeQuery != null) {
			if (query != "") {
				query = query + " OR ";
			}
			query = query + "userType:" + userTypeQuery;
			field = "userType";
		}
		if (fileTypeQuery != null) {
			if (query != "") {
				query = query + " OR ";
			}
			query = query + "fileType:" + fileTypeQuery;
			field = "fileType";
		}
		if (userNameQuery != null) {
			if (query != "") {
				query = query + " OR ";
			}
			query = query + "userName:" + userNameQuery;
			field = "userName";
		}
		if (fileNameQuery != null) {
			if (query != "") {
				query = query + " OR ";
			}
			query = query + "fileName:" + fileNameQuery;
			field = "fileName";
		}

		try {
			/*
			 * 1. Query object, created that encapusulates the user query
			 *
			 * 2. IndexReader that allows you to read the index.
			 *
			 * 3. IndexSearcher that allows you to take the query and search the index.
			 */
			QueryParser q = new QueryParser(field, analyzer);
			int hitsPerPage = 10;
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);

			TopDocs docs = searcher.search(q.parse(query), hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;
			StringBuilder responseBackToUser = new StringBuilder();
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType") + "-"
						+ d.get("userName") + "\n");
			}
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.getWriter().write(responseBackToUser.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		resp.setContentType("text/plain");
//
//		String fileNameQuery = req.getParameter("fileName");
//		if (fileNameQuery != null) {
//			try {
//				/*
//				 * 1. Query object, created that encapusulates the user query
//				 *
//				 * 2. IndexReader that allows you to read the index.
//				 *
//				 * 3. IndexSearcher that allows you to take the query and search the index.
//				 */
//				Query q = new QueryParser("fileName", analyzer).parse(fileNameQuery);
//				int hitsPerPage = 10;
//				IndexReader reader = DirectoryReader.open(index);
//				IndexSearcher searcher = new IndexSearcher(reader);
//				TopDocs docs = searcher.search(q, hitsPerPage);
//				ScoreDoc[] hits = docs.scoreDocs;
//				StringBuilder responseBackToUser = new StringBuilder();
//				for (int i = 0; i < hits.length; ++i) {
//					int docId = hits[i].doc;
//					Document d = searcher.doc(docId);
//					responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType")
//							+ "-" + d.get("userName") + "\n");
//				}
//				resp.setHeader("Access-Control-Allow-Origin", "*");
//				resp.getWriter().write(responseBackToUser.toString());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//
//		String fileTypeQuery = req.getParameter("fileType");
//		if (fileTypeQuery != null) {
//			try {
//				Query q = new QueryParser("fileType", analyzer).parse(fileTypeQuery);
//				int hitsPerPage = 10;
//				IndexReader reader = DirectoryReader.open(index);
//				IndexSearcher searcher = new IndexSearcher(reader);
//				TopDocs docs = searcher.search(q, hitsPerPage);
//				ScoreDoc[] hits = docs.scoreDocs;
//				StringBuilder responseBackToUser = new StringBuilder();
//				for (int i = 0; i < hits.length; ++i) {
//					int docId = hits[i].doc;
//					Document d = searcher.doc(docId);
//					responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType")
//							+ "-" + d.get("userName") + "\n");
//				}
//				resp.setHeader("Access-Control-Allow-Origin", "*");
//				resp.getWriter().write(responseBackToUser.toString());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//
//		String userNameQuery = req.getParameter("userName");
//		if (userNameQuery != null) {
//			try {
//				Query q = new QueryParser("userName", analyzer).parse(userNameQuery);
//				int hitsPerPage = 10;
//				IndexReader reader = DirectoryReader.open(index);
//				IndexSearcher searcher = new IndexSearcher(reader);
//				TopDocs docs = searcher.search(q, hitsPerPage);
//				ScoreDoc[] hits = docs.scoreDocs;
//				StringBuilder responseBackToUser = new StringBuilder();
//				for (int i = 0; i < hits.length; ++i) {
//					int docId = hits[i].doc;
//					Document d = searcher.doc(docId);
//					responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType")
//							+ "-" + d.get("userName") + "\n");
//				}
//				resp.setHeader("Access-Control-Allow-Origin", "*");
//				resp.getWriter().write(responseBackToUser.toString());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//
//		String userTypeQuery = req.getParameter("userType");
//		if (userTypeQuery != null) {
//			try {
//				Query q = new QueryParser("userType", analyzer).parse(userTypeQuery);
//				int hitsPerPage = 10;
//				IndexReader reader = DirectoryReader.open(index);
//				IndexSearcher searcher = new IndexSearcher(reader);
//				TopDocs docs = searcher.search(q, hitsPerPage);
//				ScoreDoc[] hits = docs.scoreDocs;
//				StringBuilder responseBackToUser = new StringBuilder();
//				for (int i = 0; i < hits.length; ++i) {
//					int docId = hits[i].doc;
//					Document d = searcher.doc(docId);
//					responseBackToUser.append(d.get("fileName") + "-" + d.get("fileType") + "-" + d.get("userType")
//							+ "-" + d.get("userName") + "\n");
//				}
//				resp.setHeader("Access-Control-Allow-Origin", "*");
//				resp.getWriter().write(responseBackToUser.toString());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}

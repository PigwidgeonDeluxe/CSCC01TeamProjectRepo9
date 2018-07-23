package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import UTSCSearchEngine.Indexing;

public class SearchQueryTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testSearchByFilename()
			throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

		File txtFile1 = folder.newFile("test file1.txt");
		File txtFile2 = folder.newFile("test file2.txt");
		File pdfFile = folder.newFile("sample file.pdf");
		File docxFile = folder.newFile("word document.docx");
		File testFolder = folder.newFolder();

		List<String> case1 = Arrays.asList("test file1.txt", "test file2.txt");
		List<String> case2 = Arrays.asList("sample file.pdf");
		List<String> case3 = Arrays.asList("word document.docx");

		Indexing indexer = new Indexing();

		Field field = indexer.getClass().getDeclaredField("docsPath");
		field.setAccessible(true);
		field.set(indexer, folder.getRoot().toString());
		indexer.doIndexing();

		BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
		Query q = new TermQuery(new Term("fileName", "test"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);

		IndexReader reader = DirectoryReader.open(indexer.getIndex());
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(boolQuery.build(), 10);
		ScoreDoc[] hits = docs.scoreDocs;
		List<String> case1List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case1List.add(d.get("fileName"));
		}
		Collections.sort(case1List);
		assertEquals("improperly matched files", case1, case1List);

		boolQuery = new BooleanQuery.Builder();
		q = new TermQuery(new Term("fileName", "sample"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);
		docs = searcher.search(boolQuery.build(), 10);
		hits = docs.scoreDocs;
		List<String> case2List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case2List.add(d.get("fileName"));
		}
		Collections.sort(case2List);
		assertEquals("improperly matched files", case2, case2List);

		boolQuery = new BooleanQuery.Builder();
		q = new TermQuery(new Term("fileName", "word"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);
		docs = searcher.search(boolQuery.build(), 10);
		hits = docs.scoreDocs;
		List<String> case3List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case3List.add(d.get("fileName"));
		}
		Collections.sort(case3List);
		assertEquals("improperly matched files", case3, case3List);
	}

	@Test
	public void testSearchByFileType()
			throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

		File txtFile1 = folder.newFile("test file1.txt");
		File txtFile2 = folder.newFile("test file2.txt");
		File pdfFile = folder.newFile("sample file.pdf");
		File docxFile = folder.newFile("word document.docx");
		File testFolder = folder.newFolder();

		List<String> case1 = Arrays.asList("txt", "txt");
		List<String> case2 = Arrays.asList("pdf");
		List<String> case3 = Arrays.asList("docx");

		Indexing indexer = new Indexing();

		Field field = indexer.getClass().getDeclaredField("docsPath");
		field.setAccessible(true);
		field.set(indexer, folder.getRoot().toString());
		indexer.doIndexing();

		BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
		Query q = new TermQuery(new Term("fileType", "txt"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);

		IndexReader reader = DirectoryReader.open(indexer.getIndex());
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(boolQuery.build(), 10);
		ScoreDoc[] hits = docs.scoreDocs;
		List<String> case1List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case1List.add(d.get("fileType"));
		}
		Collections.sort(case1List);
		assertEquals("improperly matched files", case1, case1List);

		boolQuery = new BooleanQuery.Builder();
		q = new TermQuery(new Term("fileType", "pdf"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);

		docs = searcher.search(boolQuery.build(), 10);
		hits = docs.scoreDocs;
		List<String> case2List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case2List.add(d.get("fileType"));
		}
		Collections.sort(case2List);
		assertEquals("improperly matched files", case2, case2List);

		boolQuery = new BooleanQuery.Builder();
		q = new TermQuery(new Term("fileType", "docx"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);

		docs = searcher.search(boolQuery.build(), 10);
		hits = docs.scoreDocs;
		List<String> case3List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case3List.add(d.get("fileType"));
		}
		Collections.sort(case3List);
		assertEquals("improperly matched files", case3, case3List);
	}

	@Test
	public void testSearchByUserType()
			throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

		File txtFile1 = folder.newFile("test file1.txt");
		File txtFile2 = folder.newFile("test file2.txt");
		File pdfFile = folder.newFile("sample file.pdf");
		File docxFile = folder.newFile("word document.docx");
		File testFolder = folder.newFolder();

		List<String> testCase = Arrays.asList("sample file.pdf", "test file1.txt", "test file2.txt",
				"word document.docx");

		Indexing indexer = new Indexing();

		Field field = indexer.getClass().getDeclaredField("docsPath");
		field.setAccessible(true);
		field.set(indexer, folder.getRoot().toString());
		indexer.doIndexing();

		BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
		Query q = new TermQuery(new Term("userType", "student"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);

		IndexReader reader = DirectoryReader.open(indexer.getIndex());
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(boolQuery.build(), 10);
		ScoreDoc[] hits = docs.scoreDocs;
		List<String> case1List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case1List.add(d.get("fileName"));
		}
		Collections.sort(case1List);
		assertEquals("improperly matched files", testCase, case1List);
	}

	@Test
	public void testSearchByUserName()
			throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

		File txtFile1 = folder.newFile("test file1.txt");
		File txtFile2 = folder.newFile("test file2.txt");
		File pdfFile = folder.newFile("sample file.pdf");
		File docxFile = folder.newFile("word document.docx");
		File testFolder = folder.newFolder();

		List<String> testCase = Arrays.asList("sample file.pdf", "test file1.txt", "test file2.txt",
				"word document.docx");

		Indexing indexer = new Indexing();

		Field field = indexer.getClass().getDeclaredField("docsPath");
		field.setAccessible(true);
		field.set(indexer, folder.getRoot().toString());
		indexer.doIndexing();

		BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
		Query q = new TermQuery(new Term("userName", "user"));
		boolQuery.add(q, BooleanClause.Occur.SHOULD);

		IndexReader reader = DirectoryReader.open(indexer.getIndex());
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(boolQuery.build(), 10);
		ScoreDoc[] hits = docs.scoreDocs;
		List<String> case1List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case1List.add(d.get("fileName"));
		}
		Collections.sort(case1List);
		assertEquals("improperly matched files", testCase, case1List);
	}

	public void testSearchByMultiple()
			throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {

		File txtFile1 = folder.newFile("test file1.txt");
		File txtFile2 = folder.newFile("test file2.txt");
		File txtFile3 = folder.newFile("sample file.txt");
		File pdfFile = folder.newFile("test file.pdf");
		File docxFile = folder.newFile("word document.docx");
		File testFolder = folder.newFolder();

		List<String> case1 = Arrays.asList("test file1.txt", "test file2.txt", "sample file2.txt");
		List<String> case2 = Arrays.asList("test file1.txt", "test file2.txt");
		List<String> case3 = Arrays.asList("word document.docx");

		Indexing indexer = new Indexing();

		Field field = indexer.getClass().getDeclaredField("docsPath");
		field.setAccessible(true);
		field.set(indexer, folder.getRoot().toString());
		indexer.doIndexing();

		BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
		Query q1 = new TermQuery(new Term("fileType", "txt"));
		Query q2 = new TermQuery(new Term("userType", "student"));
		boolQuery.add(q1, BooleanClause.Occur.SHOULD);
		boolQuery.add(q2, BooleanClause.Occur.SHOULD);

		IndexReader reader = DirectoryReader.open(indexer.getIndex());
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(boolQuery.build(), 10);
		ScoreDoc[] hits = docs.scoreDocs;
		List<String> case1List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case1List.add(d.get("fileName"));
		}
		Collections.sort(case1List);
		assertEquals("improperly matched files", case1, case1List);

		boolQuery = new BooleanQuery.Builder();
		q1 = new TermQuery(new Term("fileName", "test"));
		q2 = new TermQuery(new Term("fileType", "txt"));
		boolQuery.add(q1, BooleanClause.Occur.SHOULD);
		boolQuery.add(q2, BooleanClause.Occur.SHOULD);

		docs = searcher.search(boolQuery.build(), 10);
		hits = docs.scoreDocs;
		List<String> case2List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case2List.add(d.get("fileName"));
		}
		Collections.sort(case2List);
		assertEquals("improperly matched files", case2, case2List);

		boolQuery = new BooleanQuery.Builder();
		q1 = new TermQuery(new Term("fileName", "word"));
		q2 = new TermQuery(new Term("userName", "user"));
		boolQuery.add(q1, BooleanClause.Occur.SHOULD);
		boolQuery.add(q2, BooleanClause.Occur.SHOULD);
		docs = searcher.search(boolQuery.build(), 10);
		hits = docs.scoreDocs;
		List<String> case3List = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			case3List.add(d.get("fileName"));
		}
		Collections.sort(case3List);
		assertEquals("improperly matched files", case3, case3List);
	}
}

package UTSCSearchEngineUnitTests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class TestUtils {

  public static ByteArrayOutputStream createDocFile(String content) throws IOException {

    XWPFDocument doc = new XWPFDocument();

    XWPFParagraph paragraph = doc.createParagraph();
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    XWPFRun paragraphRun = paragraph.createRun();
    paragraphRun.setText(content);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    doc.write(out);
    doc.close();
    return out;
  }

  public static ByteArrayOutputStream createPdfFile(String content) throws IOException {

    PDDocument doc = new PDDocument();

    PDPage page = new PDPage();
    PDPageContentStream contentStream = new PDPageContentStream(doc, page);

    contentStream.beginText();
    contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
    contentStream.newLineAtOffset(25, 500);
    contentStream.showText(content);
    contentStream.close();

    doc.addPage(page);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    doc.save(out);
    doc.close();
    return out;
  }
}

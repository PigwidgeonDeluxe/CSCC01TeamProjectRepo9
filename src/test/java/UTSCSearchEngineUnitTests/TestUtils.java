package UTSCSearchEngineUnitTests;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class TestUtils {

  public static ByteArrayOutputStream createDocFile(String content) throws IOException {

    XWPFDocument doc = new XWPFDocument();

    // setup document
    XWPFParagraph paragraph = doc.createParagraph();
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    XWPFRun paragraphRun = paragraph.createRun();
    paragraphRun.setText(content);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    doc.write(out);
    return out;
  }
}

package UTSCSearchEngineUnitTests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class CreateDOCX {

  /**
   * Create a word docx file for testing purposes
   * 
   * @param path
   * @param filename
   * @param content
   */
  public static void create(String path, String filename, String content) {

    String output = path + "/" + filename;
    XWPFDocument document = new XWPFDocument();

    // setup the first document
    XWPFParagraph para = document.createParagraph();
    para.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun para1Run = para.createRun();
    para1Run.setText(content);

    // write out the document
    FileOutputStream out;
    try {
      out = new FileOutputStream(output);
      document.write(out);
      out.close();
      document.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

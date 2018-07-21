package UTSCSearchEngineUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import UTSCSearchEngine.User;

public class UserTest extends Mockito {

  private static String docsPath = null;

//  @Test
//  public void testSetDocsPath() throws NoSuchFieldException, SecurityException,
//      IllegalArgumentException, IllegalAccessException {
//    User user = new User();
//    user.setDocsPath("examplepath");
//
//    Field field = user.getClass().getDeclaredField("docsPath");
//    field.setAccessible(true);
//    assertEquals("Docs path didn't match", field.get(user), "examplepath");
//  }
//
//  // create temporary folder for testing
//  @Rule
//  public TemporaryFolder folder = new TemporaryFolder();
//
//  @Test
//  public void testDoPostCreateUserStudent() throws IOException {
//
//    File users = folder.newFile("users.csv");
//    docsPath = users.getParent().toString();
//
//    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);
//    HttpServletResponse response = mock(HttpServletResponse.class);
//    JSONObject json = mock(JSONObject.class);
//
//    BufferedReader bf = mock(BufferedReader.class);
//    Stream<String> str = mock(Stream.class);
//
//    when(request.getReader()).thenReturn(bf);
//    when(bf.lines()).thenReturn(str);
//    when(str.collect(any())).thenReturn("{\n" + "    \"userName\":\"user22\",\n"
//        + "    \"userType\":\"student\",\n" + "    \"password\":\"2222\"\n" + "}");
//
//
//    when(request.getParameter("create")).thenReturn("true");
//
//
//    StringWriter stringWriter = new StringWriter();
//    PrintWriter writer = new PrintWriter(stringWriter);
//    when(response.getWriter()).thenReturn(writer);
//
//    User test =  new User();
//    test.setDocsPath(docsPath);
//    test.doPost(request, response);
//
//    writer.flush();
//    assertEquals("Make sure users.csv is empty, otherwise user creation failed",
//        "{\"message\":\"Successfully created new user\",\"status\":\"SUCCESS\"}", stringWriter.toString());
//
//    Boolean found = false;
//    Scanner scanner = new Scanner(new File(docsPath + "users.csv"));
//    while (scanner.hasNext()) {
//      String[] user = scanner.next().split(",");
//      if ("user22".equals(user[0])) {
//        found = true;
//      }
//    }
//    assertTrue(
//        "Make sure users.csv exists in the right directory, otherwise user was not written successfully",
//        found);
//    scanner.close();
//
//  }
//
//  @Test
//  public void testDoPostCreateUserInstructor() throws IOException {
//    File users = folder.newFile("users.csv");
//    docsPath = users.getParent().toString();
//
//    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);
//    HttpServletResponse response = mock(HttpServletResponse.class);
//    JSONObject json = mock(JSONObject.class);
//
//    BufferedReader bf = mock(BufferedReader.class);
//    Stream<String> str = mock(Stream.class);
//
//    when(request.getReader()).thenReturn(bf);
//    when(bf.lines()).thenReturn(str);
//    when(str.collect(any())).thenReturn("{\n" + "    \"userName\":\"user22\",\n"
//        + "    \"userType\":\"instructor\",\n" + "    \"password\":\"2222\"\n" + "}");
//
//
//    when(request.getParameter("create")).thenReturn("true");
//
//
//    StringWriter stringWriter = new StringWriter();
//    PrintWriter writer = new PrintWriter(stringWriter);
//    when(response.getWriter()).thenReturn(writer);
//
//    User test =  new User();
//    test.setDocsPath(docsPath);
//    test.doPost(request, response);
//
//    writer.flush();
//    assertEquals("Make sure users.csv is empty, otherwise user creation failed",
//        "{\"message\":\"Successfully created new user\",\"status\":\"SUCCESS\"}", stringWriter.toString());
//
//    Boolean found = false;
//    Scanner scanner = new Scanner(new File(docsPath + "users.csv"));
//    while (scanner.hasNext()) {
//      String[] user = scanner.next().split(",");
//      if ("user22".equals(user[0])) {
//        found = true;
//      }
//    }
//    assertTrue(
//        "Make sure users.csv exists in the right directory, otherwise user was not written successfully",
//        found);
//    scanner.close();
//  }


}

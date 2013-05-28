package br.usp.ime.cogroo.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.interceptor.download.FileDownload;
import br.usp.ime.cogroo.logic.TextSanitizer;

@Resource
public class SyntaxTreeController {
  
  private TextSanitizer sanitizer;

  private static final Logger LOG = Logger
      .getLogger(SyntaxTreeController.class);
  
  public SyntaxTreeController(TextSanitizer sanitizer) {
    this.sanitizer = sanitizer;
  }
  
  @Get
  @Path("/syntaxTree/{data*}")
  public Download tree(String data) throws Exception {
    data = sanitizer.sanitize(data, false, true);
    
    if(Strings.isNullOrEmpty(data)) {
      data = "[]";
    }
    
    File file = createTree(data);
    String contentType = "image/jpg";
    String filename = "stgraph.png";

    return new FileDownload(file, contentType, filename);
  }

  
  private static final String ISO = Charsets.ISO_8859_1.name(); 
  private static File createTree(String data) throws Exception {
    data = data.replace('_', ' ');
    String cookie;
    Pattern stgraph = Pattern.compile("(stgraph.png.*?)\"", Pattern.MULTILINE);
    String targetURL = "http://ccsl.ime.usp.br/cogroo/phpsyntaxtree/?";
    String urlParameters = 
        "data=" + URLEncoder.encode(data, ISO)
        + "&antialias=" + URLEncoder.encode("on", ISO) 
        //+ "&autosub=" + URLEncoder.encode("off", ISO) 
        + "&color=" + URLEncoder.encode("on", ISO) 
        + "&triangles=" + URLEncoder.encode("off", ISO)
        + "&fontsize=" + URLEncoder.encode("10", ISO);
    

    URL url;
    HttpURLConnection connection = null;
    // Create connection
    url = new URL(targetURL);
    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
        "application/x-www-form-urlencoded; charset=ISO-8859-1");

    connection.setRequestProperty("Content-Length",
        "" + Integer.toString(urlParameters.getBytes().length));
    connection.setRequestProperty("Content-Language", "pt-BR");

    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);
    // Send request
    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
    wr.writeBytes(urlParameters);
    wr.flush();
    wr.close();

    // Get Response
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    String line;
    StringBuffer response = new StringBuffer();
    while ((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    cookie = connection.getHeaderField("Set-Cookie");
    rd.close();
    String r = response.toString();
    connection.disconnect();

    Matcher m = stgraph.matcher(r);

    if (m.find()) {
      String path = m.group(1);

      // /
      URL imgUrl = new URL("http://ccsl.ime.usp.br/cogroo/phpsyntaxtree/"
          + path);

      InputStream input = null;
      HttpURLConnection urlConn = null;
      urlConn = (HttpURLConnection) (imgUrl.openConnection());
      urlConn.setRequestMethod("GET");
      // urlConn.setFollowRedirects(true);
      urlConn.setDoOutput(true);
      urlConn.setDoInput(true);
      urlConn.setUseCaches(false);
      urlConn.setAllowUserInteraction(false);
      urlConn.setRequestProperty("Cookie", cookie);
      input = urlConn.getInputStream();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int n = 0;
      while (-1 != (n = input.read(buf))) {
        out.write(buf, 0, n);
      }
      out.close();
      input.close();
      byte[] responseImg = out.toByteArray();
      File f = File.createTempFile("stgraph", ".png");
      f.deleteOnExit();
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(responseImg);
      fos.close();
      urlConn.disconnect();
      urlConn = null;
      return f;
    } else {
      return null;
    }
  }

}

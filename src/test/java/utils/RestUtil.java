package utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;

public class RestUtil {

	private static final Logger LOG = Logger.getLogger(RestUtil.class);

	public String get(String urlRoot, String path)
			throws IOException {
		
		return execute("GET", urlRoot, path, null);
	}

	public Map<String, String> post(String urlRoot, String path, Map<String, String> data)
			throws IOException {
		return br.usp.ime.cogroo.util.RestUtil.extractResponse(execute("POST", urlRoot, path, convert(data)));
	}

	public String execute(String method, String urlRoot, String path,
			String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = toUrl(urlRoot, path);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			
			
			StringBuilder sb = new StringBuilder();
			sb.append("CoGrOO/" + "3.0.7" + " ");
			sb.append("Java/" + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ") ");
			sb.append(System.getProperty("os.name") + "/" + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ") ");
			
			connection.setRequestProperty ( "User-agent", sb.toString());
			
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			if(urlParameters != null) {
				connection.setRequestProperty("Content-Length",
						"" + Integer.toString(urlParameters.getBytes().length));
				
				// Send request
				DataOutputStream wr = new DataOutputStream(
						connection.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
			}


			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {
			LOG.error("Communication error.", e);
			return null;
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private static URL toUrl(String urlRoot, String path)
			throws MalformedURLException {
		return new URL(urlRoot + "/" + path);
	}

	private static String convert(Map<String, String> data) {
		StringBuilder sb = new StringBuilder();
		for (String key : data.keySet()) {
			sb.append(encode(key) + "=" + encode(data.get(key)));
			sb.append("&");
		}
		return sb.subSequence(0, sb.length() - 1).toString();
	}

	private static String encode(String text) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}

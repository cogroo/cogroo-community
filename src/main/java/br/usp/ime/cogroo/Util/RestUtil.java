package br.usp.ime.cogroo.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestUtil {
	
	private static final Pattern responsePattern = Pattern.compile("\\$\\{(.*?)\\|(.*?)\\}&");
	
	// response have the format:
	// ${key|value}&
	// one response per line
	
	public static String prepareResponse(String key, String data) {
		return "${" + key + "|" + data + "}&";
	}
	
	public static Map<String, String> extractResponse(String response) {
		Map<String, String> data = new HashMap<String, String>();
		Matcher theMatcher = responsePattern.matcher(response);
		while(theMatcher.find()) {
			data.put(theMatcher.group(1), theMatcher.group(2));
		}
		return data;
	}
}

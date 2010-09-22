package br.usp.ime.cogroo.Util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class RestUtilTest {

	@Test
	public void testProcessResponse() throws IOException {
		Map<String,String> resp = RestUtil.extractResponse(getSampleText());
		
		assertEquals(resp.get("key1").length(), 182);
		assertEquals(resp.get("key2").length(), 182);
		assertEquals(resp.get("key3").length(), 182);
		assertEquals(resp.get("key4").length(), 182);
		assertEquals(resp.size(), 4);
	}
	
	private String getSampleText() throws IOException {
		File f = new File(getClass().getResource("sampleResponse.txt").getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		while(line != null) {
			sb.append(line + "\n");
			line = reader.readLine();
		}
		return sb.toString();
	}

}

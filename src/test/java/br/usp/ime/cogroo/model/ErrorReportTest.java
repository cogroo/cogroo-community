package br.usp.ime.cogroo.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class ErrorReportTest {
	ErrorReport williamError;
	ErrorReport wesleyError;
	
	@Before
	public void setup() {
		williamError = new ErrorReport( "sampleText", new ArrayList<Comment>(), "version", new User("William"), new Date(),new Date(), Boolean.TRUE, Boolean.TRUE);
		wesleyError = new ErrorReport( "sampleText", new ArrayList<Comment>(), "version", new User("Wesley"), new Date(),new Date(), Boolean.TRUE, Boolean.TRUE);
				
	}
	
	@Test
	public void testGetSampleText() {
		assertEquals("sampleText", williamError.getSampleText());
	}
	
	@Test
	public void testShouldHaveDifferentHashcode() {
		assertTrue(williamError.hashCode() != wesleyError.hashCode());
	}

}

package br.usp.ime.cogroo.model;

import org.junit.Test;
import static junit.framework.Assert.*;


public class UserTest {
	
	@Test
	public void shouldGetNameOfUser() {
		User william = new User("william");
		assertEquals("william", william.getLogin());
	
		User colen = new User("Colen");
		assertEquals("Colen", colen.getLogin());
			
	}
	
}

package br.usp.ime.cogroo.integration.stories.common;

import com.thoughtworks.selenium.Selenium;
import static junit.framework.Assert.*;

public class ThenAsserts {
	private final Selenium browser;
	
	public ThenAsserts(Selenium browser) {
		this.browser = browser;
	}
	
	public void iGetAnErrorWithDescription(String description) {
			System.out.println(description);
		assertTrue(browser.isTextPresent(description));
	}

	public void iMustBeLoggedInAs(String name) {
		assertTrue(browser.isTextPresent(name));	
		assertTrue(browser.isTextPresent("logout"));	
	}

	public void iMustNotBeLoggedIn() {
		assertFalse(browser.isTextPresent("logout"));	
	}

	public void theWordAppearsOnList(String string) {
		// TODO Auto-generated method stub
		
	}
}

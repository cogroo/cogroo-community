package br.usp.ime.cogroo.integration.stories.common;

import com.thoughtworks.selenium.Selenium;

public class WhenActions {
	private final Selenium browser;
	
	public WhenActions(Selenium browser) {
		this.browser = browser;
	}
	
	public void iSearchForWord(String word) {
		browser.type("word", word);
		browser.click("go");
	}

	public WhenActions iAmAtDictionaryEntrySearchPage() {
		browser.open("/dictionaryEntrySearch");
		return this;
	}
	
	public WhenActions and() {
		return this;
	}

	public void iSignUpAs(String username) {
		browser.click("link=Efetuar login");
		browser.type("user.name", username);
		browser.click("login");
	}

	public void iLogout() {
		browser.click("link=(logout)");
		
	}

	
}

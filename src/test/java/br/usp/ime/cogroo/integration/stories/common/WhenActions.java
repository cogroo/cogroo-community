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
		browser.click("//div[@id='loginlink']/p/a/b");
		browser.type("login", username);
		browser.click("//input[@value=' entrar » ']");
	}
	
	public void iRegisterAs(String username,
			String email,
			String password,
			String repeatPassword,
			String name,
			Boolean agree) {
		browser.click("//div[@id='loginlink']/p/a/b");
		browser.click("//div[@id='logform']/form/p/a[1]/b");
		browser.type("login", username);
		browser.type("email", email);
		browser.type("pw", password);
		browser.type("passwordRepeat", repeatPassword);
		browser.type("name", name);
		if(agree) {
			browser.click("iAgree");
		}
		browser.click("//input[@value=' Inscrever-se » ']");
	}

	public void iLogout() {
		browser.click("link=(logout)");
		
	}

	
}

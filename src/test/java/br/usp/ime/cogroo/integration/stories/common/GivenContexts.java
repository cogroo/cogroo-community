package br.usp.ime.cogroo.integration.stories.common;

import com.thoughtworks.selenium.Selenium;


public class GivenContexts {
	private final Selenium browser;
	
	public GivenContexts(Selenium browser) {
		this.browser = browser;
	}
	
	public void thereIsAnAnonymousUser() {
		
	}
	
	public GivenContexts and() {
		return this;
	}

	public GivenContexts theUserDoesntExist(String string) {
		
		return this;
	}

	public void iAmOnTheRootPage() {
		browser.open("/");
	}

	public GivenContexts thereisAnUserNamed(String string) {
		// TODO usuários ainda não sao salvos no bd, mas posteriormente este metodo iria criar ele.
		
		return this;
	}

	public void iAmLoggedAs(String username) {
		browser.open("/");
		browser.click("link=Efetuar login");
		browser.type("username", username);
		browser.click("login");
	}


}

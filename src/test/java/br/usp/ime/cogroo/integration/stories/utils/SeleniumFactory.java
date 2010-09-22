package br.usp.ime.cogroo.integration.stories.utils;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class SeleniumFactory {
	private final Selenium selenium;
	
	public SeleniumFactory() {
		this.selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080");
		this.selenium.start();
		this.selenium.windowMaximize();
		this.selenium.setSpeed("400");
	}
	
	public Selenium getBrowser() {
		return selenium;
	}
	
	public void close() {
		selenium.stop();
	}
}
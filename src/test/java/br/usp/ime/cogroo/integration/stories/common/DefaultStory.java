package br.usp.ime.cogroo.integration.stories.common;

import org.junit.After;
import org.junit.Before;

import br.usp.ime.cogroo.integration.stories.utils.SeleniumFactory;

import com.thoughtworks.selenium.Selenium;

public class DefaultStory {
	private SeleniumFactory factory;
	protected GivenContexts given;
	protected WhenActions when;
    protected ThenAsserts then;

    @Before
    public void setUp() {
    	factory = new SeleniumFactory();
    	Selenium browser = factory.getBrowser();

    	given = new GivenContexts(browser);
        when = new WhenActions(browser);
        then = new ThenAsserts(browser);
    }
    
    @After
    public void tearDown() {
    	factory.close();
    }
}

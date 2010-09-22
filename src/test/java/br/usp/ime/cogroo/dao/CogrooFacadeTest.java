package br.usp.ime.cogroo.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CogrooFacadeTest {

	private CogrooFacade theCogrooFacade;

	@Before
	public void before() {
		this.theCogrooFacade = new CogrooFacade(new DummyBaseDictionary());
		this.theCogrooFacade.setResources("target/cogroo");
		this.theCogrooFacade.setCogroo(new DummyCogroo());
	}

	@After
	public void after() {
		theCogrooFacade = null;
	}

	@Test
	public void testCouldGetFacade() {
		assertNotNull(this.theCogrooFacade);
	}

	@Test
	public void testGetMistakesHasSize() {
		List<String> mistakes = this.theCogrooFacade
				.getMistakes("A text with errors.");

		assertTrue(mistakes.size() > 0);
	}

	@Test
	public void testGetMistakesAssertFields() {
		List<String> mistakes = this.theCogrooFacade
				.getMistakes("A text with errors.");
		String m = mistakes.get(0);

		assertTrue(m.contains("Rule"));
		assertTrue(m.contains("Mistake"));
		assertTrue(m.contains("Short Message"));
	}

}

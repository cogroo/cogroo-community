package br.usp.ime.cogroo.model;

import junit.framework.Assert;

import org.junit.Test;


public class WordTest {

	@Test
	public void shouldBeEqualsIfWordisEqual() {
		Word word = new Word();
		word.setWord("palavra");

		Word expected = new Word();
		expected.setWord("palavra");

		Assert.assertEquals(expected, word);
	}
}

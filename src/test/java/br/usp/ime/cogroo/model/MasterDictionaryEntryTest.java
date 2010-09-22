package br.usp.ime.cogroo.model;

import junit.framework.Assert;

import org.junit.Test;

public class MasterDictionaryEntryTest {
	@Test
	public void shouldBeEqualsIfPrimitiveAndPostagAreEquals() {
		Word word = new Word();
		word.setWord("palavra");
		PosTag postag = new PosTag("tag 1");
		
		DictionaryEntry masterDictionaryEntry = new DictionaryEntry();
		masterDictionaryEntry.setLemma(word);
		masterDictionaryEntry.setPosTag(postag);

		DictionaryEntry expected = new DictionaryEntry();
		expected.setLemma(word);
		expected.setPosTag(postag);
		
		Assert.assertEquals(expected, masterDictionaryEntry);
	}
}

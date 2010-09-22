package br.usp.ime.cogroo.integration.stories;

import org.junit.Test;

import br.usp.ime.cogroo.integration.stories.common.DefaultStory;

public class DictionaryEntryStory extends DefaultStory {
	
	@Test
	public void searchInvalidWord() {
		given.thereIsAnAnonymousUser();
		when.iAmAtDictionaryEntrySearchPage().and().iSearchForWord("palavra_invalida");
		then.iGetAnErrorWithDescription("Não foi possível encontrar a palavra");
	}	
}

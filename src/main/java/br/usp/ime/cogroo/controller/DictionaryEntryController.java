package br.usp.ime.cogroo.controller;

import java.util.List;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.logic.DictionaryManager;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.model.NicePrintDictionaryEntry;

@Resource
public class DictionaryEntryController {

	private DictionaryManager dictionaryManager;
	private Result result;
	private TextSanitizer sanitizer;
	private static final Logger LOG = Logger
			.getLogger(DictionaryEntryController.class);
	
	private static final String HEADER_TITLE = "Léxico";
	private static final String HEADER_DESCRIPTION = "Busca uma palavra no dicionário léxico do corretor CoGrOO.";

	public DictionaryEntryController(DictionaryManager dictionaryManager,
			Result result,
			TextSanitizer sanitizer) {
	this.dictionaryManager = dictionaryManager;
		this.result = result;
		this.sanitizer = sanitizer;
	}

	@Get
	@Path("/dictionary")
	public void list() {
		LOG.debug("Will list of size: "
				+ dictionaryManager.listDictionaryEntries().size());
		result.include("dictionaryEntryList", dictionaryManager
				.listDictionaryEntriesForUser());
	}

	@Get
	@Path("/dictionary/search")
	public void search() {
		result.include("word", "casa");
		result.include("headerTitle", HEADER_TITLE).include("headerDescription",
				HEADER_DESCRIPTION);
	}

	@Get
	@Path("/dictionary/search/{word}")
	public void search(String word) {
		word = sanitizer.sanitize(word, false);
		if (word != null && word.length() > 0) {
			List<NicePrintDictionaryEntry> dictionaryEntry = dictionaryManager
					.searchWordAndLemma(word);
			result.include("dictionaryEntryList", dictionaryEntry).include(
					"word", word);
		}
		result.include("headerTitle", HEADER_TITLE).include("headerDescription",
				HEADER_DESCRIPTION);
	}
}

package br.usp.ime.cogroo.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.DictionaryManager;
import br.usp.ime.cogroo.logic.EditPosTagLogic;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.NicePrintDictionaryEntry;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.Word;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.tag.LegacyTagInterpreter;

@Resource
public class DictionaryEntryController {

	private DictionaryManager dictionaryManager;
	private EditPosTagLogic editPosTagLogic;
	private Result result;
	private Validator validator;
	private LoggedUser loggedUser;
	private ApplicationData appData;
	private TextSanitizer sanitizer;
	private static final Logger LOG = Logger
			.getLogger(DictionaryEntryController.class);

	public DictionaryEntryController(DictionaryManager dictionaryManager,
			Result result, Validator validator, LoggedUser loggedUser,
			EditPosTagLogic editPosTagLogic, ApplicationData appData,
			TextSanitizer sanitizer) {
	this.dictionaryManager = dictionaryManager;
		this.editPosTagLogic = editPosTagLogic;
		this.result = result;
		this.validator = validator;
		this.loggedUser = loggedUser;
		this.appData = appData;
		this.sanitizer = sanitizer;
	}

	@Get
	@Path("/dictionaryEntries")
	public void list() {
		LOG.debug("Will list of size: "
				+ dictionaryManager.listDictionaryEntries().size());
		result.include("dictionaryEntryList", dictionaryManager
				.listDictionaryEntriesForUser());
	}

	@Get
	@Path("/dictionaryEntry")
	public void add() {
		if (!loggedUser.isLogged()) {
			LOG.error("Unknown user trying to add dictionaryEntry.");
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, ExceptionMessages.ERROR));
		}

		validator.onErrorUse(Results.page()).of(LoginController.class).login();

		result.include("typeFieldList", editPosTagLogic.listTypeFields());
	}

	@Post
	@Path("/dictionaryEntry")
	public void add(DictionaryEntry dictionaryEntry, List<String> fieldList,
			String tagClass) {
		Word word = dictionaryEntry.getWord();
		Word lemma = dictionaryEntry.getLemma();
		word.setWord(sanitizer.sanitize(word.getWord(), false));
		lemma.setWord(sanitizer.sanitize(lemma.getWord(), false));

		if (!dictionaryEntry.isValid()) {
			validator.add(new ValidationMessage(ExceptionMessages.INVALID_ENTRY,
					ExceptionMessages.EMPTY_FIELD));
		}
		if (tagClass == null || tagClass.length() == 0) {
			validator.add(new ValidationMessage(ExceptionMessages.MISSING_CLASS_TAG,
					ExceptionMessages.EMPTY_FIELD));
		}
		if (validator.hasErrors()) {
			result.include("typeFieldList", editPosTagLogic.listTypeFields());
		}
		validator.onErrorUse(Results.page())
				.of(DictionaryEntryController.class).add();

		
		MorphologicalTag mTag = editPosTagLogic.getMorphologicalTag(fieldList,
				tagClass);
		LegacyTagInterpreter tagInterpreter = new LegacyTagInterpreter();
		String tag = tagInterpreter.serialize(mTag);
		PosTag posTag = new PosTag(tag);
		dictionaryEntry.setPosTag(posTag);

		try {
			dictionaryManager.add(dictionaryEntry);
			appData.incDictionaryEntries();
			result.include("justAddedDictionaryEntry", true).include("login", loggedUser.getUser().getLogin());
			result.redirectTo(DictionaryEntryController.class).list();
		} catch (Exception e) {
			LOG.error("Couldn't add dictionaryEntry: " + dictionaryEntry, e);
			validator.add(new ValidationMessage(e.getMessage(), ExceptionMessages.ERROR));

			result.include("dictionaryEntry", dictionaryEntry).include("typeFieldList", editPosTagLogic.listTypeFields());
			validator.onErrorUse(Results.page()).of(DictionaryEntryController.class).add();

		}

	}

	@Get
	@Path("/dictionaryEntrySearch")
	public void search() {
		result.include("word", "casa");
	}

	@Post
	@Path("/dictionaryEntrySearch")
	public void search(String word) {
		word = sanitizer.sanitize(word, false);
		if (word != null && word.length() > 0) {
			List<NicePrintDictionaryEntry> dictionaryEntry = dictionaryManager
					.searchWordAndLemma(word);
			result.include("dictionaryEntryList", dictionaryEntry).include(
					"word", word);
		}
	}

	@Delete
	@Path("/dictionaryEntryDelete")
	public void delete(List<String> listaWords) throws Exception {
		String[] item = null;
		if (listaWords != null) {
			for (String entry : listaWords) {
				item = entry.split("-");
				Word word = new Word(item[0]);
				Word lemma = new Word(item[1]);
				PosTag posTag = new PosTag(item[2]);
				LOG.debug("Got item to delete: " + Arrays.toString(item));
				DictionaryEntry dictionaryEntry = new DictionaryEntry(word,
						lemma, posTag);
				LOG.debug("Will delete dictionaryEntry: "
						+ dictionaryEntry.toString());
				dictionaryManager.delete(dictionaryEntry);
				appData.decDictionaryEntries();
			}

		}

		result.redirectTo(DictionaryEntryController.class).search();
	}

	@Post
	@Path("/dictionaryEntryEdit")
	public void edit(List<String> listaWords) {

	}
}

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
import br.usp.ime.cogroo.exceptions.Messages;
import br.usp.ime.cogroo.logic.DictionaryManager;
import br.usp.ime.cogroo.logic.EditPosTagLogic;
import br.usp.ime.cogroo.logic.Stats;
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
	private static final Logger LOG = Logger
			.getLogger(DictionaryEntryController.class);
	
	//TODO Dependência parece ser necessária. Aqui é o melhor lugar?
	private Stats stats;

	public DictionaryEntryController(DictionaryManager dictionaryManager,
			Result result, Validator validator, LoggedUser loggedUser,
			EditPosTagLogic editPosTagLogic, Stats stats) {
		this.dictionaryManager = dictionaryManager;
		this.editPosTagLogic = editPosTagLogic;
		this.result = result;
		this.validator = validator;
		this.loggedUser = loggedUser;
		this.stats = stats;
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
					Messages.ONLY_LOGGED_USER_CAN_DO_THIS, Messages.ERROR));
		}

		validator.onErrorUse(Results.page()).of(LoginController.class).login();

		result.include("typeFieldList", editPosTagLogic.listTypeFields());
	}

	@Post
	@Path("/dictionaryEntry")
	public void add(DictionaryEntry dictionaryEntry, List<String> fieldList,
			String tagClass) {

		if (!dictionaryEntry.isValid()) {
			validator.add(new ValidationMessage(Messages.INVALID_ENTRY,
					Messages.EMPTY_FIELD));
		}
		if (tagClass == null || tagClass.length() == 0) {
			validator.add(new ValidationMessage(Messages.MISSING_CLASS_TAG,
					Messages.EMPTY_FIELD));
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
			result.redirectTo(DictionaryEntryController.class).list();
		} catch (Exception e) {
			LOG.error("Couldn't add dictionaryEntry: " + dictionaryEntry, e);
			validator.add(new ValidationMessage(e.getMessage(), Messages.ERROR));

			result.include("dictionaryEntry", dictionaryEntry).include("typeFieldList", editPosTagLogic.listTypeFields());
			validator.onErrorUse(Results.page()).of(DictionaryEntryController.class).add();

		}

	}

	@Get
	@Path("/dictionaryEntrySearch")
	public void search() {
		result.include("totalMembers", stats.getTotalMembers())
				.include("onlineMembers", stats.getOnlineMembers())
				.include("reportedErrors", stats.getReportedErrors());
	}

	@Post
	@Path("/dictionaryEntrySearch")
	public void search(String word) {
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
			}

		}

		result.redirectTo(DictionaryEntryController.class).search();
	}

	@Post
	@Path("/dictionaryEntryEdit")
	public void edit(List<String> listaWords) {

	}
}

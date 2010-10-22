package br.usp.ime.cogroo.controller;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.usp.ime.cogroo.dao.DictionaryEntryDAO;
import br.usp.ime.cogroo.dao.DictionaryEntryUserDAO;
import br.usp.ime.cogroo.dao.PosTagDAO;
import br.usp.ime.cogroo.dao.WordDAO;
import br.usp.ime.cogroo.exceptions.Messages;
import br.usp.ime.cogroo.logic.DictionaryManager;
import br.usp.ime.cogroo.logic.EditPosTagLogic;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.Word;

public class DictionaryEntryControllerTest {

	private MockResult result;
	private DictionaryEntryController dictionaryEntryController;

	@Before
	public void setUp() {
		result = new MockResult();

		EntityManager em = HSQLDBEntityManagerFactory.createEntityManager();

		LoggedUser loggedUser = new LoggedUser();
		loggedUser.setUser(new User("cogroo"));
		DictionaryEntryDAO dictionaryEntryDAO = new DictionaryEntryDAO(em);
		DictionaryEntryUserDAO dictionaryEntryUserDAO = new DictionaryEntryUserDAO(
				em);
		WordDAO wordDAO = new WordDAO(em);
		PosTagDAO postagDAO = new PosTagDAO(em);
		DictionaryManager logic = new DictionaryManager(loggedUser,
				dictionaryEntryDAO, dictionaryEntryUserDAO, wordDAO, postagDAO);

		Validator validator = new MockValidator();
		EditPosTagLogic logic2 = new EditPosTagLogic();
		dictionaryEntryController = new DictionaryEntryController(logic,
				result, validator, loggedUser, logic2, null);

	}

	@Test
	public void shouldNotInsertEmptyDictionaryEntry() {
		DictionaryEntry dictionaryEntry = getDictionaryEntry("","","");
		checkError(dictionaryEntry, "");
	}

	@Test
	public void shouldNotInsertDictionaryEntryWithEmptyWord() {
		DictionaryEntry dictionaryEntry = getDictionaryEntry("", "casa","");
		checkError(dictionaryEntry, "PREPOSITION");
	}

	

	@Test
	public void shouldNotInsertDictionaryEntryWithEmptyLemma() {
		DictionaryEntry dictionaryEntry = getDictionaryEntry("casa", "","");
		checkError(dictionaryEntry, "PREPOSITION");
	}

	@Test
	public void shouldNotInsertDictionaryEntryWithEmptyPosTag() {
		DictionaryEntry dictionaryEntry = getDictionaryEntry("casa", "casa","");

		try {
			dictionaryEntryController.add(dictionaryEntry, null, "");
			Assert.fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			Assert.assertEquals(1, errors.size());
			Message message = errors.get(0);
			Assert.assertEquals(Messages.MISSING_CLASS_TAG, message.getMessage());
		}
	}

	@Test
	public void shouldNotInsertDictionaryEntryWithEmptyWordLemma() {
		DictionaryEntry dictionaryEntry = getDictionaryEntry("", "", "");		
		checkError(dictionaryEntry, "PREPOSITION");
	}

	private DictionaryEntry getDictionaryEntry(String w, String l, String p) {
		Word word = new Word(w);
		Word lemma = new Word(l);
		PosTag posTag = new PosTag(p);
		return new DictionaryEntry(word, lemma, posTag);

	}
	
	private void checkError(DictionaryEntry dictionaryEntry, String classe) {
		try {
			dictionaryEntryController.add(dictionaryEntry, null, classe);
			Assert.fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			Assert.assertTrue(errors.size() > 0);
			Message message = errors.get(0);
			Assert.assertEquals(Messages.INVALID_ENTRY, message.getMessage());
			Assert.assertEquals(Messages.EMPTY_FIELD, message.getCategory());
		}
	}
}

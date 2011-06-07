package br.usp.ime.cogroo.controller;

import javax.persistence.EntityManager;

import org.junit.Before;

import utils.HSQLDBEntityManagerFactory;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.usp.ime.cogroo.dao.DictionaryEntryDAO;
import br.usp.ime.cogroo.dao.DictionaryEntryUserDAO;
import br.usp.ime.cogroo.dao.PosTagDAO;
import br.usp.ime.cogroo.dao.WordDAO;
import br.usp.ime.cogroo.logic.DictionaryManager;
import br.usp.ime.cogroo.logic.EditPosTagLogic;
import br.usp.ime.cogroo.logic.TextSanitizer;
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

		LoggedUser loggedUser = new LoggedUser(null);
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
				result, null, new TextSanitizer());

	}


	private DictionaryEntry getDictionaryEntry(String w, String l, String p) {
		Word word = new Word(w);
		Word lemma = new Word(l);
		PosTag posTag = new PosTag(p);
		return new DictionaryEntry(word, lemma, posTag);

	}
}

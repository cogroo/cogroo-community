package br.usp.ime.cogroo.logic.errorreport;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import utils.ResourcesUtil;
import br.usp.ime.cogroo.CommunityException;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.DummyBaseDictionary;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerBadInterventionDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerOmissionDAO;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;

public class ErrorEntryLogicTest {
	
	User wesley;
	User william;
	EntityManager em;
	ErrorEntryLogic mErrorEntryLogic;

	@Before
	public void setUp() throws Exception {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		wesley = new User("Wesley");
		william = new User("William");
		
		UserDAO userDAO = new UserDAO(em);
		em.getTransaction().begin();
		userDAO.add(william);
		userDAO.add(wesley);
		em.getTransaction().commit();
		
		LoggedUser lu = new LoggedUser();
		lu.setUser(william);
		
		CogrooFacade facade = new CogrooFacade(new DummyBaseDictionary());
		facade.setResources("target/cogroo");
		
		mErrorEntryLogic = new ErrorEntryLogic(
				lu, new ErrorEntryDAO(em), 
				new UserDAO(em), 
				new CommentDAO(em), 
				facade,
				new GrammarCheckerVersionDAO(em),
				new GrammarCheckerOmissionDAO(em),
				new GrammarCheckerBadInterventionDAO(em));
	}

	@Test(expected=CommunityException.class)
	public void testAddErrorEntryInvalidUser() throws CommunityException {
		mErrorEntryLogic.addErrorEntry("invaliduser", "");
	}
	
	@Test
	public void testAddErrorEntry() throws CommunityException, IOException {
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getName(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();
		assertTrue(list.size() > 0);
		assertNotNull(list.get(0).getId());
		assertNotNull(list.get(0).getVersion().getId());
		assertNotNull(list.get(0).getComments().get(0).getId());
		assertNotNull(list.get(0).getOmission().getId());
	}
		

}

package br.usp.ime.cogroo.logic.errorreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import utils.ResourcesUtil;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.DummyBaseDictionary;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
import br.usp.ime.cogroo.dao.HistoryEntryDAO;
import br.usp.ime.cogroo.dao.HistoryEntryFieldDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerBadInterventionDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerOmissionDAO;
import br.usp.ime.cogroo.exceptions.CommunityException;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;

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
		
		LoggedUser lu = new LoggedUser(null);
		lu.setUser(william);
		
		CogrooFacade facade = new CogrooFacade(new DummyBaseDictionary());
		facade.setResources("target/cogroo/gc");
		
		ApplicationData appdata = mock(ApplicationData.class);
		
		mErrorEntryLogic = new ErrorEntryLogic(
				lu, new ErrorEntryDAO(em), 
				new UserDAO(em), 
				new CommentDAO(em), 
				facade,
				new GrammarCheckerVersionDAO(em),
				new GrammarCheckerOmissionDAO(em),
				new GrammarCheckerBadInterventionDAO(em),
				new HistoryEntryDAO(em),
				new HistoryEntryFieldDAO(em),
				appdata,
				null,
				null,
				null,
				new RulesLogic(facade));
	}

	@Test(expected=CommunityException.class)
	public void testAddErrorEntryInvalidUser() throws CommunityException {
		mErrorEntryLogic.addErrorEntry("invaliduser", "");
	}
	
	@Test
	public void testAddErrorEntry() throws CommunityException, IOException {
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();
		assertTrue(list.size() > 0);
		assertNotNull(list.get(0).getId());
		assertNotNull(list.get(0).getVersion().getId());
		assertNotNull(list.get(0).getComments().get(0).getId());
		assertNotNull(list.get(0).getOmission().getId());
	}
	
	@Test
	public void testAddComment() throws CommunityException, IOException {
		
		
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();

		Long errorID = list.get(0).getId();
		
		em.getTransaction().begin();
		Long commentID = mErrorEntryLogic.addCommentToErrorEntry(errorID, wesley.getId(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();
		
		em.getTransaction().begin();
		mErrorEntryLogic.addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();
		
		ErrorEntryDAO dao = new ErrorEntryDAO(em);
		
		ErrorEntry error = dao.retrieve(new Long(errorID));
		
		List<Comment> comments = error.getComments();
		
		assertEquals(2, comments.size());
		
		assertEquals(william, comments.get(0).getUser()); // the submitter
		assertEquals(wesley, error.getComments().get(1).getUser()); // wesley added the first comment
		assertEquals("a comment", comments.get(1).getComment()); // the comment from wesley
		
		assertEquals(1, comments.get(1).getAnswers().size());
	}
		
	@Test
	public void testDeleteAnswer() throws CommunityException, IOException {
		
		
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();

		Long errorID = list.get(0).getId();
		
		em.getTransaction().begin();
		Long commentID = mErrorEntryLogic.addCommentToErrorEntry(errorID, wesley.getId(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();
		
		em.getTransaction().begin();
		mErrorEntryLogic.addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();
		
		ErrorEntryDAO dao = new ErrorEntryDAO(em);
		
		ErrorEntry error = dao.retrieve(new Long(errorID));
		
		List<Comment> comments = error.getComments();
		
		assertEquals(2, comments.size());
		
		assertEquals(william, comments.get(0).getUser()); // the submitter
		assertEquals(wesley, error.getComments().get(1).getUser()); // wesley added the first comment
		assertEquals("a comment", comments.get(1).getComment()); // the comment from wesley
		
		assertEquals(1, comments.get(1).getAnswers().size());
		
		
		
		em.getTransaction().begin();
		mErrorEntryLogic.removeAnswer(comments.get(1).getAnswers().get(0), comments.get(1));// addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();
		
		ErrorEntry error1 = dao.retrieve(new Long(errorID));
		
		assertEquals(0, error1.getComments().get(1).getAnswers().size());
	}
	
	@Test
	public void testSetPriority() throws CommunityException, IOException {
		
		
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();

		Long errorID = list.get(0).getId();
		
		assertEquals(Priority.NORMAL, list.get(0).getPriority());
		
		em.getTransaction().begin();
		mErrorEntryLogic.setPriority(list.get(0), Priority.IMMEDIATE);
		em.getTransaction().commit();
		
		
		ErrorEntryDAO dao = new ErrorEntryDAO(em);
		
		ErrorEntry error = dao.retrieve(new Long(errorID));
		
		assertEquals(Priority.IMMEDIATE, error.getPriority());
		
		assertEquals(1, error.getHistoryEntries().size());
		
		
	}
	
	@Test
	public void testSetState() throws CommunityException, IOException {
		
		
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();

		Long errorID = list.get(0).getId();
		
		assertEquals(State.OPEN, list.get(0).getState());
		
		em.getTransaction().begin();
		mErrorEntryLogic.setState(list.get(0), State.CLOSED);
		em.getTransaction().commit();
		
		
		ErrorEntryDAO dao = new ErrorEntryDAO(em);
		
		ErrorEntry error = dao.retrieve(new Long(errorID));
		
		assertEquals(State.CLOSED, error.getState());
		
		assertEquals(Priority.NORMAL, list.get(0).getPriority());
		
		em.getTransaction().begin();
		mErrorEntryLogic.setPriority(list.get(0), Priority.IMMEDIATE);
		em.getTransaction().commit();
		
		
		assertEquals(Priority.IMMEDIATE, error.getPriority());
		
		assertEquals(2, error.getHistoryEntries().size());
	}
	
	@Test
	public void testSetStateAndPriority() throws CommunityException, IOException {
		
		
		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry(william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		em.getTransaction().commit();

		Long errorID = list.get(0).getId();
		
		assertEquals(State.OPEN, list.get(0).getState());
		
		em.getTransaction().begin();
		mErrorEntryLogic.setState(list.get(0), State.CLOSED);
		em.getTransaction().commit();
		
		
		ErrorEntryDAO dao = new ErrorEntryDAO(em);
		
		ErrorEntry error = dao.retrieve(new Long(errorID));
		
		assertEquals(State.CLOSED, error.getState());
	}
}

package br.usp.ime.cogroo.logic.errorreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import utils.ResourcesUtil;
import br.usp.ime.cogroo.dao.CogrooFacade;
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
import br.usp.ime.cogroo.logic.StringTemplateUtil;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.HistoryEntry;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;
import br.usp.ime.cogroo.notifiers.Notificator;

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

		CogrooFacade facade = new CogrooFacade();

		ApplicationData appdata = mock(ApplicationData.class);

		ServletContext mockServletContext = mock(ServletContext.class);
		String path = ErrorEntryLogicTest.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../src/main/webapp/stringtemplates";
		when(mockServletContext.getRealPath("/stringtemplates")).thenReturn(path);

		Notificator mockNotificato = mock(Notificator.class);

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
				mockNotificato,
				new StringTemplateUtil(mockServletContext),
				new TextSanitizer(),
				new RulesLogic(facade));
	}

	@Test(expected=CommunityException.class)
	public void testAddErrorEntryInvalidUser() throws CommunityException {
		mErrorEntryLogic.addErrorEntry("invaliduser", "");
	}

	@Test
	public void testAddErrorEntry() throws CommunityException, IOException {

	  List<ErrorEntry> list = createAndCommitErrorEntryList();

		assertTrue(list.size() > 0);
		assertNotNull(list.get(0).getId());
		assertNotNull(list.get(0).getVersion().getId());
		assertNotNull(list.get(0).getOmission().getId());
	}

	@Test
	public void testAddComment() throws CommunityException, IOException {


		em.getTransaction().begin();
		List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry("cogroo", william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
		list.get(0).setHistoryEntries(new ArrayList<HistoryEntry>());
		list.get(0).setComments(new ArrayList<Comment>());
		em.getTransaction().commit();

		Long errorID = list.get(0).getId();

		em.getTransaction().begin();
		Long commentID = mErrorEntryLogic.addCommentToErrorEntry(errorID, wesley.getId(), "a comment", false);// addComment(errorID, newComment);
		em.getTransaction().commit();

		em.getTransaction().begin();
		mErrorEntryLogic.addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();

		ErrorEntryDAO dao = new ErrorEntryDAO(em);

		ErrorEntry error = dao.retrieve(new Long(errorID));

		List<Comment> comments = error.getComments();

		assertEquals(1, comments.size());
		assertEquals(1, comments.get(0).getAnswers().size());

		assertEquals(wesley, comments.get(0).getUser()); // the submitter
		assertEquals(william, comments.get(0).getAnswers().get(0).getUser()); // wesley added the first comment
		assertEquals("a comment", comments.get(0).getComment()); // the comment from wesley
		assertEquals("a answer", comments.get(0).getAnswers().get(0).getComment()); // the answer

	}

	@Test
	public void testUserErrorsCommentsCount() throws CommunityException, IOException {


	        em.getTransaction().begin();
	        List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry("cogroo", william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
	        list.get(0).setHistoryEntries(new ArrayList<HistoryEntry>());
	        list.get(0).setComments(new ArrayList<Comment>());
	        em.getTransaction().commit();

	        Long errorID = list.get(0).getId();

	        em.getTransaction().begin();
	        Long commentID = mErrorEntryLogic.addCommentToErrorEntry(errorID, wesley.getId(), "a comment", false);// addComment(errorID, newComment);
	        em.getTransaction().commit();

	        em.getTransaction().begin();
	        mErrorEntryLogic.addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
	        em.getTransaction().commit();

	        ErrorEntryDAO dao = new ErrorEntryDAO(em);
	        CommentDAO cdao = new CommentDAO(em);

	        assertEquals(4, dao.count(william));
	        assertEquals(0, dao.count(wesley));

	        assertEquals(5, cdao.count(william));
	        assertEquals(1, cdao.count(wesley));
	    }

	@Test
	public void testDeleteAnswer() throws CommunityException, IOException {


	    List<ErrorEntry> list = createAndCommitErrorEntryList();

		Long errorID = list.get(0).getId();

		em.getTransaction().begin();
		Long commentID = mErrorEntryLogic.addCommentToErrorEntry(errorID, wesley.getId(), "a comment", false);// addComment(errorID, newComment);
		em.getTransaction().commit();

		em.getTransaction().begin();
		mErrorEntryLogic.addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();

		ErrorEntryDAO dao = new ErrorEntryDAO(em);

		ErrorEntry error = dao.retrieve(new Long(errorID));

		List<Comment> comments = error.getComments();

		assertEquals(1, comments.size());

		assertEquals(wesley, comments.get(0).getUser()); // the submitter
		assertEquals(william, error.getComments().get(0).getAnswers().get(0).getUser()); // wesley added the first comment

		assertEquals(1, comments.get(0).getAnswers().size());


		em.getTransaction().begin();
		mErrorEntryLogic.removeAnswer(comments.get(0).getAnswers().get(0), comments.get(0));// addAnswerToComment(commentID, william.getId(), "a answer");// (errorID, wesley.getId().intValue(), "a comment");// addComment(errorID, newComment);
		em.getTransaction().commit();

		ErrorEntry error1 = dao.retrieve(new Long(errorID));

		assertEquals(0, error1.getComments().get(0).getAnswers().size());
	}

	@Test
	public void testSetPriority() throws CommunityException, IOException {


	  List<ErrorEntry> list = createAndCommitErrorEntryList();

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


	  List<ErrorEntry> list = createAndCommitErrorEntryList();

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


	  List<ErrorEntry> list = createAndCommitErrorEntryList();

		Long errorID = list.get(0).getId();

		assertEquals(State.OPEN, list.get(0).getState());

		em.getTransaction().begin();
		mErrorEntryLogic.setState(list.get(0), State.CLOSED);
		em.getTransaction().commit();


		ErrorEntryDAO dao = new ErrorEntryDAO(em);

		ErrorEntry error = dao.retrieve(new Long(errorID));

		assertEquals(State.CLOSED, error.getState());
	}

	private List<ErrorEntry> createAndCommitErrorEntryList() throws CommunityException, IOException {
	     em.getTransaction().begin();
	        List<ErrorEntry> list = mErrorEntryLogic.addErrorEntry("cogroo", william.getLogin(), ResourcesUtil.getResourceAsString(getClass(), "/br/usp/ime/cogroo/logic/ErrorReport1.xml"));
	        list.get(0).setHistoryEntries(new ArrayList<HistoryEntry>());
	        list.get(0).setComments(new ArrayList<Comment>());
	        em.getTransaction().commit();

	        return list;
	}
}

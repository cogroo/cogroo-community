package br.usp.ime.cogroo.dao.errorreport;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;

public class ErrorEntryDAOTest {

	private EntityManager em;
	private ErrorEntryDAO errorReportDAO;

	private User wesley;
	private User william;
	GrammarCheckerVersion version;

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		errorReportDAO = new ErrorEntryDAO(em);
		populate();
	}

	private void populate() {

		william = new User("William");
		wesley = new User("Wesley");

		GrammarCheckerVersionDAO versionDAO = new GrammarCheckerVersionDAO(em);

		GrammarCheckerVersion version= null;

		UserDAO userDAO = new UserDAO(em);

		em.getTransaction().begin();
		userDAO.add(william);
		userDAO.add(wesley);
		version = versionDAO.retrieve("0.0.1-SNAPSHOT");
		em.getTransaction().commit();

		ErrorEntry error1 = new ErrorEntry(
				"A sample text",
				1, 4,
				null,
				version,
				william,
				new Date(),
				new Date(),
				null,
				null,
				null,
				null);
		ErrorEntry error2 = new ErrorEntry(
				"A sample text",
				1, 4,
				null,
				version,
				wesley,
				new Date(),
				new Date(),
				null,
				null,
				State.CLOSED,
				Priority.IMMEDIATE);

		em.getTransaction().begin();
		errorReportDAO.add(error1);
		errorReportDAO.add(error2);
		em.getTransaction().commit();
	}

	@Test
	public void testCanGetError() {
		List<ErrorEntry> reports =  errorReportDAO.listAll();
		assertEquals(2, reports.size());
	}

	@Test
	public void testPriority() {
		List<ErrorEntry> reports =  errorReportDAO.listAll();

		assertEquals(Priority.NORMAL, reports.get(0).getPriority());
		assertEquals(State.OPEN, reports.get(0).getState());

		assertEquals(Priority.IMMEDIATE, reports.get(1).getPriority());
		assertEquals(State.CLOSED, reports.get(1).getState());
	}

	@Test
	public void testPriority2() {
		List<ErrorEntry> reports =  errorReportDAO.listAll();

		reports.get(0).setPriority(Priority.HIGH);
		reports.get(0).setState(State.FEEDBACK);

		em.getTransaction().begin();
		errorReportDAO.update(reports.get(0));
		em.getTransaction().commit();

		List<ErrorEntry> reports1 =  errorReportDAO.listAll();
		assertEquals(Priority.HIGH, reports1.get(0).getPriority());
		assertEquals(State.FEEDBACK, reports1.get(0).getState());
	}

}

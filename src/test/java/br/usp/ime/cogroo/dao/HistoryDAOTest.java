package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.HistoryEntry;
import br.usp.ime.cogroo.model.errorreport.HistoryEntryField;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;

public class HistoryDAOTest {
	private EntityManager em;
	private UserDAO userDAO;
	private HistoryEntryDAO historyDAO;
	private HistoryEntryFieldDAO historyEntryDAO;
	private User robson;
	private User wesley;
	private ErrorEntryDAO errorReportDAO;
	GrammarCheckerVersion version;

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		userDAO = new UserDAO(em);
		historyDAO = new HistoryEntryDAO(em);
		historyEntryDAO = new HistoryEntryFieldDAO(em);
		errorReportDAO = new ErrorEntryDAO(em);
		populateWithUsers();
		populate();
	}

	private void populateWithUsers() {
		
		robson = new User("Robson");
		
		wesley = new User("Wesley");
		
		em.getTransaction().begin();
		userDAO.add(robson);
		userDAO.add(wesley);
		em.getTransaction().commit();
		
	}
	
private void populate() {
		
		GrammarCheckerVersionDAO versionDAO = new GrammarCheckerVersionDAO(em);
		
		GrammarCheckerVersion version= null;
		
		
		em.getTransaction().begin();
		version = versionDAO.retrieve("0.0.1-SNAPSHOT");
		em.getTransaction().commit();
		
		ErrorEntry error1 = new ErrorEntry(
				"A sample text", 
				1, 4,
				null, 
				version, 
				robson, 
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

	@After
	public void tearDown() {
		em.close();
	}

	@Test
	public void testCreteHistory() {
		List<HistoryEntryField> l = new ArrayList<HistoryEntryField>();
		HistoryEntry h = new HistoryEntry(wesley, new Date(), l, errorReportDAO.listAll().get(0));
		
		HistoryEntryField he0 = new HistoryEntryField(h, "fieldName0", "before0", "after0", true);
		HistoryEntryField he1 = new HistoryEntryField(h, "fieldName1", "before1", "after1", false);
		
		l.add(he0);
		l.add(he1);
		
		em.getTransaction().begin();
		historyEntryDAO.add(he0);
		historyEntryDAO.add(he1);
		historyDAO.add(h);
		em.getTransaction().commit();
		
		HistoryEntry hist = historyDAO.retrieve(h.getId());
		
		assertEquals(2, hist.getHistoryEntryField().size());
		
		assertTrue(hist.getHistoryEntryField().get(0).getIsFormatted());
		assertFalse(hist.getHistoryEntryField().get(1).getIsFormatted());
		
	}
	
}

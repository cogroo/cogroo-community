package br.usp.ime.cogroo.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.CommentDAO;
import br.usp.ime.cogroo.dao.ErrorReportDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.Comment;
import br.usp.ime.cogroo.model.ErrorReport;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;

public class ErrorReportLogicTest {
	
	private EntityManager em;
	private ErrorReportLogic errorReportLogic;
	
	private User wesley;
	private User william;
	
	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		ErrorReportDAO errorReportDAO = new ErrorReportDAO(em);
		UserDAO userDAO = new UserDAO(em);
		
		william = new User("William");
		wesley = new User("Wesley");
		
		CommentDAO commentDAO = new CommentDAO(em);
		
		em.getTransaction().begin();
		userDAO.add(william);
		userDAO.add(wesley);
		em.getTransaction().commit();
		
		LoggedUser lu = new LoggedUser();
		lu.setUser(william);
		
		errorReportLogic = new ErrorReportLogic(lu, errorReportDAO, userDAO, commentDAO, new CogrooFacade(null));
		
	}

	@Test
	public void testAddErrorEntryWithComment() {
		em.getTransaction().begin();
		errorReportLogic.addErrorEntry("dummy", "a text", "a comment", "a version");
		em.getTransaction().commit();
		
		List<ErrorReport> reports = errorReportLogic.getAllReports();
		assertTrue(reports.size() == 1);
		ErrorReport report = reports.get(0);
		
		assertEquals("dummy", report.getSubmitter().getName());
		assertEquals("a text", report.getSampleText());
		assertEquals("a version", report.getVersion());
		assertNotNull(report.getCreation());
		assertNotNull(report.getModified());
		assertTrue(report.getComments().size() == 1);
		Comment c = report.getComments().get(0);

		assertEquals("dummy", c.getUser().getName());
		assertEquals("a comment", c.getComment());
		assertNotNull(c.getDate());
		
	}

}

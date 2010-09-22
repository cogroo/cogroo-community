package br.usp.ime.cogroo.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.model.ErrorReport;
import br.usp.ime.cogroo.model.User;

public class ErrorReportDAOTest {

	private EntityManager em;
	private ErrorReportDAO errorReportDAO;
	
	private User wesley;
	private User william;
	
	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		errorReportDAO = new ErrorReportDAO(em);
		
		populate();
	}

	private void populate() {
		william = new User("William");
		wesley = new User("Wesley");
		UserDAO userDAO = new UserDAO(em);
		em.getTransaction().begin();
		userDAO.add(william);
		userDAO.add(wesley);
		em.getTransaction().commit();
		
		ErrorReport error1 = new ErrorReport( "A sample text", null, "0.0.1-SNAPSHOT", william, new Date(),new Date(), Boolean.TRUE, Boolean.TRUE);
		ErrorReport error2 = new ErrorReport( "A sample text", null, "0.0.1-SNAPSHOT", wesley , new Date(),new Date(), Boolean.TRUE, Boolean.TRUE);
		
		em.getTransaction().begin();
		errorReportDAO.add(error1);
		errorReportDAO.add(error2);
		em.getTransaction().commit();
	}
	
	@Test
	public void testCanGetError() {
		List<ErrorReport> reports =  errorReportDAO.listAll();
		for (ErrorReport errorReport : reports) {
			System.out.println(errorReport.getSubmitter().getName());
		}
		
		assertEquals(2, reports.size());
	}
	
}

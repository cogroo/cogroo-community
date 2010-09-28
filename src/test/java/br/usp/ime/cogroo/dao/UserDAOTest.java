package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.model.User;

public class UserDAOTest {
	private EntityManager em;
	private UserDAO userDAO;
	private User robson;
	private User wesley;

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();

		
		populateWithUsers();
	}

	private void populateWithUsers() {
		userDAO = new UserDAO(em);
		robson = new User("Robson");
		
		wesley = new User("Wesley");
		
		em.getTransaction().begin();
		userDAO.add(robson);
		userDAO.add(wesley);
		em.getTransaction().commit();
		
	}

	@After
	public void tearDown() {
		em.close();
	}

	@Test
	public void bdShouldHave2Users() {
		List<User> actual = userDAO.listAll();
		Assert.assertEquals(2, actual.size());
	}
	
	@Test
	public void bdShouldDeleteWesley() {
		em.getTransaction().begin();
		userDAO.delete(wesley);
		em.getTransaction().commit();
		Assert.assertFalse(userDAO.exist("Wesley"));
		Assert.assertNull(userDAO.retrieve("Wesley"));
	}
	
	@Test
	public void bdShouldExistWesley() {
		Assert.assertTrue(userDAO.exist("Wesley"));
	}

	@Test
	public void bdUpdateWesley() {
		userDAO.update(wesley);
		Assert.assertTrue(userDAO.exist("Wesley"));
	}
	
	@Test
	public void bdShouldGetWesleyByID() {
		Assert.assertEquals(wesley,userDAO.retrieve(wesley.getId()));
	}

	
}

package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.model.PosTag;

public class PosTagDAOTest {
	
	private EntityManager em;
	private PosTagDAO posTagDAO;

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		populateWithPosTags();
		posTagDAO = new PosTagDAO(em);
	}

	private void populateWithPosTags() {
		PosTag p1 = new PosTag("tag 1");
		PosTag p2 = new PosTag("tag 2");
		PosTag p3 = new PosTag("tag 3");
		PosTag p4 = new PosTag("tag 4");
		PosTag p5 = new PosTag("tag 5");
		PosTag p6 = new PosTag("tag do Robson");
		PosTag p7 = new PosTag("tag super bacana");
		
		em.getTransaction().begin();
		em.persist(p1);
		em.persist(p2);
		em.persist(p3);
		em.persist(p4);
		em.persist(p5);
		em.persist(p6);
		em.persist(p7);
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		em.close();
	}

	@Test
	public void bdShouldHaveInitially7ElementsPosTag() {
		List<PosTag> actual = posTagDAO.listAll();
		Assert.assertEquals(7, actual.size());
	}

	@Test
	public void testRetrievePosTagById() {
		List<PosTag> list = posTagDAO.listAll();
		PosTag expected = list.get(0);

		PosTag actual = posTagDAO.retrieve(expected.getId());

		Assert.assertEquals(expected,actual);
	}
	
	@Test
	public void bdShouldRetrievePosTagByString() {
		PosTag expected = new PosTag("tag 3");
		PosTag actual = posTagDAO.retrieve("tag 3");

		Assert.assertEquals(expected.getPosTag(), actual.getPosTag());
	}
	
	@Test
	public void BdShouldContainAddedPosTags() {
    	List<PosTag> actual = posTagDAO.listAll();

		Assert.assertTrue(actual.contains(new PosTag("tag 1")));
		Assert.assertTrue(actual.contains(new PosTag("tag 2")));
		Assert.assertTrue(actual.contains(new PosTag("tag 3")));
		Assert.assertTrue(actual.contains(new PosTag("tag 4")));
		Assert.assertTrue(actual.contains(new PosTag("tag 5")));
		Assert.assertTrue(actual.contains(new PosTag("tag do Robson")));
	}
	
	@Test
	public void BdShouldNotContainNotAddedPosTags() {
    	List<PosTag> actual = posTagDAO.listAll();
		Assert.assertFalse(actual.contains(new PosTag("tag 6")));
	}
	
	@Test(expected = PersistenceException.class)
	public void bdThrowsExceptionWhenTryInsertRepeatedPosTags() {

		em.getTransaction().begin();
		
		PosTag postag1 = new PosTag("tag 7");
		posTagDAO.add(postag1);
		PosTag postag2 = new PosTag("tag 7");
		posTagDAO.add(postag2);

		em.getTransaction().commit();
	}
	
	@Test
	public void BdShouldNotContainDeletedPosTag() {
		em.getTransaction().begin();
		
		PosTag postag = posTagDAO.retrieve("tag 2");
		posTagDAO.delete(postag);
		
		em.getTransaction().commit();

		Assert.assertFalse((posTagDAO.listAll()).contains(postag));
	}
	

}

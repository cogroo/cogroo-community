package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.DictionaryEntryUser;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.Word;

public class DictionaryEntryUserDAOTest {
	EntityManager em;
	DictionaryEntryUserDAO dictionaryEntryUserDAO;
	User william;
	User robson;

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();

		dictionaryEntryUserDAO = new DictionaryEntryUserDAO(em);
		populateWithDictionaryEntryUsers();
	}

	private void populateWithDictionaryEntryUsers() {
		Word word1 = new Word("word1");
		Word word2 = new Word("word2");
		Word word3 = new Word("word3");
		
		Word lemma1 = new Word("lemma1");
		Word lemma2 = new Word("lemma2");
		Word lemma3 = new Word("lemma3");

		WordDAO wordDAO = new WordDAO(em);
		em.getTransaction().begin();
		wordDAO.add(word1);
		wordDAO.add(word2);
		wordDAO.add(word3);
		wordDAO.add(lemma1);
		wordDAO.add(lemma2);
		wordDAO.add(lemma3);
		em.getTransaction().commit();

		PosTag posTag1 = new PosTag("tag1");
		PosTag posTag2 = new PosTag("tag2");
		PosTag posTag3 = new PosTag("tag3");

		PosTagDAO posTagDAO = new PosTagDAO(em);
		em.getTransaction().begin();
		posTagDAO.add(posTag1);
		posTagDAO.add(posTag2);
		posTagDAO.add(posTag3);
		em.getTransaction().commit();

		DictionaryEntry dictionaryEntry1 = new DictionaryEntry(word1,lemma1,posTag1);
		DictionaryEntry dictionaryEntry2 = new DictionaryEntry(word2,lemma2,posTag2);
		DictionaryEntry dictionaryEntry3 = new DictionaryEntry(word3,lemma3,posTag3);

		DictionaryEntryDAO dictionaryEntryDAO = new DictionaryEntryDAO(em);   
		em.getTransaction().begin();
		dictionaryEntryDAO.add(dictionaryEntry1);
		dictionaryEntryDAO.add(dictionaryEntry2);
		dictionaryEntryDAO.add(dictionaryEntry3);
		em.getTransaction().commit();
		
		william = new User("William");
		robson = new User("Robson");
		em.getTransaction().begin();
		em.persist(william);
		em.persist(robson);
		em.getTransaction().commit();
		
		DictionaryEntryUser entry1 = new DictionaryEntryUser(dictionaryEntry1,william);
		em.getTransaction().begin();
		dictionaryEntryUserDAO.add(entry1);
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		em.close();
	}
	
	@Test
	public void bdShouldHaveDictionaryEntryForUserWilliam() {
		DictionaryEntry entry = new DictionaryEntry(new Word("word1"), new Word("lemma1"), new PosTag("tag1")); 
		DictionaryEntryUser entryUser = dictionaryEntryUserDAO.find(entry, william);
		
		Assert.assertTrue(entryUser.getUser().equals(william));
	}
	
	@Test
	public void bdShouldCantHaveDictionaryEntryForUserRobson() {
		DictionaryEntry entry = new DictionaryEntry(new Word("word1"), new Word("lemma1"), new PosTag("tag1")); 
		DictionaryEntryUser entryUser = dictionaryEntryUserDAO.find(entry, robson);
		
		Assert.assertNull(entryUser);
	}
	
	@Test
	public void shouldFindAllRelatedUsersOfADicionaryEntry(){
		Word word1 = new Word("word10");
		
		Word lemma1 = new Word("lemma10");

		WordDAO wordDAO = new WordDAO(em);
		em.getTransaction().begin();
		wordDAO.add(word1);
		wordDAO.add(lemma1);
		em.getTransaction().commit();

		PosTag posTag1 = new PosTag("tag10");

		PosTagDAO posTagDAO = new PosTagDAO(em);
		em.getTransaction().begin();
		posTagDAO.add(posTag1);
		em.getTransaction().commit();

		DictionaryEntry dictionaryEntry1 = new DictionaryEntry(word1,lemma1,posTag1);

		DictionaryEntryDAO dictionaryEntryDAO = new DictionaryEntryDAO(em);   
		em.getTransaction().begin();
		dictionaryEntryDAO.add(dictionaryEntry1);
		em.getTransaction().commit();
		
		User wesley = new User("wesley");
		User thiago = new User("Thiago");
		em.getTransaction().begin();
		em.persist(wesley);
		em.persist(thiago);
		em.getTransaction().commit();
		
		DictionaryEntryUser entry1 = new DictionaryEntryUser(dictionaryEntry1,wesley);
		DictionaryEntryUser entry2 = new DictionaryEntryUser(dictionaryEntry1,thiago);
		em.getTransaction().begin();
		dictionaryEntryUserDAO.add(entry1);
		dictionaryEntryUserDAO.add(entry2);
		em.getTransaction().commit();

		List<DictionaryEntryUser> userList = dictionaryEntryUserDAO.findUsers(dictionaryEntry1);
		Assert.assertEquals(2, userList.size());  
	}
	
	@Test
	public void bdFindWilliamEntries() {
		List<DictionaryEntryUser> list = dictionaryEntryUserDAO.find(william);
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void dbShouldNotHaveDeletedDictionaryEntryUser() {
		List<DictionaryEntryUser> userList;
		
		Word word1 = new Word("word10");
		Word lemma1 = new Word("lemma10");

		WordDAO wordDAO = new WordDAO(em);
		em.getTransaction().begin();
		wordDAO.add(word1);
		wordDAO.add(lemma1);
		em.getTransaction().commit();

		PosTag posTag1 = new PosTag("tag10");

		PosTagDAO posTagDAO = new PosTagDAO(em);
		em.getTransaction().begin();
		posTagDAO.add(posTag1);
		em.getTransaction().commit();

		DictionaryEntry dictionaryEntry1 = new DictionaryEntry(word1,lemma1,posTag1);

		DictionaryEntryDAO dictionaryEntryDAO = new DictionaryEntryDAO(em);   
		em.getTransaction().begin();
		dictionaryEntryDAO.add(dictionaryEntry1);
		em.getTransaction().commit();
		
		User wesley = new User("wesley");
		User thiago = new User("Thiago");
		em.getTransaction().begin();
		em.persist(wesley);
		em.persist(thiago);
		em.getTransaction().commit();
		
		DictionaryEntryUser entry1 = new DictionaryEntryUser(dictionaryEntry1,wesley);
		DictionaryEntryUser entry2 = new DictionaryEntryUser(dictionaryEntry1,thiago);
		em.getTransaction().begin();
		dictionaryEntryUserDAO.add(entry1);
		dictionaryEntryUserDAO.add(entry2);
		em.getTransaction().commit();
		
		
		userList = dictionaryEntryUserDAO.findUsers(dictionaryEntry1);
		Assert.assertEquals(2, userList.size());  
		
		em.getTransaction().begin();
		dictionaryEntryUserDAO.delete(entry1);
		em.getTransaction().commit();
		
		userList = dictionaryEntryUserDAO.findUsers(dictionaryEntry1);
		Assert.assertEquals(1, userList.size());
		
	}
}

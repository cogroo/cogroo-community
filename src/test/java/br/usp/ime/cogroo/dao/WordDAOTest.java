package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.Word;
import br.usp.ime.cogroo.model.WordUser;

public class WordDAOTest {
	private EntityManager em;
	private WordDAO wordDAO;
	private User robson;
	private User wesley;
	
	private static final Logger LOG = Logger.getLogger(WordDAOTest.class);

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();

		populateWithWords();
		wordDAO = new WordDAO(em);
	}

	private void populateWithWords() {
		Word w1 = new Word("Teclado");
		Word w2 = new Word("Mouse");
		Word w3 = new Word("Monitor");
		Word w4 = new Word("Rato");
		
		robson = new User("Robson");
		Word ratazana = new Word("Ratazana");
		
		wesley = new User("Wesley");
		Word ratao = new Word("Ratao");
		
		em.getTransaction().begin();
		em.persist(robson);
		em.persist(wesley);
		em.persist(w1);
		em.persist(w2);
		em.persist(w3);
		em.persist(w4);
		em.persist(ratazana);
		em.persist(ratao);
		em.getTransaction().commit();
		
		WordUser wu6 = new WordUser(ratao,wesley);
		
		WordUser wu1 = new WordUser(ratazana,robson);
		wu1.setDeleted(true);
		// Robson wants a Ratao too!
		WordUser wu2 = new WordUser(ratao,robson);
		wu2.setDeleted(false);
		
		em.getTransaction().begin();
		em.persist(wu1);
		em.persist(wu2);
		em.persist(wu6);
		em.getTransaction().commit();
		
	}

	@After
	public void tearDown() {
		em.close();
	}

	@Test
	public void bdShouldHaveInitially6ElementsWord() {
		List<Word> actual = wordDAO.listAll();
		Assert.assertEquals(6, actual.size());
	}

	@Test
	public void bdShouldContainAddedWords() {
		List<Word> actual = wordDAO.listAll();

		Assert.assertTrue(actual.contains(new Word("Monitor")));
	}

	@Test
	public void bdShouldContainWordMouse() {
		Word actual = wordDAO.retrieve("Mouse");
		Assert.assertTrue(actual.getWord().equals("Mouse"));
	}
	
	@Test
	public void bdWesleyCantGetRatazana() {
		WordUser actual = wordDAO.retrieve("Ratazana", wesley);
		Assert.assertNull(actual);
	}
	
	@Test
	public void bdShouldContainWordMouseWithUserRobson() {
		WordUser actual = wordDAO.retrieve("Ratazana", robson);
		Assert.assertTrue(actual.getWord().getWord().equals("Ratazana"));
		Assert.assertTrue(actual.getUser().getName().equals("Robson"));
	}
	
	@Test
	public void bdOnlyUserRobsonAndWesleyCanGetRatao() {
		
		WordUser doWesley = wordDAO.retrieve("Ratao", wesley);
		Assert.assertTrue(doWesley.getWord().getWord().equals("Ratao"));
		Assert.assertTrue(doWesley.getUser().getName().equals("Wesley"));
		
		WordUser doRobson = wordDAO.retrieve("Ratao", robson);
		Assert.assertTrue(doRobson.getWord().getWord().equals("Ratao"));
		Assert.assertTrue(doRobson.getUser().getName().equals("Robson"));
	}
	
	@Test
	public void testSohParaMostrarProWilliam() {
		List<WordUser> lista = robson.getWordUserList();
		List<String> wordList = new ArrayList<String>();
		for (WordUser wordUser : lista) {
			LOG.debug(wordUser.getWord().getWord() +" : " + wordUser.isDeleted());
			wordList.add(wordUser.getWord().getWord());
		}
		Assert.assertTrue(wordList.contains("Ratazana"));
		Assert.assertTrue(wordList.contains("Ratao"));
	}
	
	
//
//	@Test
//	public void testRetrieveWord() {
//		List<Word> actual = wordDAO.listAll();
//		Word w1 = actual.get(0);
//
//		Word w2 = wordDAO.retrieve(w1.getId());
//
//		Assert.assertEquals(w1, w2);
//	}
//
//	@Test
//	public void testAddWords() {
//		Word w1 = new Word("Caneta");
//
//		em.getTransaction().begin();
//		wordDAO.add(w1);
//		em.getTransaction().commit();
//		List<Word> actual = wordDAO.listAll();
//		Assert.assertTrue(actual.contains(new Word("Caneta")));
//	}
//
//	@Test(expected = PersistenceException.class)
//	public void bdThrowsExceptionWhenTryInsertRepeatedWords() {
//		Word w1 = new Word("Teclado");
//
//		em.getTransaction().begin();
//		wordDAO.add(w1);
//		em.getTransaction().commit();
//	}
//
//	@Test
//	public void testEditedWords() {
//		List<Word> listWord = wordDAO.listAll();
//		Word w1 = listWord.get(1);
//		w1.setWord("Alterada");
//
//		em.getTransaction().begin();
//		wordDAO.update(w1);
//		em.getTransaction().commit();
//
//		listWord = wordDAO.listAll();
//		Assert.assertTrue(listWord.contains(new Word("Alterada")));
//	}
//
//	@Test
//	public void testDeletedWords() {
//		List<Word> listWord = wordDAO.listAll();
//		Word w1 = listWord.get(1);
//
//		em.getTransaction().begin();
//		wordDAO.delete(w1);
//		em.getTransaction().commit();
//
//		listWord = wordDAO.listAll();
//		Assert.assertFalse(listWord.contains(new Word(w1.getWord())));
//	}
//
//	@Test
//	public void testIfUpdatedWithAddOrUpdate() {
//		List<Word> listWord = wordDAO.listAll();
//		Word w1 = listWord.get(1);
//		w1.setWord("Lapis");
//
//		em.getTransaction().begin();
//		wordDAO.addOrUpdate(w1);
//		em.getTransaction().commit();
//
//		listWord = wordDAO.listAll();
//		Assert.assertTrue(listWord.contains(new Word("Lapis")));
//	}
//
//	@Test
//	public void testIfAddedWithAddOrUpdate() {
//		List<Word> listWord = wordDAO.listAll();
//		Word w1 = new Word("Sapato");
//
//		em.getTransaction().begin();
//		wordDAO.addOrUpdate(w1);
//		em.getTransaction().commit();
//
//		listWord = wordDAO.listAll();
//		Assert.assertTrue(listWord.contains(new Word("Sapato")));
//	}

}

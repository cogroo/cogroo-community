package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.Word;

public class DictionaryEntryDAOTest {
	EntityManager em;
	DictionaryEntryDAO masterDictionaryEntryDAO;

	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();

		masterDictionaryEntryDAO = new DictionaryEntryDAO(em);
		populateWithDictionaryEntrys();
	}

	private void populateWithDictionaryEntrys() {

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

		DictionaryEntry dictionaryEntry1 = new DictionaryEntry();
		dictionaryEntry1.setLemma(lemma1);
		dictionaryEntry1.setPosTag(posTag1);
		dictionaryEntry1.setWord(word1);

		DictionaryEntry dictionaryEntry2 = new DictionaryEntry();
		dictionaryEntry2.setLemma(lemma2);
		dictionaryEntry2.setPosTag(posTag2);
		dictionaryEntry2.setWord(word2);

		DictionaryEntry dictionaryEntry3 = new DictionaryEntry();
		dictionaryEntry3.setLemma(lemma3);
		dictionaryEntry3.setPosTag(posTag3);
		dictionaryEntry3.setWord(word3);

		em.getTransaction().begin();
		masterDictionaryEntryDAO.add(dictionaryEntry1);
		masterDictionaryEntryDAO.add(dictionaryEntry2);
		masterDictionaryEntryDAO.add(dictionaryEntry3);
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		em.close();
	}

	@Test
	public void bdShouldHaveInitiallyThreeElementsmasterDictionaryEntrys() {
		List<DictionaryEntry> actual = masterDictionaryEntryDAO.listAll();
		Assert.assertEquals(3, actual.size());
	}

	@Test
	public void BdShouldContainAddedmasterDictionaryEntrys() {
		List<DictionaryEntry> actual = masterDictionaryEntryDAO.listAll();

		Word word1 = new Word("word1");
		Word word2 = new Word("word2");
		Word word3 = new Word("word3");
		
		Word lemma1 = new Word("lemma1");
		Word lemma2 = new Word("lemma2");
		Word lemma3 = new Word("lemma3");

		PosTag posTag1 = new PosTag("tag1");
		PosTag posTag2 = new PosTag("tag2");
		PosTag posTag3 = new PosTag("tag3");

		DictionaryEntry masterDictionaryEntry1 = new DictionaryEntry(word1, lemma1, posTag1);
		DictionaryEntry masterDictionaryEntry2 = new DictionaryEntry(word2, lemma2, posTag2);
		DictionaryEntry masterDictionaryEntry3 = new DictionaryEntry(word3, lemma3, posTag3);

		Assert.assertTrue(actual.contains(masterDictionaryEntry1));
		Assert.assertTrue(actual.contains(masterDictionaryEntry2));
		Assert.assertTrue(actual.contains(masterDictionaryEntry3));
	}

	@Test
	public void BdShouldNotContainNotAddedmasterDictionaryEntrys() {
		List<DictionaryEntry> actual = masterDictionaryEntryDAO.listAll();

		Word word2 = new Word();
		word2.setWord("word2");

		PosTag posTag1 = new PosTag("tag 1");

		DictionaryEntry masterDictionaryEntry1 = new DictionaryEntry();
		masterDictionaryEntry1.setLemma(word2);
		masterDictionaryEntry1.setPosTag(posTag1);

		Assert.assertFalse(actual.contains(masterDictionaryEntry1));
	}

	@Test(expected = PersistenceException.class)
	public void bdThrowsExceptionWhenTryInsertRepeatedmasterDictionaryEntrys() {

		em.getTransaction().begin();

		Word word2 = new Word();
		word2.setWord("word2");

		PosTag posTag1 = new PosTag("tag 1");

		DictionaryEntry masterDictionaryEntry1 = new DictionaryEntry();
		masterDictionaryEntry1.setLemma(word2);
		masterDictionaryEntry1.setPosTag(posTag1);

		DictionaryEntry masterDictionaryEntry2 = new DictionaryEntry();
		masterDictionaryEntry2.setLemma(word2);
		masterDictionaryEntry2.setPosTag(posTag1);

		masterDictionaryEntryDAO.add(masterDictionaryEntry1);
		masterDictionaryEntryDAO.add(masterDictionaryEntry2);

		em.getTransaction().commit();
	}

	@Test
	public void bdShouldNotContainDeletedmasterDictionaryEntry() {
		List<DictionaryEntry> listmasterDictionaryEntry = masterDictionaryEntryDAO.listAll();
		DictionaryEntry masterDictionaryEntry = listmasterDictionaryEntry.get(1);
		Word word = masterDictionaryEntry.getWord();
		Word primitive = masterDictionaryEntry.getLemma();
		PosTag posTag = masterDictionaryEntry.getPosTag();
		DictionaryEntry expected = new DictionaryEntry(word, primitive, posTag);

		em.getTransaction().begin();
		masterDictionaryEntryDAO.delete(masterDictionaryEntry);
		em.getTransaction().commit();

		listmasterDictionaryEntry = masterDictionaryEntryDAO.listAll();
		Assert.assertFalse(listmasterDictionaryEntry.contains(expected));
	}

	@Test
	public void bdShouldRetrievemasterDictionaryEntryByPrimitiveAndPostag() {
		List<DictionaryEntry> listmasterDictionaryEntry = masterDictionaryEntryDAO.listAll();
		DictionaryEntry expected = listmasterDictionaryEntry.get(1);
		Word word = expected.getWord();
		Word primitive = expected.getLemma();
		PosTag posTag = expected.getPosTag();

		DictionaryEntry actual = masterDictionaryEntryDAO.retrieve(word, primitive, posTag);
		Assert.assertEquals(expected, actual);
	}
}

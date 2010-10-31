package br.usp.ime.cogroo.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.dao.DictionaryEntryDAO;
import br.usp.ime.cogroo.dao.DictionaryEntryUserDAO;
import br.usp.ime.cogroo.dao.PosTagDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.WordDAO;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.DictionaryEntryUser;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.NicePrintDictionaryEntry;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.Word;
import br.usp.pcs.lta.cogroo.tools.dictionary.PairWordPOSTag;

public class DictionaryManagerTest {
	DictionaryManager dictionaryNull;
	DictionaryManager dictionaryUserWesley;
	DictionaryManager dictionaryUserWilliam;
	EntityManager em;
	User wesley;
	User william;
	
	@Test
	public void testWordAndPosTagOfTheLemmaComputar() {
		List<PairWordPOSTag> lista = dictionaryNull.getWordsAndPosTagsForLemma("computar");
		PairWordPOSTag p1 = new PairWordPOSTag("computemos","V_IMP_1P_VFIN_");
		PairWordPOSTag p2 = new PairWordPOSTag("computardes","V_FUT_2P_SUBJ_VFIN_");
		assertTrue(lista.contains(p1));
		assertTrue(lista.contains(p2));
	}
	
	@Test
	public void testLemmaAndPosTagOfTheWordComputa() {
		List<PairWordPOSTag> lista = dictionaryNull.getLemmasAndPosTagsForWord("computa");
		PairWordPOSTag p1 = new PairWordPOSTag("computar","V_PR_3S_IND_VFIN_");
		PairWordPOSTag p2 = new PairWordPOSTag("computar","V_IMP_2S_VFIN_");
		assertTrue(lista.contains(p1));
		assertTrue(lista.contains(p2));
	}
	
	@Test
	public void shouldBePosTagOfTheWordComputadorExists() {
		List<String> listaPosTagComputador = dictionaryNull.getPOSTagsForWord("computemos");
		assertTrue(listaPosTagComputador.contains("V_IMP_1P_VFIN_"));
	}
	
	@Test
	public void testIfWordCasaExists() {
		assertTrue("word casa should exist", dictionaryNull.wordExists("casa"));
	}
	
	@Test
	public void testIfWrongWordNotExists() {
		assertFalse("unknown word should not exist", dictionaryNull.wordExists("palabraEzkizita"));
	}
	
	// Case 1
	@Test
	public void testAnonymousUserShouldntGetWesleyDataForLemma() {
		List<PairWordPOSTag> data = dictionaryNull.getWordsAndPosTagsForLemma("Wesley");
		assertEquals(0,data.size());
	}
	
	// Case 2
	@Test
	public void testUserWesleyShouldGetWesleyLemma() {
		List<PairWordPOSTag> data = dictionaryUserWesley.getWordsAndPosTagsForLemma("Wesley");
		assertTrue(data.size() > 0);
	}
	
	
	// Case 3
	@Test
	public void testUserWesleyShouldntGetLemmaWesleyDeleted() {
		List<PairWordPOSTag> data = dictionaryUserWesley.getWordsAndPosTagsForLemma("DeletedWesley");
		assertTrue(data.size() == 0);
	}
	
	
	// Case 4
	@Test
	public void testUserWilliamShouldntGetWesleyLemma() {
		List<PairWordPOSTag> data = dictionaryUserWilliam.getWordsAndPosTagsForLemma("Wesley");
		assertTrue(data.size() == 0);
	}
	
	// Case 1
	@Test
	public void testAnonymousUserShouldntGetWesleyTagsForWord() {
		List<String> data = dictionaryNull.getPOSTagsForWord("Wesley");
		assertEquals(0,data.size());
	}
	
	// Case 2
	@Test
	public void testUserWesleyShouldGetWesleyTagsForWord() {
		List<String> data = dictionaryUserWesley.getPOSTagsForWord("Wesley");
		assertTrue(data.size() > 0);
	}
	
	// Case 3
	@Test
	public void testUserWesleyShouldntGetTagsForWordWesleyDeleted() {
		List<String> data = dictionaryUserWesley.getPOSTagsForWord("DeletedWesley");
		assertTrue(data.size() == 0);
	}
	
	
	// Case 4
	@Test
	public void testUserWilliamShouldntGetWesleyTagsForWord() {
		List<String> data = dictionaryUserWilliam.getPOSTagsForWord("Wesley");
		assertTrue(data.size() == 0);
	}
	
	// Case 1
	@Test
	public void testAnonymousUserShouldntGetWesleyDataForWord() {
		List<PairWordPOSTag> data = dictionaryNull.getLemmasAndPosTagsForWord("Wesley");
		assertEquals(0,data.size());
	}
	
	// Case 2
	@Test
	public void testUserWesleyShouldGetWesleyWord() {
		List<PairWordPOSTag> data = dictionaryUserWesley.getLemmasAndPosTagsForWord("Wesley");
		assertTrue(data.size() > 0);
	}
	
	
	// Case 3
	@Test
	public void testUserWesleyShouldntGetWordWesleyDeleted() {
		List<PairWordPOSTag> data = dictionaryUserWesley.getLemmasAndPosTagsForWord("DeletedWesley");
		assertTrue(data.size() == 0);
	}
	
	
	// Case 4
	@Test
	public void testUserWilliamShouldntGetWesleyWord() {
		List<PairWordPOSTag> data = dictionaryUserWilliam.getLemmasAndPosTagsForWord("Wesley");
		assertTrue(data.size() == 0);
	}
	
	// Case 1
	@Test
	public void testAnonymousUserShouldntGetWesleyDataExists() {
		assertFalse(dictionaryNull.wordExists("Wesley"));
	}
	
	// Case 2
	@Test
	public void testUserWesleyShouldGetWesleyExists() {
		assertTrue(dictionaryUserWesley.wordExists("Wesley"));
	}
	
	
	// Case 3
	@Test
	public void testUserWesleyShouldntGetExistsWesleyDeleted() {
		assertFalse(dictionaryUserWesley.wordExists("DeletedWesley"));
	}
	
	
	// Case 4
	@Test
	public void testUserWilliamShouldntGetWesleyExists() {
		assertFalse(dictionaryUserWilliam.wordExists("Wesley"));
	}
	
	@Test
	public void testAnonimousShouldHave0Elements() {
		assertEquals(0, dictionaryNull.listDictionaryEntries().size() );
	}
	
	@Test
	public void testWesleyShouldHave2Elements() {
		assertEquals(2, dictionaryUserWesley.listDictionaryEntries().size() );
	}
	
	@Test
	public void testWilliamShouldHave3Elements() {
		assertEquals(3, dictionaryUserWilliam.listDictionaryEntries().size() );
	}
	
	@Test(expected=Exception.class)
	public void testAnonymousCantAddData() throws Exception {
		assertEquals(0, dictionaryNull.listDictionaryEntries().size() );
		em.getTransaction().begin();
		dictionaryNull.add(new DictionaryEntry(
				new Word("aaa"),
				new Word("aaa"),
				new PosTag("abc")));
		em.getTransaction().commit();
		assertEquals(0, dictionaryNull.listDictionaryEntries().size() );
	}
	
	
	@Test
	public void testWesleyCanAddData() throws Exception {
		assertEquals(2, dictionaryUserWesley.listDictionaryEntries().size() );
		em.getTransaction().begin();
		dictionaryUserWesley.add(new DictionaryEntry(
				new Word("aaa"),
				new Word("aaa"),
				new PosTag("N_M_P_")));
		em.getTransaction().commit();
		assertEquals(3, dictionaryUserWesley.listDictionaryEntries().size() );
	}
	
	@Test
	public void testWesleyCanGetLemmaOfComputa() throws Exception {
		List<PairWordPOSTag> l = dictionaryUserWesley.getLemmasAndPosTagsForWord("computa") ;
		
		assertEquals("computar", l.get(0).getWord());
		
	}
	
	@Test
	public void testAnonimousCanGetLemmaOfComputa() throws Exception {
		List<PairWordPOSTag> l = dictionaryNull.getLemmasAndPosTagsForWord("computa") ;
		
		assertEquals("computar", l.get(0).getWord());
		
	}
	
	@Test(expected=Exception.class)
	public void testWesleyCantAddWordWithUnknowLemma() throws Exception {
		assertEquals(2, dictionaryUserWesley.listDictionaryEntries().size() );
		em.getTransaction().begin();
		dictionaryUserWesley.add(new DictionaryEntry(
				new Word("aaa"),
				new Word("bbb"),
				new PosTag("N_F_S_")));
		em.getTransaction().commit();
		assertEquals(2, dictionaryUserWesley.listDictionaryEntries().size() );
	}
	
	@Test
	public void testWilliamCanAddADictionaryEntryThatAlreadyThatWesleyAlreadyAdded() throws Exception {
		assertEquals(2, dictionaryUserWesley.listDictionaryEntries().size() );
		em.getTransaction().begin();
		dictionaryUserWesley.add(new DictionaryEntry(
				new Word("aaa"),
				new Word("aaa"),
				new PosTag("N_M_P_")));
		em.getTransaction().commit();
		assertEquals(3, dictionaryUserWesley.listDictionaryEntries().size() );
		
		assertEquals(3, dictionaryUserWilliam.listDictionaryEntries().size() );
		em.getTransaction().begin();
		dictionaryUserWilliam.add(new DictionaryEntry(
				new Word("aaa"),
				new Word("aaa"),
				new PosTag("N_M_P_")));
		em.getTransaction().commit();
		assertEquals(4, dictionaryUserWilliam.listDictionaryEntries().size() );
	}
	
	@Test
	public void testWesleyCanAddWordWithKnowData() throws Exception {
		assertEquals(2, dictionaryUserWesley.listDictionaryEntries().size() );
		em.getTransaction().begin();
		dictionaryUserWesley.add(new DictionaryEntry(
				new Word("William"),
				new Word("William"),
				new PosTag("V_IMP_1P_VFIN_")));

		em.getTransaction().commit();
		assertEquals(3, dictionaryUserWesley.listDictionaryEntries().size() );
	}
	
	@Test
	public void testAnonymousUserShouldGet4Entries() {
		List<NicePrintDictionaryEntry> dlist = dictionaryNull.searchWordAndLemma("computar");
		assertEquals(4,dlist.size());
	}
	
	@Test
	public void testUserWilliamShouldGet4Entries() {
		List<NicePrintDictionaryEntry> dlist = dictionaryUserWilliam.searchWordAndLemma("computar");
		assertEquals(2,dlist.size());
	}
	
	@Test
	public void testWesleyCanDeleteADictionaryEntryHeCreated() throws Exception {
		
		List<NicePrintDictionaryEntry> dicEntries = dictionaryUserWesley.listDictionaryEntriesForUser();
		DictionaryEntryDAO dao = new DictionaryEntryDAO(em);
		NicePrintDictionaryEntry entry = dicEntries.get(0);
		
		DictionaryEntry fromDao = dao.find(entry.getWord().getWord(), entry.getLemma().getWord(), entry.getPosTag().getPosTag());
		assertNotNull(fromDao);

		assertEquals(2,dicEntries.size());
		em.getTransaction().begin();
		dictionaryUserWesley.delete(entry.getDictionaryEntry());
		em.getTransaction().commit();
		
		dicEntries = dictionaryUserWesley.listDictionaryEntriesForUser();
		assertEquals(1,dicEntries.size());
		
		
		fromDao = dao.find(entry.getWord().getWord(), entry.getLemma().getWord(), entry.getPosTag().getPosTag());
		assertNull(fromDao);
	}
	
	@Test
	public void testWilliamCanDeleteADictionaryEntryHeCreatedButNotAffectWesley() throws Exception {
		
		List<NicePrintDictionaryEntry> dicEntries = dictionaryUserWilliam.listDictionaryEntriesForUser();
		NicePrintDictionaryEntry entry = dicEntries.get(1);
		
		
		DictionaryEntryDAO dao = new DictionaryEntryDAO(em);
		
		DictionaryEntry fromDao = dao.find(entry.getWord().getWord(), entry.getLemma().getWord(), entry.getPosTag().getPosTag());
		assertNotNull(fromDao);

		assertEquals(3,dicEntries.size());
		em.getTransaction().begin();
		dictionaryUserWilliam.delete(entry.getDictionaryEntry());
		em.getTransaction().commit();
		
		dicEntries = dictionaryUserWilliam.listDictionaryEntriesForUser();
		assertEquals(2,dicEntries.size());
		
		
		fromDao = dao.find(entry.getWord().getWord(), entry.getLemma().getWord(), entry.getPosTag().getPosTag());
		assertNotNull(fromDao);
	}
	
	@Test
	public void testWesleyCanDeleteAGlobalDictionaryEntry() throws Exception {
		
		List<NicePrintDictionaryEntry> dicEntries = dictionaryUserWesley.searchWordAndLemma("computar");

		assertEquals(4,dicEntries.size());
		em.getTransaction().begin();
		dictionaryUserWesley.delete(dicEntries.get(0).getDictionaryEntry());
		em.getTransaction().commit();
		
		dicEntries = dictionaryUserWesley.searchWordAndLemma("computar");
		assertEquals(3,dicEntries.size());
	}
	
	@Test(expected=Exception.class)
	public void testAnonymousCantDeleteDictionaryEntry() throws Exception {
		
		List<NicePrintDictionaryEntry> dicEntries = dictionaryNull.searchWordAndLemma("computar");
		
		em.getTransaction().begin();
		dictionaryNull.delete(dicEntries.get(0).getDictionaryEntry());
		em.getTransaction().commit();

	}
	
	@Before
	public void setUp() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		
		PosTagDAO pd = new PosTagDAO(em);
		WordDAO wd = new WordDAO(em);
		DictionaryEntryDAO dd = new DictionaryEntryDAO(em);

		DictionaryEntryUserDAO dud = new DictionaryEntryUserDAO(em);
		
		// dictionaryManager without user
		dictionaryNull = new DictionaryManager( new LoggedUser(null), dd, dud, wd, pd);
		
		wesley = new User("Wesley");
		LoggedUser lu = new LoggedUser(null);
		lu.setUser(wesley);
		// dictionaryManager with Wesley as user
		dictionaryUserWesley = new DictionaryManager( lu, dd, dud, wd, pd);
		
		william = new User("William");
		LoggedUser lwill = new LoggedUser(null);
		lwill.setUser(william);
		// dictionaryManager with Wesley as user
		dictionaryUserWilliam = new DictionaryManager( lwill, dd, dud, wd, pd);
		

		populateWithMasterDictionaryEntrys();
	}
	
	
	
	@After
	public void tearDown() {
		em.close();
	}
	
	private void populateWithMasterDictionaryEntrys() {
		

		Word computar = new Word("computar");
		Word computa = new Word("computa");
		Word computador = new Word("computador");
		Word casa = new Word("casa");
		Word computemos = new Word("computemos");
		Word computardes = new Word("computardes");
		
		// Wesley Knows about 
		Word pWesley = new Word("Wesley");
		Word pDeletedWesley = new Word("DeletedWesley");
		Word pWilliam = new Word("William");
		Word pWesleyWilliam = new Word("WesleyWilliam");
		// Wesley and William knows about IME
		Word pIme = new Word("IME");

		WordDAO wordDAO = new WordDAO(em);
		em.getTransaction().begin();
		wordDAO.add(computar);
		wordDAO.add(computa);
		wordDAO.add(computador);
		wordDAO.add(casa);
		wordDAO.add(computemos);
		wordDAO.add(computardes);
		
		wordDAO.add(pWesley);
		wordDAO.add(pWilliam);
		wordDAO.add(pIme);
		wordDAO.add(pDeletedWesley);
		wordDAO.add(pWesleyWilliam);
		em.getTransaction().commit();

		PosTag V_IMP_1P_VFIN_ = new PosTag("V_IMP_1P_VFIN_");
		PosTag V_FUT_2P_SUBJ_VFIN_ = new PosTag("V_FUT_2P_SUBJ_VFIN_");
		PosTag V_PR_3S_IND_VFIN_ = new PosTag("V_PR_3S_IND_VFIN_");
		PosTag V_IMP_2S_VFIN_ = new PosTag("V_IMP_2S_VFIN_");
		PosTag ADJ_M_S_ = new PosTag("ADJ_M_S_");
		PosTag N_M_P_ = new PosTag("N_M_P_");
		PosTag N_M_S_ = new PosTag("N_M_S_");

		PosTagDAO posTagDAO = new PosTagDAO(em);
		em.getTransaction().begin();
		posTagDAO.add(V_IMP_1P_VFIN_);
		posTagDAO.add(V_FUT_2P_SUBJ_VFIN_);
		posTagDAO.add(V_PR_3S_IND_VFIN_);
		posTagDAO.add(V_IMP_2S_VFIN_);
		posTagDAO.add(ADJ_M_S_);
		posTagDAO.add(N_M_P_);
		posTagDAO.add(N_M_S_);
		em.getTransaction().commit();
		
		DictionaryEntry dictionaryEntryCasa = new DictionaryEntry();
		dictionaryEntryCasa.setLemma(casa);
		dictionaryEntryCasa.setPosTag(V_IMP_1P_VFIN_);
		dictionaryEntryCasa.setWord(casa);
		dictionaryEntryCasa.setGlobal(true);

		DictionaryEntry dictionaryEntry1 = new DictionaryEntry();
		dictionaryEntry1.setLemma(computar);
		dictionaryEntry1.setPosTag(V_IMP_1P_VFIN_);
		dictionaryEntry1.setWord(computemos);
		dictionaryEntry1.setGlobal(true);

		DictionaryEntry dictionaryEntry2 = new DictionaryEntry();
		dictionaryEntry2.setLemma(computar);
		dictionaryEntry2.setPosTag(V_FUT_2P_SUBJ_VFIN_);
		dictionaryEntry2.setWord(computardes);
		dictionaryEntry2.setGlobal(true);

		DictionaryEntry dictionaryEntry3 = new DictionaryEntry();
		dictionaryEntry3.setLemma(computar);
		dictionaryEntry3.setPosTag(V_PR_3S_IND_VFIN_);
		dictionaryEntry3.setWord(computa);
		dictionaryEntry3.setGlobal(true);
		
		DictionaryEntry dictionaryEntry4 = new DictionaryEntry();
		dictionaryEntry4.setLemma(computar);
		dictionaryEntry4.setPosTag(V_IMP_2S_VFIN_);
		dictionaryEntry4.setWord(computa);
		dictionaryEntry4.setGlobal(true);
		
		DictionaryEntryDAO dictionaryEntryDAO = new DictionaryEntryDAO(em);
		em.getTransaction().begin();
		dictionaryEntryDAO.add(dictionaryEntry1);
		dictionaryEntryDAO.add(dictionaryEntry2);
		dictionaryEntryDAO.add(dictionaryEntry3);
		dictionaryEntryDAO.add(dictionaryEntry4);
		dictionaryEntryDAO.add(dictionaryEntryCasa);
		em.getTransaction().commit();
		
		UserDAO userDAO = new UserDAO(em);
		em.getTransaction().begin();
		userDAO.add(william);
		userDAO.add(wesley);
		em.getTransaction().commit();
		
		DictionaryEntry deWesley = new DictionaryEntry();
		deWesley.setLemma(pWesley);
		deWesley.setPosTag(V_IMP_2S_VFIN_);
		deWesley.setWord(pWesley);
		DictionaryEntryUser deuWesley = new DictionaryEntryUser(deWesley, wesley);
		
		DictionaryEntry deDeletedWesley = new DictionaryEntry();
		deDeletedWesley.setLemma(pDeletedWesley);
		deDeletedWesley.setPosTag(V_IMP_2S_VFIN_);
		deDeletedWesley.setWord(pDeletedWesley);
		DictionaryEntryUser deuDeletedWesley = new DictionaryEntryUser(deDeletedWesley, wesley);
		deuDeletedWesley.setDeleted(true);
		
		DictionaryEntry deDeletedWesleyWilliam = new DictionaryEntry();
		deDeletedWesleyWilliam.setLemma(pWesleyWilliam);
		deDeletedWesleyWilliam.setPosTag(V_IMP_2S_VFIN_);
		deDeletedWesleyWilliam.setWord(pWesleyWilliam);
		DictionaryEntryUser deuDeletedWesleyWilliamWesley = new DictionaryEntryUser(deDeletedWesleyWilliam, wesley);
		deuDeletedWesleyWilliamWesley.setDeleted(true);
		
		DictionaryEntryUser deuDeletedWesleyWilliamWilliam = new DictionaryEntryUser(deDeletedWesleyWilliam, william);
		deuDeletedWesleyWilliamWilliam.setDeleted(false);
		
		DictionaryEntry dictionaryEntryIME = new DictionaryEntry();
		dictionaryEntryIME.setLemma(pIme);
		dictionaryEntryIME.setPosTag(V_IMP_2S_VFIN_);
		dictionaryEntryIME.setWord(pIme);
		dictionaryEntryIME.setGlobal(false);
		DictionaryEntryUser deuIMEWesley = new DictionaryEntryUser(dictionaryEntryIME, wesley);
		deuIMEWesley.setDeleted(false);
		
		DictionaryEntryUser deuIMEWilliam = new DictionaryEntryUser(dictionaryEntryIME, william);
		deuIMEWilliam.setDeleted(false);
		
		// William dont want some words
		DictionaryEntryUser deuNo1 = new DictionaryEntryUser(dictionaryEntry1, william);
		deuNo1.setDeleted(true);
		
		DictionaryEntryUser deuNo2 = new DictionaryEntryUser(dictionaryEntry2, william);
		deuNo2.setDeleted(true);
		
		DictionaryEntry deWilliam = new DictionaryEntry();
		deWilliam.setLemma(pWilliam);
		deWilliam.setPosTag(V_IMP_2S_VFIN_);
		deWilliam.setWord(pWilliam);
		DictionaryEntryUser deuWilliam = new DictionaryEntryUser(deWilliam, william);
		
		em.getTransaction().begin();
		dictionaryEntryDAO.add(deWilliam);
		dictionaryEntryDAO.add(deWesley);
		dictionaryEntryDAO.add(deDeletedWesley);
		dictionaryEntryDAO.add(deDeletedWesleyWilliam);

		dictionaryEntryDAO.add(dictionaryEntryIME);
		em.getTransaction().commit();
		
		DictionaryEntryUserDAO dictionaryEntryUserDAO = new DictionaryEntryUserDAO(em);
		em.getTransaction().begin();
		dictionaryEntryUserDAO.add(deuWesley);
		dictionaryEntryUserDAO.add(deuDeletedWesley);
		dictionaryEntryUserDAO.add(deuWilliam);
		dictionaryEntryUserDAO.add(deuDeletedWesleyWilliamWilliam);
		dictionaryEntryUserDAO.add(deuNo1);
		dictionaryEntryUserDAO.add(deuNo2);

		dictionaryEntryUserDAO.add(deuIMEWesley);
		dictionaryEntryUserDAO.add(deuIMEWilliam);
		em.getTransaction().commit();
	}


}

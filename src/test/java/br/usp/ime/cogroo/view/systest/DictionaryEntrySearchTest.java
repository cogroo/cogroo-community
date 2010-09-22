//package br.usp.ime.cogroo.view.systest;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//
//import junit.framework.Assert;
//
//import org.apache.log4j.Logger;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import br.usp.ime.cogroo.dao.UserDAO;
//import br.usp.ime.cogroo.model.DictionaryEntry;
//import br.usp.ime.cogroo.model.PosTag;
//import br.usp.ime.cogroo.model.Word;
//
//import com.thoughtworks.selenium.DefaultSelenium;
//import com.thoughtworks.selenium.Selenium;
//
//
//public class DictionaryEntrySearchTest {
//	
//
//	private static final String TIME = "1000";
//	private Selenium selenium;
//	
//	private EntityManagerFactory emf;
//	private EntityManager em;
//	private UserDAO userDAO;
//	
//	private static final Logger LOG = Logger.getLogger(DictionaryEntrySearchTest.class);
//
//	@Before
//	public void start() {
//		Map<String, String> mapa = new HashMap<String, String>();
//		mapa.put("hibernate.connection.url", "jdbc:hsqldb:mem:test");
//
//		emf = Persistence.createEntityManagerFactory("default", mapa);
//		em = emf.createEntityManager();
//		userDAO = new UserDAO(em);
//		
//		this.selenium = new DefaultSelenium("localhost", 4444, "*firefox",
//				"http://localhost:8080");
//		this.selenium.start();
//	}
//
//	@After
//	public void stop() {
//		this.selenium.stop();
//	}
//	
//	@Test
//	public void testWilliamSearchHisEntries() {
//		List<DictionaryEntry> list = new ArrayList<DictionaryEntry>();
//		list.add(new DictionaryEntry(new Word("nadar"), new Word("nadar"), new PosTag("V_INF_")));
//		list.add(new DictionaryEntry(new Word("nadar"), new Word("nado"), new PosTag("V_PS_1P_IND_VFIN_")));
//		list.add(new DictionaryEntry(new Word("computar"), new Word("computar"), new PosTag("V_INF_")));
//		list.add(new DictionaryEntry(new Word("computar"), new Word("computo"), new PosTag("V_PS_1P_IND_VFIN_")));
//		
//		execute("William", list);
//		search("nado");
//
//		Assert.assertTrue(this.selenium.isTextPresent("nado"));
//		Assert.assertTrue(this.selenium.isTextPresent("nadar"));
//		Assert.assertTrue(this.selenium.isTextPresent("V_PS_1P_IND_VFIN_"));
//		Assert.assertFalse(this.selenium.isTextPresent("V_INF_"));
//		Assert.assertFalse(this.selenium.isTextPresent("computar"));
//		
//		search("nadar");
//
//		Assert.assertTrue(this.selenium.isTextPresent("nado"));
//		Assert.assertTrue(this.selenium.isTextPresent("nadar"));
//		Assert.assertTrue(this.selenium.isTextPresent("V_PS_1P_IND_VFIN_"));
//		Assert.assertTrue(this.selenium.isTextPresent("V_INF_"));
//		Assert.assertFalse(this.selenium.isTextPresent("computar"));
//		
//	}
//	
//	@Test
//	public void testWesleySearchHisEntries() {
//		List<DictionaryEntry> list = new ArrayList<DictionaryEntry>();
//		list.add(new DictionaryEntry(new Word("cantar"), new Word("cantar"), new PosTag("V_INF_")));
//		list.add(new DictionaryEntry(new Word("cantar"), new Word("canto"), new PosTag("V_PS_1P_IND_VFIN_")));
//		list.add(new DictionaryEntry(new Word("computar"), new Word("computar"), new PosTag("V_INF_")));
//		list.add(new DictionaryEntry(new Word("computar"), new Word("computo"), new PosTag("V_PS_1P_IND_VFIN_")));
//		
//		execute("William", list);
//		search("canto");
//
//		Assert.assertTrue(this.selenium.isTextPresent("canto"));
//		Assert.assertTrue(this.selenium.isTextPresent("cantar"));
//		Assert.assertTrue(this.selenium.isTextPresent("V_PS_1P_IND_VFIN_"));
//		Assert.assertFalse(this.selenium.isTextPresent("V_INF_"));
//		Assert.assertFalse(this.selenium.isTextPresent("computar"));
//		
//		search("cantar");
//
//		Assert.assertTrue(this.selenium.isTextPresent("canto"));
//		Assert.assertTrue(this.selenium.isTextPresent("cantar"));
//		Assert.assertTrue(this.selenium.isTextPresent("V_PS_1P_IND_VFIN_"));
//		Assert.assertTrue(this.selenium.isTextPresent("V_INF_"));
//		Assert.assertFalse(this.selenium.isTextPresent("computar"));
//		
//	}
//	
//	private void execute(String user, List<DictionaryEntry> entries) {
//		selenium.open("/login");
//		selenium.type("user.name", user);
//		selenium.click("//input[@value='Entrar']");
//		selenium.waitForPageToLoad("30000");
//		
//		for (DictionaryEntry dictionaryEntry : entries) {
//			selenium.open("/dictionaryEntry");
//			selenium.type("dictionaryEntry.lemma.word", dictionaryEntry.getWord().getWord());
//			selenium.type("dictionaryEntry.word.word",  dictionaryEntry.getLemma().getWord());
//			selenium.type("dictionaryEntry.posTag.posTag",  dictionaryEntry.getPosTag().getPosTag());
//			//selenium.click("//input[@value='Incluir']");
//			//selenium.waitForPageToLoad("30000");
//			selenium.click("//input[@value='Salvar']");
//			selenium.waitForPageToLoad("30000");
//		}
//
//		
//	}
//	
//	private void search(String word) {
//		selenium.open("/dictionaryEntrySearch");
//		selenium.type("word", word);
//		selenium.click("go");
//		selenium.waitForPageToLoad("30000");
//	}
//	
//
//}

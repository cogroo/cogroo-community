//package br.usp.ime.cogroo.view.systest;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
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
//import br.usp.ime.cogroo.logic.DictionaryManager;
//import br.usp.ime.cogroo.model.User;
//
//import com.thoughtworks.selenium.DefaultSelenium;
//import com.thoughtworks.selenium.Selenium;
//
//public class ParseViewTest {
//
//	private static final String TIME = "1000";
//	private Selenium selenium;
//	
//	private EntityManagerFactory emf;
//	private EntityManager em;
//	private UserDAO userDAO;
//	
//	private static final Logger LOG = Logger.getLogger(ParseViewTest.class);
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
//	public void shouldShowMistakeIfSentenceIsWrong() throws Exception {
//		String user = createRandomUser();
//		
//		selenium.open("/login");
//		selenium.type("user.name", user);
//		selenium.click("//input[@value='Entrar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.open("/dictionaryEntry");
//		selenium.type("dictionaryEntry.lemma.word", "ser");
//		selenium.type("dictionaryEntry.word.word", "ser");
//		selenium.type("dictionaryEntry.posTag.posTag", "V_INF_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "levar");
//		selenium.type("dictionaryEntry.word.word", "levar");
//		selenium.type("dictionaryEntry.posTag.posTag", "V_INF_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "o");
//		selenium.type("dictionaryEntry.word.word", "o");
//		selenium.type("dictionaryEntry.posTag.posTag", "DET_M_S_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "ser");
//		selenium.type("dictionaryEntry.word.word", "fomos");
//		selenium.type("dictionaryEntry.posTag.posTag", "V_PS_1P_IND_VFIN_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "levar");
//		selenium.type("dictionaryEntry.word.word", "levados");
//		selenium.type("dictionaryEntry.posTag.posTag", "V_PCP_M_P_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "a");
//		selenium.type("dictionaryEntry.word.word", "a");
//		selenium.type("dictionaryEntry.posTag.posTag", "PRP_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "o");
//		selenium.type("dictionaryEntry.word.word", "a");
//		selenium.type("dictionaryEntry.posTag.posTag", "DET_F_S_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", "crer");
//		selenium.type("dictionaryEntry.word.word", "crer");
//		selenium.type("dictionaryEntry.posTag.posTag", "V_INF_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("//input[@value='Incluir']");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("dictionaryEntry.lemma.word", ".");
//		selenium.type("dictionaryEntry.word.word", ".");
//		selenium.type("dictionaryEntry.posTag.posTag", "-PNT_ABS_");
//		selenium.click("//input[@value='Salvar']");
//		selenium.open("/");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("texto", "fomos levados à crer");
//		selenium.click("go");
//		selenium.waitForPageToLoad("30000");
//		Assert.assertTrue(this.selenium.isTextPresent(" Mistake 1"));
//		
//		// TODO: can't delete user!
//		User u = userDAO.retrieve(user);
//		if(u != null) {
//			em.getTransaction().begin();
//			userDAO.delete(u);
//			em.getTransaction().commit();
//		} else {
//			LOG.error("can't delete user!: " + user);
//		}
//	}
//
//	private String createRandomUser() {
//		Random r = new Random();
//		String token = Long.toString(Math.abs(r.nextLong()), 36);
//
//		return token;
//	}
//		
//}
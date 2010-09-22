package br.usp.ime.cogroo.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.User;

public class SecurityUtilTest {

	
	private EntityManager em;
	private UserDAO userDAO;
	private SecurityUtil security;
	private User dummy;

	@Test
	public void testCreateSecretKey() throws InvalidKeyException {
		KeyPair kp = security.genKeyPair();
		
		String key = security.genSecretKeyForUser(dummy, kp.getPublic().getEncoded());
		
		assertNotNull(key);
		
	}
	
	private static final String aStr = "Cogroo Comunidade";
	
	@Test
	public void testEncodeDecodeURLSafe() throws UnsupportedEncodingException {
		
		String encoded = security.encodeURLSafe(aStr.getBytes("UTF-8"));
		
		String decoded = new String(security.decodeURLSafe(encoded));
		
		assertEquals(aStr, decoded);
	}
	
	@Test
	public void testEncodeDecodeURLSafeString() throws UnsupportedEncodingException {
		
		String encoded = security.encodeURLSafe("Cogroo Comunidade");
		
		String decoded = security.decodeURLSafeString(encoded);
		
		assertEquals(aStr, decoded);
	}
	
	@Test
	public void testEncodeDecode() throws UnsupportedEncodingException {
		
		String encoded = security.encode("Cogroo Comunidade".getBytes("UTF-8"));
		
		String decoded = new String(security.decode(encoded));
		
		assertEquals(aStr, decoded);
	}
	
	@Before
	public void setup() {
		em = HSQLDBEntityManagerFactory.createEntityManager();
		this.dummy = new User("dummy");
		this.userDAO = new UserDAO(em);
		em.getTransaction().begin();
		userDAO.add(dummy);
		em.getTransaction().commit();
		
		this.security = new SecurityUtil();
	}
	
	@After
	public void tearDown() {
		em.close();
	}

}

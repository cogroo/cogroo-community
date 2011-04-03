package br.usp.ime.cogroo.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.jfree.util.Log;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.ShortUrl;

@Component
public class ShortUrlDAO {

	private EntityManager em;
	public static final String SHORTURL_ENTITY = ShortUrl.class.getName();

	public ShortUrlDAO(EntityManager e) {
		em = e;
	}

	public void add(ShortUrl shortUrl) {
		try {
			em.persist(shortUrl);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public ShortUrl retrieve(String toBeFound) {
		ShortUrl shortURL = null;
		try {
			shortURL = (ShortUrl) em.createQuery("from "+SHORTURL_ENTITY+" w where w.url=?").setParameter(1, toBeFound).getSingleResult();
		} catch (NoResultException e) {
			shortURL = null;
		} catch (RuntimeException e) {
			Log.error("Error retrieving url.", e);
		}
		return shortURL;
	}

}

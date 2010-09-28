package br.usp.ime.cogroo.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;

@Component
public class GrammarCheckerVersionDAO {
	
	private static final Logger LOG = Logger.getLogger(GrammarCheckerVersionDAO.class);

	private EntityManager mEntityManager;
	public static final String VERSION_ENTITY = GrammarCheckerVersion.class
			.getName();

	public GrammarCheckerVersionDAO(EntityManager aEntityManager) {
		this.mEntityManager = aEntityManager;
	}
	
	public GrammarCheckerVersion retrieve(Long id) {
		GrammarCheckerVersion version = mEntityManager.find(GrammarCheckerVersion.class, id);
		return version;
	}
	
	private void add(GrammarCheckerVersion version) {
		try {
			mEntityManager.persist(version);
		} catch (PersistenceException e) {
			mEntityManager.getTransaction().rollback();
			throw e;
		}
	}
	
	public GrammarCheckerVersion retrieve(String toBeFound) {
		GrammarCheckerVersion version = null;
		try {
			version = (GrammarCheckerVersion) mEntityManager.createQuery("from "+VERSION_ENTITY+" w where w.version=?").setParameter(1, toBeFound).getSingleResult();
		} catch (NoResultException e) {
			version = new GrammarCheckerVersion(toBeFound);
			add(version);
		} catch (RuntimeException e) {
			LOG.error("Error while retrieving version: " + toBeFound, e);
		}
		return version;
	}

}

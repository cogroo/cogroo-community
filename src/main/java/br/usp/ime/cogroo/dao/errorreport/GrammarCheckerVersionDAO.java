package br.usp.ime.cogroo.dao.errorreport;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;

@Component
public class GrammarCheckerVersionDAO {

	private EntityManager em;
	public static final String VERSION_ENTITY = GrammarCheckerVersion.class.getName();

	public GrammarCheckerVersionDAO(EntityManager e) {
		em = e;
	}

	public GrammarCheckerVersion retrieve(Long id) {
		GrammarCheckerVersion version = em.find(GrammarCheckerVersion.class, id);
		return version;
	}

	public void add(GrammarCheckerVersion version) {
		try {
			em.persist(version);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(GrammarCheckerVersion version) {
		em.merge(version);
	}

	public void delete(GrammarCheckerVersion version) {
		em.remove(version);
	}

	@SuppressWarnings("unchecked")
	public List<GrammarCheckerVersion> listAll() {
		return em.createQuery("from "+VERSION_ENTITY).getResultList();
	}
}

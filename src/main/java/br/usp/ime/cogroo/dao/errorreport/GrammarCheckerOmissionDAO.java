package br.usp.ime.cogroo.dao.errorreport;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;

@Component
public class GrammarCheckerOmissionDAO {

	private EntityManager em;
	public static final String OMISSION_ENTITY = GrammarCheckerOmission.class.getName();

	public GrammarCheckerOmissionDAO(EntityManager e) {
		em = e;
	}

	public GrammarCheckerOmission retrieve(Long id) {
		GrammarCheckerOmission error = em.find(GrammarCheckerOmission.class, id);
		return error;
	}

	public void add(GrammarCheckerOmission error) {
		try {
			em.persist(error);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(GrammarCheckerOmission error) {
		em.merge(error);
	}

	public void delete(GrammarCheckerOmission error) {
		em.remove(error);
	}

	@SuppressWarnings("unchecked")
	public List<GrammarCheckerOmission> listAll() {
		return em.createQuery("from "+OMISSION_ENTITY).getResultList();
	}
}

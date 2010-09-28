package br.usp.ime.cogroo.dao.errorreport;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;

@Component
public class GrammarCheckerBadInterventionDAO {

	private EntityManager em;
	public static final String BAD_INT_ENTITY = GrammarCheckerBadIntervention.class.getName();

	public GrammarCheckerBadInterventionDAO(EntityManager e) {
		em = e;
	}

	public GrammarCheckerBadIntervention retrieve(Long id) {
		GrammarCheckerBadIntervention error = em.find(GrammarCheckerBadIntervention.class, id);
		return error;
	}

	public void add(GrammarCheckerBadIntervention error) {
		try {
			em.persist(error);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(GrammarCheckerBadIntervention error) {
		em.merge(error);
	}

	public void delete(GrammarCheckerBadIntervention error) {
		em.remove(error);
	}

	@SuppressWarnings("unchecked")
	public List<GrammarCheckerBadIntervention> listAll() {
		return em.createQuery("from "+BAD_INT_ENTITY).getResultList();
	}
}

package br.usp.ime.cogroo.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.errorreport.HistoryEntry;

@Component
public class HistoryEntryDAO {

	private EntityManager em;
	public static final String COMMENT_ENTITY = HistoryEntry.class.getName();

	public HistoryEntryDAO(EntityManager e) {
		em = e;
	}

	public HistoryEntry retrieve(Long id) {
		HistoryEntry History = em.find(HistoryEntry.class, id);
		return History;
	}

	public void add(HistoryEntry History) {
		try {
			em.persist(History);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}
}

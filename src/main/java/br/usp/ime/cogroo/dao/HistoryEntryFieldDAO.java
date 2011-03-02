package br.usp.ime.cogroo.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.errorreport.HistoryEntryField;

@Component
public class HistoryEntryFieldDAO {

	private EntityManager em;
	public static final String COMMENT_ENTITY = HistoryEntryField.class.getName();

	public HistoryEntryFieldDAO(EntityManager e) {
		em = e;
	}

	public HistoryEntryField retrieve(Long id) {
		HistoryEntryField historyEntry = em.find(HistoryEntryField.class, id);
		return historyEntry;
	}

	public void add(HistoryEntryField historyEntry) {
		try {
			em.persist(historyEntry);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}
}

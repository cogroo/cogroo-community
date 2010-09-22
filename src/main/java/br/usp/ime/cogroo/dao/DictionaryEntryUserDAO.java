package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.DictionaryEntryUser;
import br.usp.ime.cogroo.model.User;

@Component
public class DictionaryEntryUserDAO {
	private EntityManager em;

	public DictionaryEntryUserDAO(EntityManager em) {
		this.em = em;
	}

	public void add(DictionaryEntryUser entry) {
		try {
			em.persist(entry);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public DictionaryEntryUser find(DictionaryEntry entry, User user) {
		DictionaryEntryUser entryUser = null;
		try {
			String query = "SELECT d from DictionaryEntryUser d WHERE d.dictionaryEntry.word.word=:word and d.dictionaryEntry.lemma.word.word=:lemma and d.dictionaryEntry.posTag.posTag=:posTag and d.user=:user";
			entryUser = (DictionaryEntryUser) em.createQuery(query)
					.setParameter("word", entry.getWord().getWord())
					.setParameter("lemma", entry.getLemma().getWord())
					.setParameter("posTag", entry.getPosTag().getPosTag())
					.setParameter("user", user).getSingleResult();

		} catch (NoResultException e) {
			entryUser = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return entryUser;
	}
	
	@SuppressWarnings("unchecked")
	public List<DictionaryEntryUser> findUsers(DictionaryEntry entry) {
		List<DictionaryEntryUser> entryUser = null;
		try {
			String query = "SELECT d from DictionaryEntryUser d WHERE d.dictionaryEntry.word.word=:word and d.dictionaryEntry.lemma.word.word=:lemma and d.dictionaryEntry.posTag.posTag=:posTag";
			entryUser = em.createQuery(query)
					.setParameter("word", entry.getWord().getWord())
					.setParameter("lemma", entry.getLemma().getWord())
					.setParameter("posTag", entry.getPosTag().getPosTag())
					.getResultList();

		} catch (NoResultException e) {
			entryUser = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return entryUser;
	}
	
	public void delete(DictionaryEntryUser dictionaryEntryUser){
		em.remove(dictionaryEntryUser);
	}

	@SuppressWarnings("unchecked")
	public List<DictionaryEntryUser> find(User user) {
		List<DictionaryEntryUser> entryUser = null;
		try {
			String query = "SELECT d from DictionaryEntryUser d WHERE d.user=:user";
			entryUser =  em.createQuery(query)
					.setParameter("user", user).getResultList();

		} catch (NoResultException e) {
			entryUser = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return entryUser;
	}
}

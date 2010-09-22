package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.Word;
import br.usp.ime.cogroo.model.WordUser;

@Component
public class WordDAO {

	private EntityManager em;
	public static final String WORD_ENTITY = Word.class.getName();

	public WordDAO(EntityManager e) {
		em = e;
	}

	public Word retrieve(Long id) {
		Word word = em.find(Word.class, id);
		return word;
	}

	public void add(Word word) {
		try {
			em.persist(word);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(Word word) {
		em.merge(word);
	}

	public void delete(Word word) {
		em.remove(word);
	}

	@SuppressWarnings("unchecked")
	public List<Word> listAll() {
		return em.createQuery("from "+WORD_ENTITY).getResultList();
	}

	public void addOrUpdate(Word word) {
		if (word.getId() == null || word.getId() == 0) {
			add(word);
		} else {
			update(word);
		}
	}

	public boolean existe(String toBeFound) {
		return (retrieve(toBeFound) != null); 
	}

	public Word retrieve(String toBeFound) {
		Word word = null;
		try {
			word = (Word) em.createQuery("from "+WORD_ENTITY+" w where w.word=?").setParameter(1, toBeFound).getSingleResult();
		} catch (NoResultException e) {
			word = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO: HANDLE ERROR
		}
		return word;
	}

	public WordUser retrieve(String toBeFound, User user) {
		WordUser word = null;
		try {
			word = (WordUser) em.createQuery("select w from WordUser w where w.word.word=?1 and w.user.id=?2").setParameter(1, toBeFound).setParameter(2, user.getId()).getSingleResult();
		} catch (NoResultException e) {
			word = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO: HANDLE ERROR
		}
		return word;
	}

}

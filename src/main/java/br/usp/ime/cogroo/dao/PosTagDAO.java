package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.User;

@Component
public class PosTagDAO {
	private EntityManager em;
	public static final String POSTAG_ENTITY = PosTag.class.getName();

	public PosTagDAO(EntityManager e) {
		em = e;
	}

	public PosTag retrieve(Long id) {
		PosTag posTag = em.find(PosTag.class, id);
		return posTag;
	}

	public void add(PosTag posTag) {
		try {
			em.persist(posTag);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void delete(PosTag posTag) {
		em.remove(posTag);

	}

	@SuppressWarnings("unchecked")
	public List<PosTag> listAll() {
		return em.createQuery("from " + POSTAG_ENTITY).getResultList();
	}

	public PosTag retrieve(String toBeFound) {
		PosTag posTag = null;
		try{
			posTag = (PosTag) em.createQuery("from "+POSTAG_ENTITY+" p where posTag=?").setParameter(1,toBeFound).getSingleResult();			
		}catch(NoResultException e){
			posTag = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return posTag;		
	}
	
	public PosTag retrieve(String toBeFound, User user) {
		PosTag posTag = null;
		try{
			posTag = (PosTag) em.createQuery("from "+POSTAG_ENTITY+" p where posTag=?1 and user=?2").setParameter(1,toBeFound).setParameter(2, user).getSingleResult();			
		}catch(NoResultException e){
			posTag = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return posTag;
	}

	@SuppressWarnings("unchecked")
	public List<PosTag> listPosTag(String word) {
		String query = "SELECT l.posTag from "+DictionaryEntryDAO.DICTIONARY_ENTRY_ENTITY+" l, Word w WHERE l.lemma = w.word.id and w.word = ?";
		List<PosTag> listPosTag = em.createQuery(query).setParameter(1, word).getResultList();
		return listPosTag;
	}



}

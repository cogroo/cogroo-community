package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.Word;

@Component
public class DictionaryEntryDAO {

	private EntityManager em;
	public static final String DICTIONARY_ENTRY_ENTITY = DictionaryEntry.class.getName();
	private static final Logger LOG = Logger.getLogger(DictionaryEntryDAO.class);

	public DictionaryEntryDAO(EntityManager e) {
		em = e;
	}
    // TODO 
	public DictionaryEntry retrieve(Word word, Word primitive, PosTag posTag) {
		DictionaryEntry actual = new DictionaryEntry(word, primitive,posTag);
		DictionaryEntry received = em.find(DictionaryEntry.class, actual.getId());
		return received;
	}

	public void add(DictionaryEntry masterDictionaryEntry) {
		try {
			em.persist(masterDictionaryEntry);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void delete(DictionaryEntry masterDictionaryEntry) {
		em.remove(masterDictionaryEntry);

	}

	@SuppressWarnings("unchecked")
	public List<DictionaryEntry> listAll() {
		return em.createQuery("from " + DICTIONARY_ENTRY_ENTITY ).getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<DictionaryEntry> listByWord(String word) {
		String query = "SELECT l from " + DICTIONARY_ENTRY_ENTITY + " l, Word w WHERE l.word = w.word.id and w.word = ?";
		List<DictionaryEntry> listMasterDictionaryEntry = (List<DictionaryEntry>)em.createQuery(query).setParameter(1, word).getResultList();
		return listMasterDictionaryEntry;
	}
	@SuppressWarnings("unchecked")
	public List<DictionaryEntry> listByLemma(String word) {
		String query = "SELECT l from " + DICTIONARY_ENTRY_ENTITY + " l, Word w WHERE l.lemma = w.word.id and w.word = ?";
		List<DictionaryEntry> listMasterDictionaryEntry = (List<DictionaryEntry>)em.createQuery(query).setParameter(1, word).getResultList();
		return listMasterDictionaryEntry;
	}
	public DictionaryEntry find(String word, String lemma, String posTag) {
		DictionaryEntry dictionaryEntry = null;
		try {
			String query = "SELECT d from DictionaryEntry d WHERE d.word.word=:word and d.lemma.word=:lemma and d.posTag.posTag=:posTag";
			dictionaryEntry = (DictionaryEntry)em.createQuery(query).setParameter("word", word).setParameter("lemma", lemma).setParameter("posTag", posTag).getSingleResult();
		} catch (NoResultException e) {
			dictionaryEntry = null;
		} catch (RuntimeException e) {
			LOG.error("Error getting dictionaryEntry.", e);
			// TODO: handle exception
		}
		return dictionaryEntry;
	}
    
	
	
}

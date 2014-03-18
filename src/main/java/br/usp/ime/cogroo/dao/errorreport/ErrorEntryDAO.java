package br.usp.ime.cogroo.dao.errorreport;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;

@Component
public class ErrorEntryDAO {

	private EntityManager em;
	public static final String ERROR_ENTITY = ErrorEntry.class.getName();

	public ErrorEntryDAO(EntityManager e) {
		em = e;
	}

	public ErrorEntry retrieve(Long id) {
		ErrorEntry error = em.find(ErrorEntry.class, id);
		return error;
	}

	public void add(ErrorEntry error) {
		try {
			em.persist(error);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(ErrorEntry error) {
		em.merge(error);
	}

	public void delete(ErrorEntry error) {
		em.remove(error);
	}

	public long count() {
		return (Long) em.createQuery("SELECT count(*) from " + ERROR_ENTITY).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<ErrorEntry> listAll() {
		return em.createQuery("from "+ERROR_ENTITY).getResultList();
	}

  public long count(User submitter) {
    return (Long) em.createQuery("SELECT count(*) from " + ERROR_ENTITY + " e where e.submitter = " + submitter.getId()).getSingleResult();
  }
}

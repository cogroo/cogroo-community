package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.ErrorReport;

@Component
public class ErrorReportDAO {

	private EntityManager em;
	public static final String ERROR_REPORT_ENTITY = ErrorReport.class.getName();

	public ErrorReportDAO(EntityManager e) {
		em = e;
	}

	public ErrorReport retrieve(Long id) {
		ErrorReport errorReport = em.find(ErrorReport.class, id);
		return errorReport;
	}

	public void add(ErrorReport errorReport) {
		try {
			em.persist(errorReport);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(ErrorReport errorReport) {
		em.merge(errorReport);
	}

	public void delete(ErrorReport errorReport) {
		em.remove(errorReport);
	}

	@SuppressWarnings("unchecked")
	public List<ErrorReport> listAll() {
		return em.createQuery("from "+ERROR_REPORT_ENTITY).getResultList();
	}
}

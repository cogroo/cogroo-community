package br.usp.ime.cogroo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;

@Component
public class UserDAO {

	private EntityManager em;
	public static final String USER_ENTITY = User.class.getName();

	public UserDAO(EntityManager e) {
		em = e;
	}

	public User retrieve(Long id) {
		User user = em.find(User.class, id);
		return user;
	}

	public void add(User user) {
		try {
			em.persist(user);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(User user) {
		em.merge(user);
	}

	public void delete(User user) {
		em.remove(user);
	}

	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return em.createQuery("from " + USER_ENTITY).getResultList();
	}
	
	public long count() {
		return (Long) em.createQuery("SELECT count(*) from " + USER_ENTITY).getSingleResult();
	}
	
	public long countLoginLater(long lastLogin) {
		return (Long) em
				.createQuery(
						"SELECT count(*) from " + USER_ENTITY
								+ " where lastLogin > ?")
				.setParameter(1, lastLogin).getSingleResult();
	}
	
	public long countLogged() {
		return (Long) em.createQuery(
				"SELECT count(*) from " + USER_ENTITY + " where logged = true")
				.getSingleResult();
	}

	public boolean exist(String toBeFound) {
		return (retrieveByLogin(toBeFound) != null);
	}

	public User retrieveByLogin(String toBeFound) {
		return retrieve(toBeFound, "login");
	}

	public User retrieveByEmail(String toBeFound) {
		return retrieve(toBeFound, "email");
	}

	private User retrieve(String value, String field) {
		User user = null;
		try {
			user = (User) em.createQuery(
					"from " + USER_ENTITY + " w where w." + field + "=?")
					.setParameter(1, value).getSingleResult();
		} catch (NoResultException e) {
			user = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO: HANDLE ERROR
		}
		return user;
	}

}

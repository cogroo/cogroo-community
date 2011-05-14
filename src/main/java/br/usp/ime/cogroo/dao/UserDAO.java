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
	
	public long count() {
		return (Long) em.createQuery("SELECT count(*) from " + USER_ENTITY).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return em.createQuery("from " + USER_ENTITY).getResultList();
	}
	
	public long countLoginLater(long lastLogin) {
		return (Long) em
				.createQuery(
						"SELECT count(*) from " + USER_ENTITY
								+ " where lastLogin > ?")
				.setParameter(1, lastLogin).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<User> retrieveTopUsers(long lastLogin, int n) {
		return em
				.createQuery(
						"from "
								+ USER_ENTITY
								+ " where lastLogin > ? order by lastLogin desc")
				.setParameter(1, lastLogin).setMaxResults(n).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<User> retrieveIdleUsers(long lastLogin, int n) {
		return em
				.createQuery(
						"from " + USER_ENTITY + " where lastLogin < ? order by lastLogin")
				.setParameter(1, lastLogin).setMaxResults(n).getResultList();
	}
	
	@Deprecated
	public boolean exist(String login) {
	return (retrieveByLogin("cogroo", login) != null);
}

	public boolean exist(String service, String login) {
		return (retrieveByLogin(service, login) != null);
	}
	
	@Deprecated
	public User retrieveByLogin(String login) {
		return retrieve("cogroo", "service", login, "login");
	}

	public User retrieveByLogin(String service, String login) {
		return retrieve(service, "service", login, "login");
	}
	
	@Deprecated
	public User retrieveByEmail(String email) {
		return retrieve("cogroo", "service", email, "email");
	}

	public User retrieveByEmail(String service, String email) {
		return retrieve(service, "service", email, "email");
	}

	private User retrieve(String value1, String field1, String value2, String field2) {
		User user = null;
		try {
			user = (User) em.createQuery(
					"from " + USER_ENTITY + " w where w." + field1 + "=?1 and w." + field2 + "=?2")
					.setParameter(1, value1).setParameter(2, value2).getSingleResult();
		} catch (NoResultException e) {
			user = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO: HANDLE ERROR
		}
		return user;
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

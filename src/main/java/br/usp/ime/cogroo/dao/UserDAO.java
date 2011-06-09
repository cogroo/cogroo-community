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

	public boolean existLogin(String provider, String login) {
		return (retrieveAll(provider, "provider", login, "login").size() > 0);
	}
	
	public boolean existEmail(String provider, String email) {
		return (retrieveAll(provider, "provider", email, "email").size() > 0);
	}
	
	@Deprecated
	public User retrieveByLogin(String login) {
		return retrieveSingle("cogroo", "provider", login, "login");
	}

	public User retrieveByLogin(String provider, String login) {
		return retrieveSingle(provider, "provider", login, "login");
	}
	
	@Deprecated
	public User retrieveByEmail(String email) {
		return retrieveSingle("cogroo", "provider", email, "email");
	}

	public User retrieveByEmail(String provider, String email) {
		return retrieveSingle(provider, "provider", email, "email");
	}

	private User retrieveSingle(String value1, String field1, String value2, String field2) {
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
	
	@SuppressWarnings("unchecked")
	private List<User> retrieveAll(String value1, String field1, String value2, String field2) {
		List<User> users = null;
		try {
			users = (List<User>) em.createQuery(
					"from " + USER_ENTITY + " w where w." + field1 + "=?1 and w." + field2 + "=?2")
					.setParameter(1, value1).setParameter(2, value2).getResultList();
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO: HANDLE ERROR
		}
		return users;
	}
	
	private User retrieveSingle(String value, String field) {
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

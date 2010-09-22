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
		return em.createQuery("from "+USER_ENTITY).getResultList();
	}

	public boolean existe(String toBeFound) {
		return (retrieve(toBeFound) != null); 
	}

	public User retrieve(String toBeFound) {
		User user = null;
		try {
			user = (User) em.createQuery("from "+USER_ENTITY+" w where w.name=?").setParameter(1, toBeFound).getSingleResult();
		} catch (NoResultException e) {
			user = null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO: HANDLE ERROR
		}
		return user;
	}
}

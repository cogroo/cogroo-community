package br.usp.ime.cogroo.dao.errorreport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.errorreport.Comment;

@Component
public class CommentDAO {

	private EntityManager em;
	public static final String COMMENT_ENTITY = Comment.class.getName();

	public CommentDAO(EntityManager e) {
		em = e;
	}

	public Comment retrieve(Long id) {
		Comment comment = em.find(Comment.class, id);
		return comment;
	}

	public void add(Comment comment) {
		try {
			em.persist(comment);
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void update(Comment comment) {
		em.merge(comment);
	}

	public void delete(Comment comment) {
		for (Comment answer : comment.getAnswers()) {
			em.remove(answer);
		}
		em.remove(comment);
	}
}

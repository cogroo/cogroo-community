package br.usp.ime.cogroo.model.errorreport;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.ime.cogroo.model.User;

@Entity
public class Comment {
	
	@Id
	@GeneratedValue
	private Long id;
	
	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(nullable = false, insertable = false, updatable = false)
	@ManyToOne
	private User user;
	
	private Date date;
	
	private String comment;
	
	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(nullable = false, insertable = false, updatable = false)
	@ManyToOne
	private ErrorEntry errorEntry;

	
	public Comment() {
		// TODO Auto-generated constructor stub
	}
	
	public Comment(User user, Date date, String comment, ErrorEntry errorEntry) {
		super();
		this.user = user;
		this.date = date;
		this.comment = comment;
		this.errorEntry = errorEntry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getErrorEntryComment() {
		return comment;
	}

	public void setErrorEntryComment(String comment) {
		this.comment = comment;
	}

	public void setErrorEntry(ErrorEntry errorEntry) {
		this.errorEntry = errorEntry;
	}

	public ErrorEntry getErrorEntry() {
		return errorEntry;
	}
	
	@Override
	public String toString() {
		return "user: " + getUser() + " date: " + getDate() + " comment: " + getErrorEntryComment();
	}
	
}

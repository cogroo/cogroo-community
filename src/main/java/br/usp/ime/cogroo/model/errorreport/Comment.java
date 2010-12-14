package br.usp.ime.cogroo.model.errorreport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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
	
	@Column(length=700)
	private String comment;
	
	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(nullable = false, insertable = false, updatable = false)
	@ManyToOne
	private ErrorEntry errorEntry;
	
	@ManyToOne
	private Comment question;

	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	private List<Comment> answers = new ArrayList<Comment>();
	
	@Transient
	private boolean isNew = false;
	
	public Comment() {
		// TODO Auto-generated constructor stub
	}
	
	public Comment(User user, Date date, String comment, ErrorEntry errorEntry, List<Comment> answers) {
		super();
		this.user = user;
		this.date = date;
		this.comment = comment;
		this.errorEntry = errorEntry;
		this.answers = answers;
	}
	
	public Comment(User user, Date date, String comment, Comment question, List<Comment> answers) {
		super();
		this.user = user;
		this.date = date;
		this.comment = comment;
		this.errorEntry = null;
		this.question = question;
		this.answers = answers;
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
	
	
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Comment> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Comment> answers) {
		this.answers = answers;
	}
	
	@Transient
	public boolean isNew() {
		return isNew;
	}
	
	@Transient
	public void setIsNew(boolean value) {
		isNew = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("user: " + getUser() + " date: " + getDate() + " comment: " + getErrorEntryComment());
		
		if(getAnswers() != null && getAnswers().size() > 0 ) {
			for (Comment answer : getAnswers()) {
				sb.append("\n\tAnswer: " + answer);
			}
		}
		return sb.toString();
	}

	public void setQuestion(Comment question) {
		this.question = question;
	}

	public Comment getQuestion() {
		return question;
	}
	
	public int getCount() {
		int count = 1;
		for (Comment comment : getAnswers()) {
			count += comment.getCount();
		}
		return count;
	}
	
}

package br.usp.ime.cogroo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class WordUser {
	
	@EmbeddedId
	private IdFK id = new IdFK();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, insertable = false, updatable = false)
	private Word word;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, insertable = false, updatable = false)
	private User user;

	private boolean deleted = false;
	
	public WordUser(Word word, User user) {
		setWord(word);
		setUser(user);
		// Guarantee referential integrity
		word.getWordUserList().add(this);
		user.getWordUserList().add(this);

	}

	public void setWord(Word word) {
		this.word = word;
		id.wordId = word.getId();
	}

	public Word getWord() {
		return word;
	}

	public void setUser(User user) {
		this.user = user;
		id.userId = user.getId();
	}

	public User getUser() {
		return user;
	}
	
	public void setId(IdFK id) {
		this.id = id;
	}

	public IdFK getId() {
		return id;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Embeddable
	public static class IdFK implements Serializable {
		private static final long serialVersionUID = 1L;
		@Column(name = "word_id", nullable=false)
		private Long wordId;
		@Column(name = "user_id", nullable=false)
		private Long userId;

		public IdFK() {
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((userId == null) ? 0 : userId.hashCode());
			result = prime * result
					+ ((wordId == null) ? 0 : wordId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IdFK other = (IdFK) obj;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			if (wordId == null) {
				if (other.wordId != null)
					return false;
			} else if (!wordId.equals(other.wordId))
				return false;
			return true;
		}

		public IdFK(Long wordId, Long userId) {
			this.wordId = wordId;
			this.userId = userId;
		}

	}

}

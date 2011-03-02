package br.usp.ime.cogroo.model.errorreport;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.usp.ime.cogroo.model.User;

@Entity
public class HistoryEntry {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Date creation;
	
	@ManyToOne
	private User user;
	
	@OneToMany(mappedBy = "historyEntry", cascade = CascadeType.ALL)
	private List<HistoryEntryField> historyEntryField;
	
	@ManyToOne
	private ErrorEntry errorEntry;
	
	public HistoryEntry() {
	}

	public HistoryEntry(User user, Date creation, List<HistoryEntryField> historyEntryField, ErrorEntry errorEntry) {
		this.creation = creation;
		this.historyEntryField = historyEntryField;
		this.errorEntry = errorEntry;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public List<HistoryEntryField> getHistoryEntryField() {
		return historyEntryField;
	}

	public void setHistoryEntryField(List<HistoryEntryField> historyEntryField) {
		this.historyEntryField = historyEntryField;
	}

	public ErrorEntry getErrorEntry() {
		return errorEntry;
	}

	public void setErrorEntry(ErrorEntry errorEntry) {
		this.errorEntry = errorEntry;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}

package br.usp.ime.cogroo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ErrorReport {

	@Id
	@GeneratedValue
	private Long id;

	private String sampleText;

	@OneToMany
	private List<Comment> comments = new ArrayList<Comment>();

	private String version;

	@ManyToOne
	private User submitter;

	private Date creation;

	private Date modified;

	private Boolean isFalsePositive;

	private Boolean isSolved;

	public ErrorReport() {
	}

	public ErrorReport(String sampleText, List<Comment> comments,
			String version, User submitter, Date creation, Date modified,
			Boolean isFalsePositive, Boolean isSolved) {
		this.sampleText = sampleText;
		this.comments = comments;
		this.version = version;
		this.submitter = submitter;
		this.setCreation(creation);
		this.setModified(modified);
		this.isFalsePositive = isFalsePositive;
		this.setIsSolved(isSolved);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSampleText() {
		return sampleText;
	}

	public void setSampleText(String sampleText) {
		this.sampleText = sampleText;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public User getSubmitter() {
		return submitter;
	}

	public void setSubmitter(User submitter) {
		this.submitter = submitter;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Boolean getIsFalsePositive() {
		return isFalsePositive;
	}

	public void setIsFalsePositive(Boolean isFalsePositive) {
		this.isFalsePositive = isFalsePositive;
	}

	public Boolean getIsSolved() {
		return isSolved;
	}

	public void setIsSolved(Boolean isSolved) {
		this.isSolved = isSolved;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id + "\n");
		sb.append("submitter: " + submitter + "\n");
		sb.append("sampleText: " + sampleText + "\n");
		sb.append("version: " + version + "\n");
		sb.append("creation: " + creation + "\n");
		sb.append("modified: " + modified + "\n");
		sb.append("isFalsePositive: " + isFalsePositive + "\n");
		sb.append("isSolved: " + isSolved + "\n");
		sb.append("comments: " + "\n");
		if(comments != null) {
			for (Comment comment : comments) {
				sb.append("   " + comment + "\n");
			}
		}
		return sb.toString();
	}

}

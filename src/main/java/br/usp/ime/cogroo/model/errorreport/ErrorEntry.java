package br.usp.ime.cogroo.model.errorreport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import br.usp.ime.cogroo.model.Comment;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;
import br.usp.ime.cogroo.model.User;

@Entity
public class ErrorEntry {

	@Id
	@GeneratedValue
	private Long id;

	private String text;
	
	private int spanStart;
	
	private int spanEnd;

	@OneToMany
	private List<Comment> comments = new ArrayList<Comment>();

	@ManyToOne
	private GrammarCheckerVersion version;
	
	@ManyToOne
	private User submitter;

	private Date creation;

	private Date modified;
	
	@OneToOne
	private GrammarCheckerBadIntervention badIntervention;
	
	@OneToOne
	private GrammarCheckerOmission omissions;

	public ErrorEntry(String text, int start, int end, List<Comment> comments,
			GrammarCheckerVersion version, User submitter, Date creation,
			Date modified, GrammarCheckerBadIntervention badIntervention,
			GrammarCheckerOmission omissions) {
		this.text = text;
		this.spanStart = start;
		this.spanEnd = end;
		this.comments = comments;
		this.version = version;
		this.submitter = submitter;
		this.creation = creation;
		this.modified = modified;
		this.badIntervention = badIntervention;
		this.omissions = omissions;
	}

	public ErrorEntry() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public GrammarCheckerVersion getVersion() {
		return version;
	}

	public void setVersion(GrammarCheckerVersion version) {
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
	
	public GrammarCheckerBadIntervention getBadIntervention() {
		return badIntervention;
	}

	public void setBadIntervention(GrammarCheckerBadIntervention badIntervention) {
		this.badIntervention = badIntervention;
	}

	public GrammarCheckerOmission getOmissions() {
		return omissions;
	}

	public void setOmissions(GrammarCheckerOmission omissions) {
		this.omissions = omissions;
	}

	public int getStart() {
		return spanStart;
	}

	public void setSpanStart(int start) {
		this.spanStart = start;
	}

	public int getEnd() {
		return spanEnd;
	}

	public void setSpanEnd(int end) {
		this.spanEnd = end;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id + "\n");
		sb.append("submitter: " + submitter + "\n");
		sb.append("text: " + text + "\n");
		sb.append("version: " + version + "\n");
		sb.append("creation: " + creation + "\n");
		sb.append("modified: " + modified + "\n");
		sb.append("comments: " + "\n");
		if(comments != null) {
			for (Comment comment : comments) {
				sb.append("   " + comment + "\n");
			}
		}
		return sb.toString();
	}

}

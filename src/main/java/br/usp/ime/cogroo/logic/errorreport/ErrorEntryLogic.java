package br.usp.ime.cogroo.logic.errorreport;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.Messages;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
import br.usp.ime.cogroo.dao.HistoryEntryDAO;
import br.usp.ime.cogroo.dao.HistoryEntryFieldDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerBadInterventionDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerOmissionDAO;
import br.usp.ime.cogroo.exceptions.CommunityException;
import br.usp.ime.cogroo.exceptions.CommunityExceptionMessages;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.BadInterventionClassification;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;
import br.usp.ime.cogroo.model.errorreport.HistoryEntry;
import br.usp.ime.cogroo.model.errorreport.HistoryEntryField;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;
import br.usp.ime.cogroo.util.BuildUtil;
import br.usp.pcs.lta.cogroo.errorreport.model.BadIntervention;
import br.usp.pcs.lta.cogroo.errorreport.model.ErrorReport;
import br.usp.pcs.lta.cogroo.errorreport.model.Omission;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;

@Component
public class ErrorEntryLogic {

	private static final Logger LOG = Logger.getLogger(ErrorEntryLogic.class);
	private ErrorEntryDAO errorEntryDAO;
	private UserDAO userDAO;
	private CommentDAO commentDAO;
	private User user;
	private CogrooFacade cogrooFacade;
	private GrammarCheckerVersionDAO versionDAO;
	private GrammarCheckerOmissionDAO omissionDAO;
	private GrammarCheckerBadInterventionDAO badInterventionDAO;
	private HistoryEntryDAO historyEntryDAO;
	private HistoryEntryFieldDAO historyEntryFieldDAO;
	private ApplicationData appData;

	public ErrorEntryLogic(LoggedUser loggedUser, ErrorEntryDAO errorEntryDAO,
			UserDAO userDAO, CommentDAO commentDAO, CogrooFacade cogrooFacade,
			GrammarCheckerVersionDAO versionDAO, GrammarCheckerOmissionDAO omissionDAO,
			GrammarCheckerBadInterventionDAO badInterventionDAO, HistoryEntryDAO historyEntryDAO, HistoryEntryFieldDAO historyEntryFieldDAO, ApplicationData appData) {
		this.userDAO = userDAO;
		this.commentDAO = commentDAO;
		this.errorEntryDAO = errorEntryDAO;
		this.user = loggedUser.getUser();
		this.cogrooFacade = cogrooFacade;
		this.versionDAO = versionDAO;
		this.omissionDAO = omissionDAO;
		this.badInterventionDAO = badInterventionDAO;
		this.historyEntryDAO = historyEntryDAO;
		this.historyEntryFieldDAO = historyEntryFieldDAO;
		this.appData = appData;
	}

	public List<ErrorEntry> getAllReports() {
		// set isNew property!
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -7);
		Date oneWeekAgo = now.getTime();
		List<ErrorEntry> errors = errorEntryDAO.listAll();

		for (ErrorEntry errorEntry : errors) {

			if(this.user == null && oneWeekAgo.before(errorEntry.getModified()) ) {

				errorEntry.setIsNew(true);
			} else if(this.user != null && this.user.getPreviousLogin().before(errorEntry.getModified())) {
				// if comments is empty, check only the submitter
				if(errorEntry.getComments().size() == 0) {
					if(!errorEntry.getSubmitter().getLogin().equals(this.user.getLogin())) {
						errorEntry.setIsNew(true);
					}
				}
				
				else {
					// we check the comments. we skip if the newest is of this user
					Date newest = this.user.getPreviousLogin();
					boolean isNew = false;
					for (Comment comment : errorEntry.getComments()) {
						
						for(Comment answer : comment.getAnswers()) {
							if(answer.getDate().after(newest)) {
								newest = answer.getDate();
								if(!answer.getUser().getLogin().equals(this.user.getLogin())) {
									isNew = true;
								} else {
									isNew = false;
								}
							}
						}
						if(comment.getDate().after(newest)) {
							newest = comment.getDate();
							if(!comment.getUser().getLogin().equals(this.user.getLogin())) {
								isNew = true;
							} else {
								isNew = false;
							}
						}

					}
					if(isNew) {
						errorEntry.setIsNew(true);
					}
				}

			}
		}
		return errors;
	}

	public SortedSet<String> getErrorCategoriesForUser(String userName) {
		 // TODO implement for user
		 return getErrorCategoriesForUser();
	}
	
	public SortedSet<String> getErrorCategoriesForUser() {
		 SortedSet<String> uniqueRules = new TreeSet<String>();
		
		 List<Rule> rules = new
		 RulesContainerHelper(getClass().getResource("/").getPath()).getContainerForXMLAccess().getComponent(RulesProvider.class).getRules().getRule();
		 for (Rule rule : rules) {
		 uniqueRules.add(rule.getType());
		 }
		
		 return uniqueRules;
	}

	public List<ErrorEntry> addErrorEntry(String username, String error)
			throws CommunityException {
		List<ErrorEntry> list = new ArrayList<ErrorEntry>();

		// try to get user, or create it
		User cogrooUser;
		if (userDAO.exist(username)) {
			cogrooUser = userDAO.retrieveByLogin(username);
		} else {
			LOG.error("Invalid user in addErrorEntry:" + username);
			throw new CommunityException(
					CommunityExceptionMessages.INVALID_USER,
					new Object[] { username });
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("Got user:" + cogrooUser);
		}
		ErrorReport er = cogrooFacade.getErrorReportAccess().getErrorReport(
				new StringReader(error));
		GrammarCheckerVersion version = versionDAO.retrieve(er.getVersion());
		
		Date time = new Date();
		
		// we split the report in several new entries...
		
		if(er.getOmissions() != null) {
			List<Omission> omissions = er.getOmissions().getOmission();
			if(omissions != null) {
				for (Omission omission : omissions) {
					
					ErrorEntry errorEntry = new ErrorEntry(
							er.getText(), 
							omission.getSpan().getStart(), 
							omission.getSpan().getEnd(), 
							new ArrayList<Comment>(),
							version, 
							cogrooUser, 
							time, 
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);
					
					if(omission.getComment() != null && omission.getComment().length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, omission.getComment(), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}
					
					GrammarCheckerOmission gcOmission = new GrammarCheckerOmission(
							omission.getCategory(), 
							omission.getCustomCategory(), 
							omission.getReplaceBy(), 
							errorEntry);
					omissionDAO.add(gcOmission);
					errorEntry.setOmissions(gcOmission);
					
					errorEntryDAO.add(errorEntry);
					appData.incReportedErrors();
					list.add(errorEntry);
				}
			}
		}
		
		if(er.getBadInterventions() != null) {
			List<BadIntervention> badInterventions = er.getBadInterventions().getBadIntervention();
			if(badInterventions != null) {
				for (BadIntervention badIntervention : badInterventions) {
					
					ErrorEntry errorEntry = new ErrorEntry(
							er.getText(), 
							badIntervention.getSpan().getStart(), 
							badIntervention.getSpan().getEnd(), 
							null,
							version, 
							cogrooUser, 
							time, 
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);
					
					if(badIntervention.getComment() != null && badIntervention.getComment().length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, badIntervention.getComment(), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}
					
					BadInterventionClassification classification = null;
					switch (badIntervention.getClassification()) {
						case FALSE_ERROR:
							classification = BadInterventionClassification.FALSE_ERROR;
							break;
						case INAPPROPRIATE_DESCRIPTION:
							classification = BadInterventionClassification.INAPPROPRIATE_DESCRIPTION;
							break;
						case INAPPROPRIATE_SUGGESTION:
							classification = BadInterventionClassification.INAPPROPRIATE_SUGGESTION;
							break;
						default:
							break;
					} 
					GrammarCheckerBadIntervention gcBadIntervention = new GrammarCheckerBadIntervention(
							classification, 
							badIntervention.getRule(),
							errorEntry);
					
					badInterventionDAO.add(gcBadIntervention);
					errorEntry.setBadIntervention(gcBadIntervention);
					
					errorEntryDAO.add(errorEntry);
					appData.incReportedErrors();
					list.add(errorEntry);
				}
			}
		}

		return list;
	}
	
	public void addErrorEntry(
			User cogrooUser,
			String text,
			List<String> badint,
			List<String> badintComments,
			List<String> badintStart,
			List<String> badintEnd, 
			List<String> badintRule, 
			List<String> omissionClassification,
			List<String> customOmissionText,
			List<String> omissionComment,
			List<String> omissionReplaceBy,
			List<String> omissionStart,
			List<String> omissionEnd) {
		
		GrammarCheckerVersion version = versionDAO.retrieve("c" + BuildUtil.POM_VERSION);
		
		Date time = new Date();
		
		// we split the report in several new entries...
		
		if(omissionClassification != null) {
			for (int i = 0; i < omissionClassification.size(); i++) {
					ErrorEntry errorEntry = new ErrorEntry(
							text,  
							Integer.parseInt(omissionStart.get(i)), 
							Integer.parseInt(omissionEnd.get(i)), 
							new ArrayList<Comment>(),
							version, 
							cogrooUser, 
							time, 
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);
					
					if(omissionComment.get(i) != null && omissionComment.get(i).length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, omissionComment.get(i), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}
					
					String classification = null;
					String customClass = null;
					
					if(omissionClassification.get(i).equals("custom")) {
						classification = null;
						customClass = customOmissionText.get(i);
					} else {
						classification = omissionClassification.get(i);
						customClass = null;
					}
					
					GrammarCheckerOmission gcOmission = new GrammarCheckerOmission(
							classification, 
							customClass,
							omissionReplaceBy.get(i), 
							errorEntry);
					omissionDAO.add(gcOmission);
					errorEntry.setOmissions(gcOmission);
					
					errorEntryDAO.add(errorEntry);
					appData.incReportedErrors();

					LOG.debug("Added errorEntry:" + errorEntry);
				}
		}
		
		if(badint != null) {
			for (int i = 0; i < badint.size(); i++) {
				if( !badint.get(i).equals("ok") ) {
					ErrorEntry errorEntry = new ErrorEntry(
							text, 
							Integer.parseInt(badintStart.get(i)), 
							Integer.parseInt(badintEnd.get(i)), 
							null,
							version, 
							cogrooUser, 
							time, 
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);
					
					if(badintComments.get(i) != null && badintComments.get(i).length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, badintComments.get(i), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}
					
					BadInterventionClassification classification = BadInterventionClassification.fromValue(badint.get(i));
					
					GrammarCheckerBadIntervention gcBadIntervention = new GrammarCheckerBadIntervention(
							classification, 
							Integer.parseInt(badintRule.get(i)),
							errorEntry);
					
					badInterventionDAO.add(gcBadIntervention);
					errorEntry.setBadIntervention(gcBadIntervention);
					
					errorEntryDAO.add(errorEntry);
					appData.incReportedErrors();
					LOG.debug("Added errorEntry:" + errorEntry);
				}
			}
		}

		
	}
	
	public List<HistoryEntryField> generateHistory(GrammarCheckerOmission before, GrammarCheckerOmission after) {
		
		List<HistoryEntryField> h = new ArrayList<HistoryEntryField>();
		
		HistoryEntryField category = new HistoryEntryField();
		category.setFieldName(Messages.OMISSION_CATEGORY);
		if(before != null) {
			category.setBefore(before.getCategory());
		}
		if(after != null) {
			category.setAfter(after.getCategory());
		}
		
		HistoryEntryField custom = new HistoryEntryField();
		category.setFieldName(Messages.OMISSION_CUSTOM_CATEGORY);
		if(before != null) {
			category.setBefore(before.getCustomCategory());
		}
		if(after != null) {
			category.setAfter(after.getCustomCategory());
		}
		
		HistoryEntryField replace = new HistoryEntryField();
		category.setFieldName(Messages.OMISSION_REPLACE_BY);
		if(before != null) {
			category.setBefore(before.getReplaceBy());
		}
		if(after != null) {
			category.setAfter(after.getReplaceBy());
		}
		
		h.add(category);
		h.add(custom);
		h.add(replace);
		
		return h;
	}
	
	public void updateBadIntervention(ErrorEntry er) {
		List<HistoryEntryField> h = new ArrayList<HistoryEntryField>();
		
		if(er.getOmission() != null) {
//			h.addAll(generateHistory(er.getOmission(), null));
			this.omissionDAO.delete(er.getOmission());
			er.setOmission(null);
		}
		if(er.getBadIntervention().getId() == null) {
//			h.addAll(generateHistory(null, er.getBadIntervention()));
			this.badInterventionDAO.add(er.getBadIntervention());
		} else {
			this.badInterventionDAO.update(er.getBadIntervention());
		}
		
		this.updateModified(er);
		
		this.errorEntryDAO.update(er);
	}

	public void updateOmission(ErrorEntry er) {
		if(er.getBadIntervention() != null) {
			this.badInterventionDAO.delete(er.getBadIntervention());
			er.setBadIntervention(null);
		}
		if(er.getOmission().getId() == null) {
			this.omissionDAO.add(er.getOmission());
		} else {
			this.omissionDAO.update(er.getOmission());
		}
		
		this.updateModified(er);
		
		this.errorEntryDAO.update(er);
	}
	
	public Long addCommentToErrorEntry(Long errorEntryID, Long userID, String comment) {
		ErrorEntry errorEntry = errorEntryDAO.retrieve(errorEntryID);
		User user = userDAO.retrieve(userID);
		Comment c = new Comment(user, new Date(), comment, errorEntry, new ArrayList<Comment>());
		commentDAO.add(c);
		errorEntry.getComments().add(c);
		updateModified(errorEntry);
		errorEntryDAO.update(errorEntry);
		return c.getId();
	}
	
	public void addAnswerToComment(Long commentID, Long userID, String comment) {
		Comment c = commentDAO.retrieve(commentID);
		User user = userDAO.retrieve(userID);
		
		Comment answer = new Comment(user, new Date(), comment, c, new ArrayList<Comment>());
		
		commentDAO.add(answer);	
		c.getAnswers().add(answer);
		commentDAO.update(c);
		updateModified(c.getErrorEntry());
		errorEntryDAO.update(c.getErrorEntry());
	}

	public void removeAnswer(Comment answer, Comment comment) {
		answer = commentDAO.retrieve(answer.getId());
		comment = commentDAO.retrieve(comment.getId());
		
		comment.getAnswers().remove(answer);
		
		commentDAO.delete(answer);
	}

	public void removeComment(Comment comment) {
		comment = commentDAO.retrieve(comment.getId());
		commentDAO.delete(comment);
	}

	public void remove(ErrorEntry errorEntry) {
		errorEntry = errorEntryDAO.retrieve(errorEntry.getId());
		LOG.debug("Will delete: " + errorEntry);
		if(errorEntry.getBadIntervention() != null) {
			badInterventionDAO.delete(errorEntry.getBadIntervention());
		}
		if(errorEntry.getOmission() != null) {
			omissionDAO.delete(errorEntry.getOmission());
		}
		errorEntryDAO.delete(errorEntry);
		appData.decReportedErrors();		
	}
	
	public void setPriority(ErrorEntry errorEntry, Priority priority) {
		errorEntry = errorEntryDAO.retrieve(errorEntry.getId());
		if(priority.equals(errorEntry.getPriority())) {
			return;
		}
		String before = errorEntry.getPriority().name();
		errorEntry.setPriority(priority);
		addHistory(errorEntry, Messages.ERROR_ENTRY_FIELD_PRIORITY, before, priority.name(), true);
		updateModified(errorEntry);
		errorEntryDAO.update(errorEntry);
	}

	public void setState(ErrorEntry errorEntry, State state) {
		errorEntry = errorEntryDAO.retrieve(errorEntry.getId());
		if(state.equals(errorEntry.getState())) {
			return;
		}
		String before = errorEntry.getState().name();
		errorEntry.setState(state);
		addHistory(errorEntry, Messages.ERROR_ENTRY_FIELD_STATE, before, state.name(), true);
		updateModified(errorEntry);
		errorEntryDAO.update(errorEntry);
	}
	
	public void updateModified(ErrorEntry errorEntry) {
		errorEntry.setModified(new Date());
	}
	
	private void addHistory(ErrorEntry errorEntry,
			List<String> fieldList, List<String> beforeList, List<String> afterList, boolean isFormatted) {
		
		List<HistoryEntryField> hefList = new ArrayList<HistoryEntryField>();

		
		HistoryEntry he = new HistoryEntry(this.user, new Date(), hefList, errorEntry);
		
		for(int i = 0; i < fieldList.size(); i++) {
			HistoryEntryField h = new HistoryEntryField(
					he, 
					fieldList.get(i), 
					beforeList.get(i), 
					afterList.get(i),
					isFormatted);
			this.historyEntryFieldDAO.add(h);
			hefList.add(h);
		}
		
		this.historyEntryDAO.add(he);
		
		List<HistoryEntry> heList = errorEntry.getHistoryEntries();
		if(heList == null) {
			heList = new ArrayList<HistoryEntry>();
			errorEntry.setHistoryEntries(heList);
		}
		
		errorEntry.getHistoryEntries().add(he);
		this.errorEntryDAO.update(errorEntry);
		
	}
	
	private void addHistory(ErrorEntry errorEntry,
			String field, String before, String after, boolean isFormatted) {
		
		List<String> fields = new ArrayList<String>(1);
		List<String> befores = new ArrayList<String>(1);
		List<String> afters = new ArrayList<String>(1);
		
		fields.add(field);
		befores.add(before);
		afters.add(after);
		
		addHistory(errorEntry, fields, befores, afters, isFormatted);
	}

}

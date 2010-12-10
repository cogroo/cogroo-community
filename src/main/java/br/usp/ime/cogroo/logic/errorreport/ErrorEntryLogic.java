package br.usp.ime.cogroo.logic.errorreport;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
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
	private ApplicationData appData;

	public ErrorEntryLogic(LoggedUser loggedUser, ErrorEntryDAO errorEntryDAO,
			UserDAO userDAO, CommentDAO commentDAO, CogrooFacade cogrooFacade,
			GrammarCheckerVersionDAO versionDAO, GrammarCheckerOmissionDAO omissionDAO,
			GrammarCheckerBadInterventionDAO badInterventionDAO, ApplicationData appData) {
		this.userDAO = userDAO;
		this.commentDAO = commentDAO;
		this.errorEntryDAO = errorEntryDAO;
		this.user = loggedUser.getUser();
		this.cogrooFacade = cogrooFacade;
		this.versionDAO = versionDAO;
		this.omissionDAO = omissionDAO;
		this.badInterventionDAO = badInterventionDAO;
		this.appData = appData;
	}

	public List<ErrorEntry> getAllReports() {
		return errorEntryDAO.listAll();
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
							null);
					
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
							null);
					
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
							null);
					
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
							null);
					
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
	
	public Long addCommentToErrorEntry(Long errorEntryID, Long userID, String comment) {
		ErrorEntry errorEntry = errorEntryDAO.retrieve(errorEntryID);
		User user = userDAO.retrieve(userID);
		Comment c = new Comment(user, new Date(), comment, errorEntry, new ArrayList<Comment>());
		commentDAO.add(c);
		errorEntry.getComments().add(c);
		errorEntry.setModified(new Date());
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
		c.getErrorEntry().setModified(new Date());
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



}

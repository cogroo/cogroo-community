package br.usp.ime.cogroo.logic.errorreport;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.CommunityException;
import br.usp.ime.cogroo.CommunityExceptionMessages;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerBadInterventionDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerOmissionDAO;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.BadInterventionClassification;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;
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

	public ErrorEntryLogic(LoggedUser loggedUser, ErrorEntryDAO errorEntryDAO,
			UserDAO userDAO, CommentDAO commentDAO, CogrooFacade cogrooFacade,
			GrammarCheckerVersionDAO versionDAO, GrammarCheckerOmissionDAO omissionDAO,
			GrammarCheckerBadInterventionDAO badInterventionDAO) {
		this.userDAO = userDAO;
		this.commentDAO = commentDAO;
		this.errorEntryDAO = errorEntryDAO;
		this.user = loggedUser.getUser();
		this.cogrooFacade = cogrooFacade;
		this.versionDAO = versionDAO;
		this.omissionDAO = omissionDAO;
		this.badInterventionDAO = badInterventionDAO;
	}

	public void addErrorEntry(String userName, String text, String comment,
			String version) {

		// // try to get user, or create it
		// User cogrooUser;
		// if(userDAO.exist(userName)) {
		// cogrooUser = userDAO.retrieve(userName);
		// if(LOG.isDebugEnabled()) {
		// LOG.debug("Could get cogrooUser: " + cogrooUser);
		// }
		//
		// } else {
		// // in the future we raise an error instead
		// cogrooUser = new User(userName);
		// userDAO.add(cogrooUser);
		//
		// if(LOG.isDebugEnabled()) {
		// LOG.debug("Added new cogrooUser: " + cogrooUser);
		// }
		// }
		//
		// ErrorEntry newReport =
		// new ErrorEntry(text, null, version, cogrooUser, new Date(), new
		// Date(), false, false);
		//
		// errorEntryDAO.add(newReport);
		//
		// List<Comment> comments = null;
		// if(comment != null && comment.length() > 0) {
		// Comment c = new Comment(cogrooUser, new Date(), comment, newReport);
		// commentDAO.add(c);
		// comments = new ArrayList<Comment>();
		// comments.add(c);
		// newReport.setComments(comments);
		// errorEntryDAO.update(newReport);
		// }
		//
		// if(LOG.isDebugEnabled()) {
		// LOG.debug("Added new ErrorEntry: " + newReport);
		// }

	}

	public List<ErrorEntry> getAllReports() {
		return errorEntryDAO.listAll();
	}

	public ErrorEntry addComment(String errorEntryID, String newComment) {
		// ErrorEntry report = errorEntryDAO.retrieve(new Long(errorEntryID));
		// Comment c = new Comment(user, new Date(), newComment, report);
		// commentDAO.add(c);
		// if(report.getComments() == null) {
		// report.setComments(new ArrayList<Comment>());
		// }
		// report.getComments().add(c);
		// errorEntryDAO.update(report);
		// return report;
		return null;
	}

	public SortedSet<String> getErrorCategoriesForUser(String userName) {
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
			cogrooUser = userDAO.retrieve(username);
		} else {
			LOG.error("Invalid user in addErrorEntry:" + username);
			throw new CommunityException(
					CommunityExceptionMessages.INVALID_USER,
					new Object[] { username });
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
					list.add(errorEntry);
				}
			}
		}

		return list;
	}
	
	public Long addCommentToErrorEntry(Long errorEntryID, Long userID, String comment) {
		ErrorEntry errorEntry = errorEntryDAO.retrieve(errorEntryID);
		User user = userDAO.retrieve(userID);
		Comment c = new Comment(user, new Date(), comment, errorEntry, new ArrayList<Comment>());
		commentDAO.add(c);
		
		return c.getId();
	}
	
	public void addAnswerToComment(Long commentID, Long userID, String comment) {
		Comment c = commentDAO.retrieve(commentID);
		User user = userDAO.retrieve(userID);
		
		Comment answer = new Comment(user, new Date(), comment, c.getErrorEntry(), new ArrayList<Comment>());
		commentDAO.add(c);	
		
		c.getAnswers().add(c);
		
		commentDAO.update(c);
	}

}

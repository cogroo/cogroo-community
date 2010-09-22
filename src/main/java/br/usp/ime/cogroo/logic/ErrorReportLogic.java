package br.usp.ime.cogroo.logic;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.CommentDAO;
import br.usp.ime.cogroo.dao.ErrorReportDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.Comment;
import br.usp.ime.cogroo.model.ErrorReport;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;

@Component
public class ErrorReportLogic {
	
	private static final Logger LOG = Logger.getLogger(ErrorReportLogic.class);
	private ErrorReportDAO errorReportDAO;
	private UserDAO userDAO;
	private CommentDAO commentDAO;
	private User user;
	private CogrooFacade cogrooFacade;
	
	public ErrorReportLogic(LoggedUser loggedUser, ErrorReportDAO errorReportDAO, UserDAO userDAO, CommentDAO commentDAO, CogrooFacade cogrooFacade) {
		this.userDAO = userDAO;
		this.commentDAO = commentDAO;
		this.errorReportDAO = errorReportDAO;
		this.user = loggedUser.getUser();
		this.cogrooFacade = cogrooFacade;
	}

	public void addErrorEntry(String userName, String text, String comment,
			String version) {
		
		// try to get user, or create it
		User cogrooUser;
		if(userDAO.existe(userName)) {
			cogrooUser = userDAO.retrieve(userName);
			if(LOG.isDebugEnabled()) {
				LOG.debug("Could get cogrooUser: " + cogrooUser);
			}
			
		} else {
			// in the future we raise an error instead
			cogrooUser = new User(userName);
			userDAO.add(cogrooUser);

			if(LOG.isDebugEnabled()) {
				LOG.debug("Added new cogrooUser: " + cogrooUser);
			}
		}
		
		ErrorReport newReport = 
			new ErrorReport(text, null, version, cogrooUser, new Date(), new Date(), false, false);
		
		errorReportDAO.add(newReport);
		
		List<Comment> comments = null;
		if(comment != null && comment.length() > 0) {
			Comment c = new Comment(cogrooUser, new Date(), comment, newReport);
			commentDAO.add(c);
			comments = new ArrayList<Comment>();
			comments.add(c);
			newReport.setComments(comments);
			errorReportDAO.update(newReport);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("Added new ErrorReport: " + newReport);
		}
		
	}

	public List<ErrorReport> getAllReports() {
		return errorReportDAO.listAll();
	}

	public ErrorReport addComment(String errorReportID, String newComment) {
		ErrorReport report = errorReportDAO.retrieve(new Long(errorReportID));
		Comment c = new Comment(user, new Date(), newComment, report);
		commentDAO.add(c);
		if(report.getComments() == null) {
			report.setComments(new ArrayList<Comment>());
		}
		report.getComments().add(c);
		errorReportDAO.update(report);
		return report;
	}
	
	public SortedSet<String> getErrorCategoriesForUser(String userName) {
		SortedSet<String> uniqueRules = new TreeSet<String>();
		
		List<Rule> rules = new RulesContainerHelper(getClass().getResource("/").getPath()).getContainerForXMLAccess().getComponent(RulesProvider.class).getRules().getRule();
		for (Rule rule : rules) {
			uniqueRules.add(rule.getType());
		}
		
		return uniqueRules;
	}

	public String addErrorEntry(String username, String error) {
		String link = null;
		try {
			br.usp.pcs.lta.cogroo.errorreport.model.ErrorReport er =
				cogrooFacade.getErrorReportAccess().getErrorReport(new StringReader(error));
			link = er.getText();
		} catch(Exception e) {
			LOG.error("Counldn't load object from xml", e);
		}
		
		return link;
	}

}

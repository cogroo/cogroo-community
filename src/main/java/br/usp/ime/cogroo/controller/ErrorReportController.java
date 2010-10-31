package br.usp.ime.cogroo.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.exceptions.CommunityException;
import br.usp.ime.cogroo.logic.SecurityUtil;
import br.usp.ime.cogroo.logic.Stats;
import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.util.RestUtil;


@Resource
public class ErrorReportController {

	private static final Logger LOG = Logger
			.getLogger(ErrorReportController.class);

	private LoggedUser loggedUser;
	private ErrorEntryLogic errorEntryLogic;
	private Result result;
	private Validator validator;
	private ErrorEntryDAO errorEntryDAO;
	private SecurityUtil securityUtil;

	private CogrooFacade cogrooFacade;

	private CommentDAO commentDAO; 
	
	//TODO Dependência parece ser necessária. Aqui é o melhor lugar?
	private Stats stats;
	
	public ErrorReportController(
			LoggedUser loggedUser, 
			ErrorEntryLogic errorEntryLogic,
			Result result,
			Validator validator,
			ErrorEntryDAO errorEntryDAO,
			CommentDAO commentDAO,
			SecurityUtil securityUtil,
			CogrooFacade cogrooFacade,
			Stats stats) {
		this.loggedUser = loggedUser;
		this.errorEntryLogic = errorEntryLogic;
		this.result = result;
		this.validator = validator;
		this.errorEntryDAO = errorEntryDAO;
		this.securityUtil = securityUtil;
		this.cogrooFacade = cogrooFacade;
		this.commentDAO = commentDAO;
		this.stats = stats;
	}
	
	@Get
	@Path("/reportNewError")
	public void reportNewError() {
	}
	
	@Post
	@Path("/reportNewError")
	public void reportNewError(
			String text,
			List<String> badint, 
			List<String> comments,
			List<String> badintStart,
			List<String> badintEnd, 
			List<String> badintRule, 
			List<String> omissionClassification,
			List<String> customOmissionText,
			List<String> omissionComment,
			List<String> omissionReplaceBy,
			List<String> omissionStart,
			List<String> omissionEnd) {
		
		System.out.println("text.......:  "+text);
		
		// badint 
		if(badint != null) {
			for (int i = 0; i < badint.size(); i++) {
				System.out.println("badint["+ i +"].......:  "+badint.get(i));
				System.out.println("Commentarios["+ i +"].:  "+comments.get(i));
				System.out.println("badintStart["+ i +"].:  "+badintStart.get(i));
				System.out.println("badintEnd["+ i +"].:  "+badintEnd.get(i));
				System.out.println("badintRule["+ i +"].:  "+badintRule.get(i));
				
			}
		}
		
		if(omissionClassification != null) {
			for (int i = 0; i < omissionClassification.size(); i++) {
				System.out.println("omissionClassification["+ i +"].......:  "+omissionClassification.get(i));
				System.out.println("customOmissionText["+ i +"].:  "+customOmissionText.get(i));
				System.out.println("omissionComment["+ i +"].:  "+omissionComment.get(i));
				System.out.println("omissionReplaceBy["+ i +"].:  "+omissionReplaceBy.get(i));
				System.out.println("omissionStart["+ i +"].:  "+omissionStart.get(i));
				System.out.println("omissionEnd["+ i +"].:  "+omissionEnd.get(i));
			}
		}
		
		errorEntryLogic.addErrorEntry(loggedUser.getUser(), text, badint, comments, badintStart, badintEnd, badintRule, omissionClassification,
				customOmissionText,
				omissionComment, omissionReplaceBy, omissionStart, omissionEnd);
		
		result.redirectTo(getClass()).list();
	}
	
	@Post
	@Path("/reportNewErrorAddText")
	public void reportNewErrorAddText(String text) {
		List<ProcessResult> pr = cogrooFacade.processText(text);
		result.include("text", text).
			include("annotatedText", cogrooFacade.getAnnotatedText(text, pr)).
			include("singleGrammarErrorList", cogrooFacade.asSingleGrammarErrorList(text, pr)).
			include("omissionCategoriesList", this.errorEntryLogic.getErrorCategoriesForUser()).
			redirectTo(getClass()).reportNewError();
		
	}
	
	@Post
	@Path("/submitErrorReport")
	public void submitErrorEntry(String username, String token, String error) {
		
		error = securityUtil.decodeURLSafeString(error);
		
		LOG.debug("Got new error report from: " + username +
				" encrypted token: " + token +
				" error: " + error );
		try {
			errorEntryLogic.addErrorEntry(username, error);
			LOG.debug("Error handled, will set response");
			result.include("result", RestUtil.prepareResponse("result","OK"));
		} catch (CommunityException e) {
			LOG.error("Error handling error submition", e);
		}
	}
	
	@Post
	@Path("/getErrorCategoriesForUser")
	public void getErrorCategoriesForUser(String username, String token) {
		LOG.debug("getErrorCategoriesForUser: " + username +
				" encrypted token: " + token);
		
		try {
			LOG.info("Will get rules for user.");
			Set<String> cat = this.errorEntryLogic.getErrorCategoriesForUser(username);
			StringBuilder sb = new StringBuilder();
			Iterator<String> it = cat.iterator();
			while (it.hasNext()) {
				sb.append(it.next() + "|");
			}
			
			LOG.debug("Categories: " + sb);
			result.include("result", RestUtil.prepareResponse("categories",sb.toString()));
		} catch (Exception e) {
			LOG.error("Error getting categories", e);
		}
	}
	
	@Get
	@Path("/errorEntries")
	public void list() {
		result.include("totalMembers", stats.getTotalMembers())
				.include("onlineMembers", stats.getOnlineMembers())
				.include("reportedErrors", stats.getReportedErrors());
		List<ErrorEntry> reports = errorEntryLogic.getAllReports();
		LOG.debug("Will list of size: "
				+ reports.size());
		result.include("errorEntryList", reports);
	}
	
	@Get
	@Path("/errorEntry")
	public void details(ErrorEntry errorEntry) {
		if(errorEntry == null) {
			result.redirectTo(getClass()).list();
			return;
		}
		
		LOG.debug("Details for: " + errorEntry);
		ErrorEntry errorEntryFromDB =errorEntryDAO.retrieve(new Long(errorEntry.getId())); 
		
		
		result.include("errorEntry", errorEntryFromDB).
			include("processResultList", cogrooFacade.processText(errorEntryFromDB.getText()));
	}
	
	@Post
	@Path("/errorEntryAddComment")
	public void addComment(ErrorEntry errorEntry, String newComment) {
		LOG.debug("errorEntry: " + errorEntry);
		LOG.debug("newComment: " + newComment);
		errorEntryLogic.addCommentToErrorEntry(errorEntry.getId(), loggedUser.getUser().getId(), newComment);
		result.redirectTo(ErrorReportController.class).details(errorEntry);
	}
	
	@Post
	@Path("/errorEntryAddAnswerToComment")
	public void addAnswerToComment(ErrorEntry errorEntry, Comment comment, String answer) {
		errorEntryLogic.addAnswerToComment(comment.getId(), loggedUser.getUser().getId(), answer);
		result.redirectTo(ErrorReportController.class).details(errorEntry);
	}
	
	@Post
	@Path("/errorEntryDeleteAnswer")
	public void remove(Comment answer, Comment comment) {
		errorEntryLogic.removeAnswer(answer, comment);
	}
	
	@Post
	@Path("/errorEntryDeleteComment")
	public void remove(Comment comment) {
		errorEntryLogic.removeComment(comment);
	}
	
	@Post
	@Path("/errorEntryDelete")
	public void remove(ErrorEntry errorEntry) {
		LOG.debug("errorEntry: " + errorEntry);
		errorEntryLogic.remove(errorEntry);
	}
	
}

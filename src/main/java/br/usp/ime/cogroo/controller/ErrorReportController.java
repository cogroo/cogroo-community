package br.usp.ime.cogroo.controller;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.exceptions.CommunityException;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.AnalyticsManager;
import br.usp.ime.cogroo.logic.SecurityUtil;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.errorreport.BadInterventionClassification;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;
import br.usp.ime.cogroo.util.RestUtil;


@Resource
public class ErrorReportController {

	private static final Logger LOG = Logger
			.getLogger(ErrorReportController.class);

	private static final int COMMENT_MAX_SIZE = 700;

	private LoggedUser loggedUser;
	private ErrorEntryLogic errorEntryLogic;
	private Result result;
	private Validator validator;
	private ErrorEntryDAO errorEntryDAO;
	private SecurityUtil securityUtil;

	private CogrooFacade cogrooFacade;
	
	private AnalyticsManager manager;
	private HttpServletRequest request;
	private TextSanitizer sanitizer;
	
	private static final String HEADER_TITLE = "Problemas Reportados";
	private static final String HEADER_DESCRIPTION = "Exibe os problemas reportados através da página e do plug-in CoGrOO para BrOffice.";

	public ErrorReportController(
			LoggedUser loggedUser, 
			ErrorEntryLogic errorEntryLogic,
			Result result,
			Validator validator,
			ErrorEntryDAO errorEntryDAO,
			SecurityUtil securityUtil,
			CogrooFacade cogrooFacade,
			AnalyticsManager manager,
			HttpServletRequest request,
			TextSanitizer sanitizer) {
		this.loggedUser = loggedUser;
		this.errorEntryLogic = errorEntryLogic;
		this.result = result;
		this.validator = validator;
		this.errorEntryDAO = errorEntryDAO;
		this.securityUtil = securityUtil;
		this.cogrooFacade = cogrooFacade;
		this.manager = manager;
		this.request = request;
		this.sanitizer = sanitizer;
	}
	
	@Get
	@Path("/reportNewError")
	public void reportNewError() {
		result.include("text", "Isso são um exemplo de erro gramaticais.");
		result.include("headerTitle", "Reportar problema")
				.include(
						"headerDescription",
						"Reporta um problema no corretor gramatical CoGrOO para a equipe, de modo com que a ferramenta possa ser aprimorada.");
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
		
		if(loggedUser.isLogged()) {
			
			text = sanitizer.sanitize(text, false, true);
			
			comments = sanitizer.sanitize(comments, true);
		
			customOmissionText = sanitizer.sanitize(customOmissionText, false);
		
			omissionComment = sanitizer.sanitize(omissionComment, true);
		
			omissionReplaceBy = sanitizer.sanitize(omissionReplaceBy, false);
			
			errorEntryLogic.addErrorEntry(loggedUser.getUser(), text, badint, comments, badintStart, badintEnd, badintRule, omissionClassification,
					customOmissionText,
					omissionComment, omissionReplaceBy, omissionStart, omissionEnd);
			
			result.include("justReported", true).include("login", loggedUser.getUser().getLogin());
			
			result.redirectTo(getClass()).list();
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, ExceptionMessages.ERROR));
		}
	}
	
	@Post
	@Path("/updateErrorReport")
	public void updateErrorReport(
			Long reportId,
			String type,
			String badintIndex, 
			String badintType,
			List<String> badintStart,
			List<String> badintEnd, 
			List<String> badintRule, 
			String omissionCategory,
			String omissionCustom,
			String omissionReplaceBy,
			String omissionStart,
			String omissionEnd) {
		
		if(loggedUser.isLogged()) {
			
			LOG.debug("reportId: " + reportId + "\n" +
				"type: " + type + "\n" +
				"badintIndex: " + badintIndex + "\n" +
				"badintType: " + badintType + "\n" +
				"badintStart: " + badintStart+ "\n" +
				"badintEnd: " + badintEnd + "\n" +
				"badintRule: " + badintRule + "\n" +
				"omissionCategory: " + omissionCategory + "\n" +
				"omissionCustom: " + omissionCustom + "\n" +
				"omissionReplaceBy: " + omissionReplaceBy + "\n" +
				"omissionStart: " + omissionStart + "\n" +
				"omissionEnd: " + omissionEnd );
			
			ErrorEntry errorEntryFromDB = this.errorEntryDAO.retrieve(reportId);
			ErrorEntry originalErrorEntry = null;
			try {
				originalErrorEntry = (ErrorEntry) errorEntryFromDB.clone();
				
			} catch (CloneNotSupportedException e) {
				LOG.error("Error cloning ErrorEntry object: ", e);
			}
			
			if(type.equals("BADINT")) {
				GrammarCheckerBadIntervention newBadIntervention = null;
				if(errorEntryFromDB.getBadIntervention() == null) {
					newBadIntervention = new GrammarCheckerBadIntervention();
					errorEntryFromDB.setBadIntervention(newBadIntervention);
				} else {
					newBadIntervention = errorEntryFromDB.getBadIntervention();
				}
				
				newBadIntervention.setClassification(Enum.valueOf(BadInterventionClassification.class, badintType));
				newBadIntervention.setErrorEntry(errorEntryFromDB);
				newBadIntervention.setRule(Integer.valueOf(badintEnd.get(Integer.valueOf(badintIndex) - 1)));
				
				errorEntryFromDB.setSpanStart(Integer.valueOf(badintStart.get(Integer.valueOf(badintIndex) - 1)));
				errorEntryFromDB.setSpanEnd(Integer.valueOf(badintEnd.get(Integer.valueOf(badintIndex) - 1)));
				
				this.errorEntryLogic.updateBadIntervention(errorEntryFromDB, originalErrorEntry);
			} else {
				int start = -1;
				int end = -1;
				
				if(omissionStart != null && omissionEnd != null) {
					start = Integer.parseInt(omissionStart);
					end = Integer.parseInt(omissionEnd);
				}
				
				GrammarCheckerOmission o = null;
				if(errorEntryFromDB.getOmission() == null) {
					o = new GrammarCheckerOmission();
					errorEntryFromDB.setOmission(o);
				} else {
					o = errorEntryFromDB.getOmission();
				}
				
				o.setCategory(omissionCategory);
				if(omissionCategory.equals(ErrorEntryLogic.CUSTOM)) {
					o.setCustomCategory(sanitizer.sanitize(omissionCustom, false));
				} else {
					o.setCustomCategory(null);
				}
				o.setErrorEntry(errorEntryFromDB);
				o.setReplaceBy(sanitizer.sanitize(omissionReplaceBy,false));
				
				errorEntryFromDB.setSpanStart(start);
				errorEntryFromDB.setSpanEnd(end);
				
				if( !(end > 0 && end > start)) {
					validator.add(new ValidationMessage(ExceptionMessages.ERROR_REPORT_OMISSION_INVALID_SELECTION,
							ExceptionMessages.ERROR));
				}
				if( omissionCategory.equals("custom") && (omissionCustom == null || omissionCustom.length() == 0)) {
					validator.add(new ValidationMessage(ExceptionMessages.ERROR_REPORT_OMISSION_MISSING_CUSTOM_CATEGORY,
							ExceptionMessages.ERROR));
				}
				if( (omissionReplaceBy == null || omissionReplaceBy.length() == 0)) {
					validator.add(new ValidationMessage(ExceptionMessages.ERROR_REPORT_OMISSION_MISSING_REPLACE,
							ExceptionMessages.ERROR));
				}
				if(validator.hasErrors()) {
					validator.onErrorUse(Results.logic()).redirectTo(ErrorReportController.class)
						.editDetails(errorEntryFromDB);
					return;
				}
				this.errorEntryLogic.updateOmission(errorEntryFromDB, originalErrorEntry);
			}
			
			result.redirectTo(getClass()).details(errorEntryFromDB);
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, ExceptionMessages.ERROR));
		}
	}
	
	@Post
	@Path("/reportNewErrorAddText")
	public void reportNewErrorAddText(String text) {
		text = sanitizer.sanitize(text, false, true);
		try {
			if(text != null && text.length() >= 0) {
				if( text.length() > 255 ) {
					text = text.substring(0,255);
				}
				
				List<ProcessResult> pr = cogrooFacade.processText(text);
				result.include("analyzed", true).
					include("cleanText", text).
					include("annotatedText", cogrooFacade.getAnnotatedText(text, pr)).
					include("singleGrammarErrorList", cogrooFacade.asSingleGrammarErrorList(text, pr)).
					include("omissionCategoriesList", this.errorEntryLogic.getErrorCategoriesForUser()).
					include("processResultList", pr).
					redirectTo(getClass()).reportNewError();
			} else {
				result.redirectTo(getClass()).reportNewError();
			}
		} catch (Exception e) {
			LOG.error("Error processing text: " + text);
		}
	}
	
	@Get
	@Path("/submitErrorReport")
	public void submitAnalytics() {
		try {		
			result.include("googleAnalyticsImageUrl",
					manager.googleAnalyticsGetImageUrl(request));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Post
	@Path("/submitErrorReport")
	public void submitErrorEntry(String username, String token, String error) {
		error = securityUtil.decodeURLSafeString(error);
		// TODO Sanitize input in the plugin. Should check also for XML markup.
		//error = sanitizer.sanitize(error, false);
		
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
		List<ErrorEntry> reports = errorEntryLogic.getAllReports();
		LOG.debug("Will list of size: "
				+ reports.size());
		result.include("errorEntryList", reports);
		if(!loggedUser.isLogged()) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, -7);
			result.include("oneWeekAgo", now.getTime());
		}
		result.include("headerTitle", HEADER_TITLE).include("headerDescription",
				HEADER_DESCRIPTION);
	}
	
	@Get
	@Path("/errorEntry/{errorEntry.id}")
	public void details(ErrorEntry errorEntry) {
		if(errorEntry == null) {
			result.redirectTo(getClass()).list();
			return;
		}
		
		ErrorEntry errorEntryFromDB =errorEntryDAO.retrieve(new Long(errorEntry.getId())); 
		LOG.debug("Details for: " + errorEntryFromDB);
		
		if (errorEntryFromDB == null) {
			validator.add(new ValidationMessage(ExceptionMessages.PAGE_NOT_FOUND,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(ErrorReportController.class)
					.list();
		}
		
		result.include("errorEntry", errorEntryFromDB).
			include("processResultList", cogrooFacade.processText(errorEntryFromDB.getText())).
			include("priorities", Priority.values()).
			include("states", State.values());
		
		String title = "Problema #" + errorEntryFromDB.getId() + ": "
				+ errorEntryFromDB.getText();
		String description = "Tipo: " + (errorEntryFromDB.getOmission() == null ? "Intervenção indevida; Erro: " + errorEntryFromDB
				.getBadIntervention().getClassification()
				: "Omissão; Categoria"
						+ (errorEntryFromDB.getOmission().getCategory() == null ? " (personalizada): " + errorEntryFromDB
						.getOmission().getCustomCategory()
						: ": " + errorEntryFromDB.getOmission().getCategory()));
		result.include("headerTitle", title).include("headerDescription",
				description);
	}
	
	@Get
	@Path("/editErrorEntry/{errorEntry.id}")
	public void editDetails(ErrorEntry errorEntry) {
		if(errorEntry == null) {
			result.redirectTo(getClass()).list();
			return;
		}
		
		ErrorEntry errorEntryFromDB =errorEntryDAO.retrieve(new Long(errorEntry.getId())); 
		LOG.debug("Details for: " + errorEntryFromDB);
		
		if (errorEntryFromDB == null) {
			validator.add(new ValidationMessage(ExceptionMessages.PAGE_NOT_FOUND,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(ErrorReportController.class)
					.list();
		} else if(!loggedUser.isLogged() || !loggedUser.getUser().getRole().getCanEditErrorReport()) {
			validator.add(new ValidationMessage(ExceptionMessages.USER_UNAUTHORIZED,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(ErrorReportController.class)
					.details(errorEntryFromDB);
		} else {
			List<ProcessResult> procRes = cogrooFacade.processText(errorEntryFromDB.getText());
			
			boolean hasError = false;
			for (ProcessResult processResult : procRes) {
				if(processResult.getMistakes().size() > 0) {
					hasError = true;
					break;
				}
			}
			
			result.include("errorEntry", errorEntryFromDB).
				include("hasError", hasError).
				include("processResultList", procRes).
				include("singleGrammarErrorList", cogrooFacade.asSingleGrammarErrorList(errorEntryFromDB.getText(), procRes)).
				include("omissionCategoriesList", this.errorEntryLogic.getErrorCategoriesForUser());
		}
	}
	
	@Post
	@Path("/errorEntryAddComment")
	public void addComment(ErrorEntry errorEntry, String newComment) {
		newComment = sanitizer.sanitize(newComment, true);
		ErrorEntry errorEntryFromDB = errorEntryDAO.retrieve(new Long(errorEntry.getId()));
		
		LOG.debug("errorEntry: " + errorEntryFromDB);
		LOG.debug("newComment: " + newComment);
		if (newComment.trim().isEmpty()) {
			validator.add(new ValidationMessage(ExceptionMessages.COMMENT_SHOULD_NOT_BE_EMPTY,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(ErrorReportController.class)
					.details(errorEntryFromDB);
		} else if (newComment.trim().length() > COMMENT_MAX_SIZE) {
			validator.add(new ValidationMessage(ExceptionMessages.COMMENT_SHOULD_NOT_EXCEED_CHAR,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(ErrorReportController.class)
					.details(errorEntryFromDB);
		} else {
			errorEntryLogic.addCommentToErrorEntry(errorEntryFromDB.getId(), loggedUser.getUser().getId(), newComment);
			
		}
		result.redirectTo(ErrorReportController.class).details(errorEntryFromDB);
	}
	
	@Post
	@Path("/errorEntryAddAnswerToComment")
	public void addAnswerToComment(ErrorEntry errorEntry, Comment comment, String answer) {
		answer = sanitizer.sanitize(answer, true);
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
	
	@Post
	@Path("/errorEntrySetPriority")
	public void errorEntrySetPriority(ErrorEntry errorEntry, String priority) {
		errorEntryLogic.setPriority(errorEntry, Enum.valueOf(Priority.class, priority));
		result.redirectTo(ErrorReportController.class).details(errorEntry);
	}
	
	
	@Post
	@Path("/errorEntrySetState")
	public void errorEntrySetState(ErrorEntry errorEntry, String state) {
		errorEntryLogic.setState(errorEntry, Enum.valueOf(State.class, state));
		result.redirectTo(ErrorReportController.class).details(errorEntry);
	}
}

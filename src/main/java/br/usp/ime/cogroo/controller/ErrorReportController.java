package br.usp.ime.cogroo.controller;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.exceptions.CommunityException;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.AnalyticsManager;
import br.usp.ime.cogroo.logic.SecurityUtil;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.BadInterventionClassification;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;
import br.usp.ime.cogroo.security.annotations.LoggedIn;
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
	private CommentDAO commentDAO;
	private SecurityUtil securityUtil;

	private CogrooFacade cogrooFacade;
	
	private AnalyticsManager manager;
	private HttpServletRequest request;
	private TextSanitizer sanitizer;

	private static final String LAST_TEXT = "LAST_NOTLOGGED_TEXT";
	
	private static final ResourceBundle messages =
	      ResourceBundle.getBundle("messages", new Locale("pt_BR"));

	public ErrorReportController(
			LoggedUser loggedUser, 
			ErrorEntryLogic errorEntryLogic,
			Result result,
			Validator validator,
			ErrorEntryDAO errorEntryDAO,
			CommentDAO commentDAO,
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
		this.commentDAO = commentDAO;
		this.securityUtil = securityUtil;
		this.cogrooFacade = cogrooFacade;
		this.manager = manager;
		this.request = request;
		this.sanitizer = sanitizer;
	}
	
	@Deprecated
	@Get
	@Path(value = "/errorEntries")
	public void deprecatedList() {
		result.use(Results.status()).movedPermanentlyTo(ErrorReportController.class).list();
	}
	
	@Get
    @Path("/reportStatusRefresh")
    @LoggedIn
    public void reportStatus() {
      User user = loggedUser.getUser();
      
      if (user != null) {
        if (user.getRole().getCanRefreshStatus()) {
          
          LOG.warn("Report Status");
          
          errorEntryLogic.refreshReports();
        }
        else {
          LOG.warn("Não tem credenciais");
        }
      }
      
      result.redirectTo(this).list();
    }
	
	@Get
	@Path("/reports/text")
	public void createCorpus() {
		result.use(Results.http()).body(errorEntryLogic.getCorpus());
	}
	
	@Get
	@Path("/reports")
	public void list() {
		List<ErrorEntry> reports = errorEntryLogic.getAllReports();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will list of size: "
					+ reports.size());
		}

		result.include("errorEntryList", reports);
		if(!loggedUser.isLogged()) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, -7);
			result.include("oneWeekAgo", now.getTime());
		}
		result.include("headerTitle",
				messages.getString("LIST_ERROR_REPORT_HEADER"))
				.include("headerDescription",
				messages.getString("LIST_ERROR_REPORT_DESCRIPTION"))
				.include("stats", errorEntryLogic.getStats());
	}
	
	@Deprecated
	@Get
	@Path(value = "/reportNewError")
	public void deprecatedAddReport() {
		result.use(Results.status()).movedPermanentlyTo(ErrorReportController.class).addReport();
	}
	
	@Get
	@Path("/reports/new/{text*}")
	public void addReportGET(String text) {
		if (text != null) {
			addReport(text);
			return;
		}
	}
	
	@Get
	@Path("/reports/new")
	public void addReport() {
		String text = (String)request.getSession().getAttribute(LAST_TEXT);
		if(text != null && loggedUser.isLogged()) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Got text saved in session: " + text);
			}
			request.getSession().removeAttribute(LAST_TEXT);
			addReport(text); // execute as a post
			return;

		}
		result.include("text", "Isto são um exemplo de erro gramaticais.");
		result.include("headerTitle",
				messages.getString("NEW_ERROR_REPORT_HEADER")).include(
				"headerDescription",
				messages.getString("NEW_ERROR_REPORT_DESCRIPTION"));
	}
	
	@Post
	@Path("/reports/newtext")
	public void addReport(String text) {
		text = sanitizer.sanitize(text, false, true);
		try {
			if(text != null && text.length() >= 0) {
				if( text.length() > 255 ) {
					text = text.substring(0,255);
				}
				
				if(!loggedUser.isLogged()) {
					// if not logged we save the text.
					if(LOG.isDebugEnabled()) {
						LOG.debug("Saving error report text before login: " + text);
					}
					request.getSession().setAttribute(LAST_TEXT, text);
				}
				
				if(LOG.isDebugEnabled()) {
					LOG.debug("Error text: " + text);
				}
				List<ProcessResult> pr = cogrooFacade.processText(text);
				if(LOG.isDebugEnabled()) {
					LOG.debug("Text processed. Results:");
					for (ProcessResult processResult : pr) {
						LOG.debug("... " + processResult.getTextAnnotatedWithErrors());
					}
				}
				result.include("analyzed", true).
					include("cleanText", text).
					include("annotatedText", cogrooFacade.getAnnotatedText(text, pr)).
					include("singleGrammarErrorList", cogrooFacade.asSingleGrammarErrorList(text, pr)).
					include("omissionCategoriesList", this.errorEntryLogic.getErrorCategoriesForUser()).
					include("processResultList", pr).
					redirectTo(getClass()).addReport();
			} else {
				result.redirectTo(getClass()).addReport();
			}
		} catch (Exception e) {
			LOG.error("Error processing text: " + text, e);
		}
	}
	
	@Post
	@Path("/reports")
	@LoggedIn
	public void addReport(
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
		
			if(LOG.isDebugEnabled()) {
				LOG.debug("Adding new report.");
			}
		text = sanitizer.sanitize(text, false, true);

		comments = sanitizer.sanitize(comments, true);

		customOmissionText = sanitizer.sanitize(customOmissionText, false);

		omissionComment = sanitizer.sanitize(omissionComment, true);

		omissionReplaceBy = sanitizer.sanitize(omissionReplaceBy, false);

		errorEntryLogic.addErrorEntry(loggedUser.getUser(), text, badint,
				comments, badintStart, badintEnd, badintRule,
				omissionClassification, customOmissionText, omissionComment,
				omissionReplaceBy, omissionStart, omissionEnd);

		result.include("okMessage", "Problema reportado com sucesso!");		
		result.include("gaEventErrorReported", true);

		if(LOG.isDebugEnabled()) {
			LOG.debug("New report added.");
		}
		result.redirectTo(getClass()).list();
	}
	
	@Deprecated
	@Get
	@Path("/errorEntry")
	public void veryDeprecatedDetails(ErrorEntry errorEntry) {
		result.use(Results.status()).movedPermanentlyTo(ErrorReportController.class).details(errorEntry);
	}
	
	@Deprecated
	@Get
	@Path(value = "/errorEntry/{errorEntry.id}")
	public void deprecatedDetails(ErrorEntry errorEntry) {
		result.use(Results.status()).movedPermanentlyTo(ErrorReportController.class).details(errorEntry);
	}
	
	@Get
	@Path("/reports/{errorEntry.id}")
	public void details(ErrorEntry errorEntry) {
		if(errorEntry == null) {
			result.redirectTo(getClass()).list();
			return;
		}
		
		ErrorEntry errorEntryFromDB =errorEntryDAO.retrieve(new Long(errorEntry.getId())); 
		LOG.debug("Details for: " + errorEntryFromDB);
		
		if (errorEntryFromDB == null) {
			result.notFound();
			return;
		}
		
		result.include("errorEntry", errorEntryFromDB).
			include("processResultList", cogrooFacade.cachedProcessText(errorEntryFromDB.getText())).
			include("priorities", Priority.values()).
			include("states", State.values());
		
		String title = "Problema Nº. " + errorEntryFromDB.getId() + ": "
				+ errorEntryFromDB.getText();

		String sender = "Enviado por: "
				+ errorEntryFromDB.getSubmitter().getName();
		String version = "Versão: " + errorEntryFromDB.getVersion().getVersion();
		String creationDate = "Criado em: " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(errorEntryFromDB.getCreation());
		String changeDate = "Modificado em: " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(errorEntryFromDB.getModified());
		String type = "Tipo: ";
		String details = null;
		if (errorEntryFromDB.getOmission() == null) {
			type += "Intervenção indevida";
			String rule = "Regra: "
					+ errorEntryFromDB.getBadIntervention().getRule();
			String classification = "Erro: "
					+ messages.getString(errorEntryFromDB.getBadIntervention()
							.getClassification().toString());
			details = rule + "; " + classification;
		} else {
			type += "Omissão";
			String category = "Categoria"
					+ (errorEntryFromDB.getOmission().getCategory() == null ? " (personalizada): "
							+ errorEntryFromDB.getOmission()
									.getCustomCategory()
							: ": "
									+ errorEntryFromDB.getOmission()
											.getCategory());
			String replaceBy = "Substituir por: "
					+ errorEntryFromDB.getOmission().getReplaceBy();
			details = category + "; " + replaceBy;
		}
		String description = sender + "; " + version + "; " + type + "; "
				+ details + "; " + creationDate + "; " + changeDate;

		result.include("headerTitle", StringEscapeUtils.escapeHtml(title))
				.include("headerDescription",
						StringEscapeUtils.escapeHtml(description));
	}
	
	@Get
	@Path("/reports/{errorEntry.id}/edit")
	@LoggedIn
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
	
	@Put
	@Path("/reports/{reportId}")
	@LoggedIn
	public void updateErrorReport(
			Long reportId,
			String type,
			String changedText,  
			String badintIndex, 
			String badintType,
			String badIntMan,
			List<String> badintStart,
			List<String> badintEnd, 
			List<String> badintRule, 
			String omissionCategory,
			String omissionCustom,
			String omissionReplaceBy,
			String omissionStart,
			String omissionEnd) {
		
			
		LOG.debug("reportId: " + reportId + "\n" +
			"type: " + type + "\n" +
		    "changedText: " + changedText + "\n" +
			"badintIndex: " + badintIndex + "\n" +
			"badintType: " + badintType + "\n" +
			"badintStart: " + badintStart+ "\n" +
			"badintEnd: " + badintEnd + "\n" +
			"badintRule: " + badintRule + "\n" +
			"badIntMan: " + badIntMan + "\n" +
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
		
		if(changedText != null && !changedText.equals(errorEntryFromDB.getText())) {
		  errorEntryFromDB.setText(changedText);
		}
		
		if(type == null) {
			if(errorEntryFromDB.getBadIntervention() != null) {
				type = "BADINT";
			} else {
				type = "OMISSION";
			}
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
			
			if( !badIntMan.isEmpty()) {
	            int start = -1;
	            int end = -1;
	            
	            if(omissionStart != null && omissionEnd != null) {
	                start = Integer.parseInt(omissionStart);
	                end = Integer.parseInt(omissionEnd);
	            }
			  
			  newBadIntervention.setRule(badIntMan);
			  errorEntryFromDB.setSpanStart(start);
	          errorEntryFromDB.setSpanEnd(end);
	            
			} else {
			  newBadIntervention.setRule(badintRule.get(Integer.valueOf(badintIndex) - 1));
			  errorEntryFromDB.setSpanStart(Integer.valueOf(badintStart.get(Integer.valueOf(badintIndex) - 1)));
	          errorEntryFromDB.setSpanEnd(Integer.valueOf(badintEnd.get(Integer.valueOf(badintIndex) - 1)));
			}
			
			
			
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
		
		result.include("gaEventErrorChanged", true);
		result.redirectTo(getClass()).details(errorEntryFromDB);

	}

	@Delete
	@Path("/reports/{errorEntry.id}")
	@LoggedIn
	public void remove(ErrorEntry errorEntry) {
		errorEntry = errorEntryDAO.retrieve(errorEntry.getId());
		if(loggedUser.getUser().equals(errorEntry.getSubmitter())
				|| loggedUser.getUser().getRole().getCanDeleteOtherUserErrorReport()) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("errorEntry: " + errorEntry);
			}
			errorEntryLogic.remove(errorEntry);
		} else {
			LOG.info("Unauthorized user tried to delete errorEntry: " + loggedUser.getUser() + " : " + errorEntry);
		}
		
	}
	
	/**
	 * Used by the plugin.
	 */
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
	
	/**
	 * Submit an error report via OpenOffice plugin. Only CoGrOO users are allowed.
	 * @param username
	 * @param token
	 * @param error
	 */
	@Post
	@Path("/submitErrorReport")
	public void submitErrorEntry(String username, String token, String error) {
		error = securityUtil.decodeURLSafeString(error);
		
		LOG.debug("Got new error report from: " + username +
				" encrypted token: " + token +
				" error: " + error );
		try {
			errorEntryLogic.addErrorEntry("cogroo", username, error);
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
	
	@Post
	@Path("/reports/{errorEntry.id}/comments")
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
		
		result.include("gaEventCommentAdded", true);
		
		result.redirectTo(ErrorReportController.class).details(errorEntryFromDB);
	}
	
	@Delete
	@Path("/reports/{errorEntry.id}/comments/{comment.id}")
	@LoggedIn
	public void remove(Comment comment) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will delete cooment." + commentDAO);
		}
		comment = commentDAO.retrieve(comment.getId());
		if( (loggedUser.getUser().getRole().getCanDeleteOwnCommment() &&
				loggedUser.getUser().equals(comment.getUser())) ||
				loggedUser.getUser().getRole().getCanDeleteOtherUserCommment()) {
			errorEntryLogic.removeComment(comment);
		} else {
			LOG.warn("Invalid user tried to delete comment " + loggedUser.getUser() + " : " + comment);
		}
		
	} 
	
	@Post
	@Path("/reports/{errorEntry.id}/comments/{comment.id}/answers")
	@LoggedIn
	public void addAnswerToComment(ErrorEntry errorEntry, Comment comment, String answer) {
		answer = sanitizer.sanitize(answer, true);
		errorEntryLogic.addAnswerToComment(comment.getId(), loggedUser.getUser().getId(), answer);
		result.include("gaEventCommentAdded", true);
		result.redirectTo(ErrorReportController.class).details(errorEntry);
	}
	
	@Delete
	@Path("/reports/{errorEntry.id}/comments/{comment.id}/answers/{answer.id}")
	@LoggedIn
	public void remove(Comment answer, Comment comment) {
		comment = commentDAO.retrieve(comment.getId());
		answer = commentDAO.retrieve(answer.getId());
		if( (loggedUser.getUser().getRole().getCanDeleteOwnCommment() &&
				loggedUser.getUser().equals(answer.getUser())) ||
				loggedUser.getUser().getRole().getCanDeleteOtherUserCommment()) {
			errorEntryLogic.removeAnswer(answer, comment);
		} else {
			LOG.warn("Invalid user tried to delete answer " + loggedUser.getUser() + " : " + comment);
		}
	}
	
	@Put
	@Path("/reports/{errorEntry.id}/priority")
	@LoggedIn
	public void errorEntrySetPriority(ErrorEntry errorEntry, String priority) {
		if(loggedUser.getUser().getRole().getCanSetErrorReportPriority()) {
			errorEntryLogic.setPriority(errorEntry, Enum.valueOf(Priority.class, priority));
			result.include("gaEventPriorityChanged", true);
			result.redirectTo(ErrorReportController.class).details(errorEntry);
		} else {
			LOG.info("Invalid user tried to set priority: " + loggedUser.getUser());
		}
	}
	
	
	@Put
	@Path("/reports/{errorEntry.id}/state")
	@LoggedIn
	public void errorEntrySetState(ErrorEntry errorEntry, String state) {
		if(loggedUser.getUser().getRole().getCanSetErrorReportState()) {
			errorEntryLogic.setState(errorEntry, Enum.valueOf(State.class, state));
			result.include("gaEventStateChanged", true);
			result.redirectTo(ErrorReportController.class).details(errorEntry);
		} else {
			LOG.info("Invalid user tried to set priority: " + loggedUser.getUser());
		}
	}
	
	@Get
	@Path("/reports/edit")
	public void multipleEdit() {
		List<ErrorEntry> reports = errorEntryLogic.getAllReports();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will list of size: "
					+ reports.size());
		}
		
		result.include("errorEntryList", reports).
			include("priorities", Priority.values()).
			include("states", State.values());
		if(!loggedUser.isLogged()) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, -7);
			result.include("oneWeekAgo", now.getTime());
		}
		result.include("headerTitle",
				messages.getString("LIST_ERROR_REPORT_HEADER")).include(
				"headerDescription",
				messages.getString("LIST_ERROR_REPORT_DESCRIPTION"));
	}
	
	@Put
	@Path("/reports/edit")
	@LoggedIn
	public void multipleEdit(Boolean applycomment, Boolean applypriority,
			Boolean applystate, Integer errorlist_lenght, Long[] errorEntryID,
			String newComment, String priority, String state) {
		if (loggedUser.getUser().getRole().getCanEditErrorReport()) {

			String comment = null;
			if (applycomment != null && applycomment) {
				comment = sanitizer.sanitize(newComment, true);;
			}
			Priority priorityEnum = null;
			if (applypriority != null && applypriority) {
				priorityEnum = Enum.valueOf(Priority.class, priority);
			}
			State stateEnum = null;
			if (applystate != null && applystate) {
				stateEnum = Enum.valueOf(State.class, state);
			}
			List<ErrorEntry> entries = new ArrayList<ErrorEntry>();
			if (errorEntryID != null && errorEntryID.length > 0) {
				LOG.info("Selected errors: " + Arrays.toString(errorEntryID));

				for (int i = 0; i < errorEntryID.length; i++) {
					if (errorEntryID[i] != null) {
						LOG.info("Will get error " + i);
						ErrorEntry error = this.errorEntryDAO
								.retrieve(errorEntryID[i]);
						LOG.info("Got " + error);
						entries.add(error);
					}
				}
			}
			errorEntryLogic.multipleEdit(entries, priorityEnum, stateEnum,
					comment);
		} else {
			LOG.info("Invalid user tried to set priority: "
					+ loggedUser.getUser());
		}

		result.redirectTo(getClass()).list();
	}
}

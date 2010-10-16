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
import br.usp.ime.cogroo.CommunityException;
import br.usp.ime.cogroo.Util.RestUtil;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.logic.SecurityUtil;
import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;


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
	
	public ErrorReportController(
			LoggedUser loggedUser, 
			ErrorEntryLogic errorEntryLogic,
			Result result,
			Validator validator,
			ErrorEntryDAO errorEntryDAO,
			SecurityUtil securityUtil,
			CogrooFacade cogrooFacade) {
		this.loggedUser = loggedUser;
		this.errorEntryLogic = errorEntryLogic;
		this.result = result;
		this.validator = validator;
		this.errorEntryDAO = errorEntryDAO;
		this.securityUtil = securityUtil;
		this.cogrooFacade = cogrooFacade;
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
			LOG.debug("Error handled, will send response");
			result.include("result", RestUtil.prepareResponse("result","OK"));
		} catch (CommunityException e) {
			LOG.error("Error handling error submition", e);
			//result.include("result", RestUtil.prepareResponse("result","ERROR"));
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
	}
	
	@Get
	@Path("/errorEntry")
	public void details(ErrorEntry errorEntry) {
		LOG.debug("Details for: " + errorEntry);
		ErrorEntry errorEntryFromDB =errorEntryDAO.retrieve(new Long(errorEntry.getId())); 
		
		
		result.include("errorEntry", errorEntryFromDB).
			include("processResultList", cogrooFacade.processText(errorEntryFromDB.getText()));
	}
	
	@Post
	@Path("/errorEntryAddComment")
	public void details(String errorEntryID, String newComment) {
		LOG.debug("Details for: " + errorEntryID);
		errorEntryLogic.addComment(errorEntryID, newComment);
		result.include("errorEntry", errorEntryDAO.retrieve(new Long(errorEntryID)));
	}
	
}

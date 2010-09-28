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
	
	public ErrorReportController(
			LoggedUser loggedUser, 
			ErrorEntryLogic errorEntryLogic,
			Result result,
			Validator validator,
			ErrorEntryDAO errorEntryDAO,
			SecurityUtil securityUtil) {
		this.loggedUser = loggedUser;
		this.errorEntryLogic = errorEntryLogic;
		this.result = result;
		this.validator = validator;
		this.errorEntryDAO = errorEntryDAO;
		this.securityUtil = securityUtil;
	}
	
	/**
	 * Gets error report from Cogroo Add-On
	 * @param userName an user name
	 * @param text a sample text
	 * @param comment comments about the issue
	 * @param version the Cogroo Add-on version
	 */
	@Post
	@Path("/cogrooErrorEntry")
	public void addErrorEntry(String userName, String text, String comment, String version) {
		LOG.debug("Got new error report from: " + userName +
				" text: " + text +
				" comment: " + comment +
				" version: " + version);
		errorEntryLogic.addErrorEntry(userName, text, comment, version);
		
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
		} catch (CommunityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.include("result", RestUtil.prepareResponse("result","OK"));
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
	
	@Post
	@Path("/errorEntry")
	public void details(String errorEntryID) {
		LOG.debug("Details for: " + errorEntryID);
		result.include("errorEntry", errorEntryDAO.retrieve(new Long(errorEntryID)));
	}
	
	@Post
	@Path("/errorEntryAddComment")
	public void details(String errorEntryID, String newComment) {
		LOG.debug("Details for: " + errorEntryID);
		errorEntryLogic.addComment(errorEntryID, newComment);
		result.include("errorEntry", errorEntryDAO.retrieve(new Long(errorEntryID)));
	}
	
}

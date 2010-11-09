package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.ApplicationData;

/**
 * @author Michel
 */
@Resource
public class StatsController {
	
	private static final long A_MONTH = 30l * 24 * 60 * 60 * 1000;
	private static final int N = 10;

	private final Result result;
	private final ApplicationData appData;
	
	private final UserDAO userDAO;

	public StatsController(Result result, ApplicationData appData, UserDAO userDAO) {
		this.result = result;
		this.appData = appData;
		this.userDAO = userDAO;
	}

	@Get
	@Path("/stats")
	public void stats() {
		long lastMonth = System.currentTimeMillis() - A_MONTH;
		appData.setIdleUsers(userDAO.retrieveIdleUsers(lastMonth, N));
		appData.setTopUsers(userDAO.retrieveTopUsers(lastMonth, N));
		
		result.include("appData", appData);
	}
}

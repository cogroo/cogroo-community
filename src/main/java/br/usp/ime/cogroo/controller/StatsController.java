package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.logic.Stats;

/**
 * @author Michel
 */
@Resource
public class StatsController {

	private final Result result;

	private final Stats stats;

	public StatsController(Result result, Stats stats) {
		this.result = result;
		this.stats = stats;
	}

	@Get
	@Path("/stats")
	public void list() {
		result.include("totalMembers", stats.getTotalMembers())
				.include("onlineMembers", stats.getOnlineMembers())
				.include("reportedErrors", stats.getReportedErrors());
	}
}

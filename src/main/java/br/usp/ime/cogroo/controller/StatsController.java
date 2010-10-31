package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Michel
 */
@Resource
public class StatsController {

	private final Result result;

	public StatsController(Result result) {
		this.result = result;
	}

	@Get
	@Path("/stats")
	public void stats() {
	}
}

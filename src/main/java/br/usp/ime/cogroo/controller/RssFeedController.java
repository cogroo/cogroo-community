package br.usp.ime.cogroo.controller;

import java.io.File;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.usp.ime.cogroo.logic.RssFeed;

@Resource
public class RssFeedController {
    
	@Get
	@Path("/rss.xml")
	public File rss() {
        return new File(RssFeed.FILENAME);
    }
}

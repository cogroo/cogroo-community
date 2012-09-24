package br.usp.ime.cogroo.logic;

import javax.servlet.ServletContext;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class StringTemplateUtil {
	
	private static final Logger LOG = Logger.getLogger(StringTemplateUtil.class);
	
	public static final String ERROR_DETAILS = "errorDetails";
	public static final String ERROR_CHANGED = "errorChanged";
	public static final String ERROR_CHANGED_TWEET = "errorChangedTweet";
	public static final String FOOTER = "footer";
	public static final String NEW_COMMENT = "newComment";
	public static final String NEW_COMMENT_TWEET = "newCommentTweet";
	public static final String ERROR_NEW = "errorNew";
	public static final String ERROR_NEW_TWEET = "errorNewTweet";
	
	private StringTemplateGroup group;
	private String templatesPath;
	
	public StringTemplateUtil(ServletContext context) {
        this.templatesPath = context.getRealPath("/stringtemplates");
		this.group =  new StringTemplateGroup("myGroup", 
				this.templatesPath, DefaultTemplateLexer.class);
		this.group.setFileCharEncoding("UTF-8");
	}
	
	public StringTemplate getTemplate(String name) {
		StringTemplate st = null;
		try {
			st = group.getInstanceOf(name);
		} catch(IllegalArgumentException e) {
			LOG.error("Could not open templates from " + templatesPath, e);
		}
		return st;
	}
	
}

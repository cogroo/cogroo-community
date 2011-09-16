package br.usp.ime.cogroo.notifiers;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.BuildUtil;

@Component
public class Notificator {
	
	private static final Logger LOG = Logger
		.getLogger(Notificator.class);
	
	private RssFeed rssFeed;
	private TwitterUtil twitter;
	private EmailSender email;
	
	public Notificator(RssFeed rssFeed, TwitterUtil twitter, EmailSender email) {
		this.rssFeed = rssFeed;
		this.twitter = twitter;
		this.email = email;
	}
	
	public void rssFeed(String title, String link, String value) {
		this.rssFeed.addRssEntry(title, link, value);
	}
	
	public File getRssFeed() {
		return this.rssFeed.getFeedFile();
	}
	
	public void cleanRssFeed() {
		this.rssFeed.clean();
	}

	public void tweet(String text, String link) {
		if(BuildUtil.NOTIFY) {
			this.twitter.tweet(StringEscapeUtils.unescapeHtml(text), link);
		} else {
			LOG.info("Notifications are desabled.");
			LOG.info("... Would tweet: " + text);
			LOG.info("... with link: " + link);
		}
		
	}
	
	public void sendEmail(String body, String subject, String email) {
		//if(BuildUtil.NOTIFY) {
		//	if(LOG.isDebugEnabled()) {
		//		Log.debug("Will send email to " + email);
		//	}
		//	this.email.sendEmail(body, subject, email);
		//} else {
			LOG.info("Notifications are disabled.");
			LOG.info("... Would email: " + body);
			LOG.info("... with subject: " + subject);
			LOG.info("... with email: " + email);
		//}
		
	}
	
	public void sendEmail(String unescapeHtml, String subject,
			Set<User> userList) {
		//if(BuildUtil.NOTIFY) {
		//	if(LOG.isDebugEnabled()) {
		//		Log.debug("Will send email to " + Arrays.toString(userList.toArray()));
		//	}
		//	this.email.sendEmail(unescapeHtml, subject, userList);
		//} else {
			LOG.info("Notifications are disabled.");
			LOG.info("... Would email: " + unescapeHtml);
			LOG.info("... with subject: " + subject);
			LOG.info("... to users: " + Arrays.toString(userList.toArray()));
		//}
		
	}

}

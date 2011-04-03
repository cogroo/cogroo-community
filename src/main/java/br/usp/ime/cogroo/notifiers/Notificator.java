package br.usp.ime.cogroo.notifiers;

import java.io.File;
import java.util.Set;

import org.jfree.util.Log;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;

@Component
public class Notificator {
	
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
		this.twitter.tweet(text, link);
	}
	
	public void sendEmail(String body, String subject, String users) {
		Log.debug("Will send email to " + users);
		this.email.sendEmail(body, subject, users);
	}
	
	public void sendEmail(String unescapeHtml, String subject,
			Set<User> userList) {
		this.email.sendEmail(unescapeHtml, subject, userList);
	}

}

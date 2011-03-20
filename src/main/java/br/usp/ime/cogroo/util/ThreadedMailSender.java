package br.usp.ime.cogroo.util;

import java.util.Set;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import br.usp.ime.cogroo.model.User;

public 	class ThreadedMailSender extends Thread {
	private static final Logger LOG = Logger
		.getLogger(ThreadedMailSender.class);
	
	private String body;
	private String subject;
	private Set<User> users;
	
	public void run() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Started ThreadedMailSender...");
		}
		for (User user : users) {
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Enviando e-mail para " + user.getEmail() + " assunto: " + subject);
				}
				EmailSender.sendEmail(body, subject, user.getEmail());
			} catch (EmailException e) {
				LOG.error("Error sending email to user " + user + " with email " + user.getEmail());
			}
		}
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	
}

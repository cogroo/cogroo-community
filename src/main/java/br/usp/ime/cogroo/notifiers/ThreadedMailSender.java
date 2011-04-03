package br.usp.ime.cogroo.notifiers;

import java.util.Set;

import org.apache.log4j.Logger;

import br.usp.ime.cogroo.model.User;

class ThreadedMailSender extends Thread {
	private static final Logger LOG = Logger
			.getLogger(ThreadedMailSender.class);

	private String body;
	private String subject;
	private Set<User> users;
	private EmailSender emailSender;

	public ThreadedMailSender(EmailSender emailSender) {
		this.emailSender = emailSender;
	}

	public void run() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Started ThreadedMailSender...");
		}
		for (User user : users) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Enviando e-mail para " + user.getEmail()
						+ " assunto: " + subject);
			}
			emailSender.sendEmail(body, subject, user.getEmail());
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

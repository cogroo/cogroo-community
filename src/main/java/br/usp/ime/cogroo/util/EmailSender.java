package br.usp.ime.cogroo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import br.usp.ime.cogroo.model.User;

public class EmailSender {
	
	private static final Logger LOG = Logger
		.getLogger(EmailSender.class);
	
	public static final String FOOTER = 
			"\n" +
			"--\n" +
			"CoGrOO Comunidade <http://ccsl.ime.usp.br/cogroo/comunidade/>\n" +
			"CoGrOO é o Corretor Gramatical para o BrOffice. Você é parte dessa comunidade!";
	
	private final static String FROM_NAME = "CoGrOO Comunidade";
	private final static String SUBJECT_PREFFIX = "[CoGrOO Comunidade] ";
	private final static String BASE_EMAIL;
	private final static String NOREPLY_EMAIL;
	
	
	private final static List<InternetAddress> REPLYTO = new ArrayList<InternetAddress>(1);
	static {
		
		BASE_EMAIL = BuildUtil.EMAIL_SYSTEM_USR;
		NOREPLY_EMAIL = BASE_EMAIL.replace("@", "-noreply@");
		try {
			REPLYTO.add(new InternetAddress(NOREPLY_EMAIL));
		} catch (AddressException e) {
		}
	}

	public static void sendEmail(String body, String subject, String toEmail)
		throws EmailException {
		StringBuffer sb = new StringBuffer("");
		sb.append(body + FOOTER);

		Email email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");

		email.setDebug(true);
		email.setSSL(true);
		email.addTo(toEmail);
		email.setAuthentication(BASE_EMAIL, BuildUtil.EMAIL_SYSTEM_PWD);
		email.setFrom(BuildUtil.EMAIL_SYSTEM_USR, FROM_NAME);
		email.setReplyTo(REPLYTO);
		email.setSubject(SUBJECT_PREFFIX + subject);
		email.setMsg(sb.toString());

		if(LOG.isDebugEnabled()) {
			LOG.debug("Will send mail:\n" + email.toString());
		}
		email.send();
	}
	
	public static void sendEmail(String body, String subject, Set<User> users) {
		ThreadedMailSender sender = new ThreadedMailSender();
		sender.setBody(body);
		sender.setSubject(subject);
		sender.setUsers(users);
		sender.start();
	}



}

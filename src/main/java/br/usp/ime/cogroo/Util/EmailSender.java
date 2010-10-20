package br.usp.ime.cogroo.Util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailSender {

	public static void sendEmail(String body) throws EmailException {
		sendEmail(body, "Sem assunto.");
	}

	public static void sendEmail(String body, String subject)
			throws EmailException {

		StringBuffer sb = new StringBuffer("");
		sb.append(body);

		Email email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");

		email.setDebug(true);
		email.setSSL(true);

		email.addTo("_DESTINATARIO_@foo.bar.com");
		email.setAuthentication("_USER_", "_PASSWORD_");
		email.setFrom("_USER_@gmail.com");
		email.setSubject(subject);
		email.setMsg(sb.toString());

		email.send();
	}

	public static void main(String[] args) {
		try {
			sendEmail("Mais um teste !", "Falha no sistema.");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package br.usp.ime.cogroo.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailSender {

	public static void sendEmail(String body, String subject, String toEmail)
	throws EmailException {
		StringBuffer sb = new StringBuffer("");
		sb.append(body);

		Email email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");

		email.setDebug(true);
		email.setSSL(true);

		email.addTo(toEmail);
		email.setAuthentication(BuildUtil.EMAIL_SYSTEM_USR, BuildUtil.EMAIL_SYSTEM_PWD);
		email.setFrom(BuildUtil.EMAIL_SYSTEM_USR);
		email.setSubject(subject);
		email.setMsg(sb.toString());

		email.send();
		
	}

	public static void main(String[] args) {
		try {
			sendEmail("Teste de envio sem senha", "Será q foi?.", "wesley.seidel@gmail.com");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

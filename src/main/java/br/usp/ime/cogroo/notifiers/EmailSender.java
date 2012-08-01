package br.usp.ime.cogroo.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.BuildUtil;

@Component
@ApplicationScoped
class EmailSender {
	
	private static final Logger LOG = Logger
		.getLogger(EmailSender.class);
	
	public static final String FOOTER = 
			"<br>" +
			"_______________________________________________<br>" +
			"CoGrOO Comunidade &lt;<a href=\"" + BuildUtil.BASE_URL + "\">" + BuildUtil.BASE_URL + "</a>&gt;<br>" +
			"CoGrOO é o Corretor Gramatical para o Apache Open|LibreOffice. Você é parte dessa comunidade!<br>" +
			"Curta o CoGrOO no <a href='http://www.facebook.com/pages/CoGrOO/191205774239878'>Facebook</a>, " +
			"acompanhe a movimentação da Comunidade <br>no <a href='http://twitter.com/cogrcom'>@CoGrCom</a> " +
			"e siga o <a href='http://twitter.com/cogroo'>@CoGrOO</a> para novidades do projeto!<br />" +
			"Caso não queira mais receber estas notificações altere as configurações <a href=\"" + BuildUtil.BASE_URL + "/login\">" + BuildUtil.BASE_URL + "/login</a>";
	
	private final static String FROM_NAME = "CoGrOO Comunidade";
	private final static String SUBJECT_PREFFIX = "[CoGrOO Comunidade] ";
	private final static String BASE_EMAIL;
	private final static String NOREPLY_EMAIL;
	private final static String SMTP = "mail.gandi.net";
	private Queue<Email> emailQueue = new ConcurrentLinkedQueue<Email>();
	
	private final static List<InternetAddress> REPLYTO = new ArrayList<InternetAddress>(1);
	static {
		
		BASE_EMAIL = BuildUtil.EMAIL_SYSTEM_USR;
		NOREPLY_EMAIL = BASE_EMAIL.replace("@", "-noreply@");
		try {
			REPLYTO.add(new InternetAddress(NOREPLY_EMAIL));
		} catch (AddressException e) {
		}
	}
	
	public EmailSender() {
	  int delay = 0;
	  int period = 15000;  // repeat every 15 sec (less than 5 email per minute).
	  Timer timer = new Timer();

	  timer.scheduleAtFixedRate(new TimerTask() {
        public void run() {
          
          if(!emailQueue.isEmpty()) {
            Email email = emailQueue.poll();
            if(email != null) {
              try {
                String res = email.send();
                if(LOG.isDebugEnabled()) {
                  LOG.debug("Sent email: " + res);
                }
              } catch (EmailException e) {
                LOG.error("Failed to send email.", e);
              }
            }
          }
          
        }
      }, delay, period);
	}

	public void sendEmail(String body, String subject, String toEmail) {
		if (toEmail == null)
			return;
		try {
			StringBuffer sb = new StringBuffer("");
			sb.append(body + FOOTER);

			Email email = new SimpleEmail();
			email.setHostName(SMTP);
			email.setDebug(true);
			email.setSSL(true);
			email.addTo(toEmail);
			email.setAuthentication(BASE_EMAIL, BuildUtil.EMAIL_SYSTEM_PWD);
			email.setFrom(BuildUtil.EMAIL_SYSTEM_USR, FROM_NAME);
			email.setReplyTo(REPLYTO);
			email.setSubject(SUBJECT_PREFFIX + subject);
			email.setContent(sb.toString(), Email.TEXT_HTML);
			if (toEmail.endsWith("hotmail.com"))
				email.setCharset(Email.ISO_8859_1);
			else
				email.setCharset("UTF-8");

			// add it to the send queue
			emailQueue.add(email);
			//email.send();
		} catch (EmailException e) {
			LOG.error("Failed to send email. toEmail: " + toEmail, e);
		}
	}
	
	public void sendEmail(String body, String subject, Set<User> users) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will send a batch of emails.");
		}
		
		for (User user : users) {
		  sendEmail(body, subject, user.getEmail()); 
		}
          
		if(LOG.isDebugEnabled()) {
			LOG.debug("ThreadedMailSender started!");
		}
		
	}
}

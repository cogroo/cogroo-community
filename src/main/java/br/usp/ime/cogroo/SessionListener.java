package br.usp.ime.cogroo;
// TODO estou no package correto?

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import br.usp.ime.cogroo.model.User;

// XXX Acho que a annotation só funciona com Tomcat 7, pois é Servlets 3.0.
//@WebListener
/**
 * @author Michel
 */
public class SessionListener implements HttpSessionListener {

	private static final String CONTADOR_SESSAO = "contadorSessao";

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("incrementandooooooooooo...");

		Integer contador = (Integer) se.getSession().getServletContext()
				.getAttribute(CONTADOR_SESSAO);
		if (contador == null) {
			contador = 0;
		}
		contador++;
		se.getSession().getServletContext()
				.setAttribute(CONTADOR_SESSAO, contador);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		System.out.println("decrementando...");

		Integer contador = (Integer) se.getSession().getServletContext()
				.getAttribute(CONTADOR_SESSAO);
		if (contador == null) {
			contador = 0;
		}
		contador--;
		se.getSession().getServletContext()
				.setAttribute(CONTADOR_SESSAO, contador);
		
		// FIXME Fazer logout do usuário atual
/*		User user = loggedUser.getUser();
		user.setLogged(false);
		userDAO.update(user);
		loggedUser.logout();*/
	}

}

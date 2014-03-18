package br.usp.ime.cogroo.model;

import java.io.Serializable;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

@Component
@SessionScoped
public class LoggedUser implements Serializable {

	private static final long serialVersionUID = -2609145744016713400L;

	private User user = null;
	private final ApplicationData appData;
	private String lastURLVisited;

	public LoggedUser(ApplicationData appData) {
		this.appData = appData;
	}

	public boolean isLogged(){
		return (this.user != null);
	}

	public void login(User user) {
		appData.addLoggedUser(user);
		this.user = user;
	}

	/**
	 * @deprecated Use {@link #login(User)} instead
	 */
	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void logout(){
		appData.removeLoggedUser(user);
		user = null;
	}

	public void setLastURLVisited(String lastURLVisited) {
		this.lastURLVisited = lastURLVisited;
	}

	public String getLastURLVisited() {
		return lastURLVisited;
	}

}

package br.usp.ime.cogroo.model;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

@Component
@SessionScoped
public class LoggedUser {

	private User user = null;
	private final ApplicationData appData;
	
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
	
	@Deprecated
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

}

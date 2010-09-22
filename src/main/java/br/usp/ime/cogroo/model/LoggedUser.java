package br.usp.ime.cogroo.model;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

@Component
@SessionScoped
public class LoggedUser {

	private User user = null;

	public boolean isLogged(){
		return (this.user != null);
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	
	public void logout(){
		user = null;
	}

}

package br.usp.ime.cogroo.model;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

@Component
@SessionScoped
public class LoggedUser {

	private User user = null;

	public boolean isLogged(){
		if (this.user != null) {
			user.setLastLogin(System.currentTimeMillis());
			// TODO gravar no banco. Como?
			return true;
		}
		return false;
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

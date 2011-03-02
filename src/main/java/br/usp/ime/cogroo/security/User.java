package br.usp.ime.cogroo.security;

public class User extends Role {
	
	public static final String ROLE_NAME = "user";
	
	@Override
	public String getRoleName() {
		return ROLE_NAME;
	}
	
	@Override
	public boolean getCanDeleteOwnCommment() {
		return true;
	}
	
	@Override
	public boolean getCanDeleteOwnErrorReport() {
		return true;
	}

}

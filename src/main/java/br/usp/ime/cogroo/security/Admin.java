package br.usp.ime.cogroo.security;

public class Admin extends Developer {
	
	public final static String ROLE_NAME = "admin";
	
	@Override
	public String getRoleName() {
		return "admin";
	}
	
	@Override
	public boolean getCanSetUserRole() {
		return true;
	}
	
	@Override
	public boolean getCanViewSensitiveUserDetails() {
		return true;
	}
	
	@Override
	public boolean getCanEditSensitiveUserDetails() {
		return true;
	}
	
}

package br.usp.ime.cogroo.security;

public abstract class Role {
	
	public abstract String getRoleName();
	
	public boolean getCanDeleteOwnCommment() {
		return false;
	}
	
	public boolean getCanDeleteOwnErrorReport() {
		return false;
	}
	
	public boolean getCanDeleteOtherUserCommment() {
		return false;
	}
	
	public boolean getCanDeleteOtherUserErrorReport() {
		return false;
	}
	
	public boolean getCanSetErrorReportPriority() {
		return false;
	}
	
	public boolean getCanSetErrorReportState() {
		return false;
	}
	
	public boolean getCanEditErrorReport() {
		return false;
	}
	
	public boolean getCanSetUserRole() {
		return false;
	}
	
	@Override
	public String toString() {
		return getRoleName();
	}
}

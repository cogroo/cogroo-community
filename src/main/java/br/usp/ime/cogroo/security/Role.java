package br.usp.ime.cogroo.security;

public abstract class Role {
	
	public abstract String getRoleName();
	
	public boolean canDeleteOwnCommment() {
		return false;
	}
	
	public boolean canDeleteOwnErrorReport() {
		return false;
	}
	
	public boolean canDeleteOtherUserCommment() {
		return false;
	}
	
	public boolean canDeleteOtherUserErrorReport() {
		return false;
	}
	
	public boolean canSetErrorReportPriority() {
		return false;
	}
	
	public boolean canSetErrorReportState() {
		return false;
	}
	
	public boolean canSetUserRole() {
		return false;
	}

	@Override
	public String toString() {
		return getRoleName();
	}
}

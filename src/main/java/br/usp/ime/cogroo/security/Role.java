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
	
	public boolean getCanViewSensitiveUserDetails() {
		return false;
	}
	
	public boolean getCanEditSensitiveUserDetails() {
		return false;
	}
	
	public boolean getCanManageRSS() {
		return false;
	}
	
	public boolean getCanRefreshRuleStatus() {
	  return false;
	}
	
	@Override
	public String toString() {
		return getRoleName();
	}
}

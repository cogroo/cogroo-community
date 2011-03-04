package br.usp.ime.cogroo.security;

public class Linguist extends User {
	
	@Override
	public String getRoleName() {
		return "linguist";
	}
	
	@Override
	public boolean getCanSetErrorReportPriority() {
		return true;
	}
	
	@Override
	public boolean getCanSetErrorReportState() {
		return true;
	}
	
	@Override
	public boolean getCanEditErrorReport() {
		return true;
	}

}

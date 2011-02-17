package br.usp.ime.cogroo.security;

public class Linguist extends User {
	
	@Override
	public String getRoleName() {
		return "linguist";
	}
	
	@Override
	public boolean canSetErrorReportPriority() {
		return true;
	}
	
	@Override
	public boolean canSetErrorReportState() {
		return true;
	}

}

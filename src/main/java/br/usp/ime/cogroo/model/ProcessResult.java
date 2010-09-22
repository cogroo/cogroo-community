package br.usp.ime.cogroo.model;

public class ProcessResult {
	private String textAnnotatedWithErrors;
	private String verticalSyntaxTree;
	private String syntaxTree;
	public String getTextAnnotatedWithErrors() {
		return textAnnotatedWithErrors;
	}
	public void setTextAnnotatedWithErrors(String textAnnotatedWithErrors) {
		this.textAnnotatedWithErrors = textAnnotatedWithErrors;
	}
	public String getVerticalSyntaxTree() {
		return verticalSyntaxTree;
	}
	public void setVerticalSyntaxTree(String verticalSyntaxTree) {
		this.verticalSyntaxTree = verticalSyntaxTree;
	}
	public String getSyntaxTree() {
		return syntaxTree;
	}
	public void setSyntaxTree(String syntaxTree) {
		this.syntaxTree = syntaxTree;
	}
}

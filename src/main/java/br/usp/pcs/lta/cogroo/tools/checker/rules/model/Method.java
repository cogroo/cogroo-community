package br.usp.pcs.lta.cogroo.tools.checker.rules.model;

import java.util.Set;

public class Method {
	private int id;
	private Rule.Method method;
	private Set<Rule> rules;

	public Method() {
	}

	public Method(Rule.Method n) {
		this.method = n;
	}

	public void setId(int i) {
		id = i;
	}

	public int getId() {
		return id;
	}

	public void setMethod(Rule.Method n) {
		method = n;
	}

	public Rule.Method getMethod() {
		return method;
	}

	public void setRules(Set<Rule> l) {
		rules = l;
	}

	public Set<Rule> getRules() {
		return rules;
	}
}

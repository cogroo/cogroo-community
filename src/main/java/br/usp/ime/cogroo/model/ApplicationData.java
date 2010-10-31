package br.usp.ime.cogroo.model;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class ApplicationData {

	private AtomicInteger onlineMembers = new AtomicInteger();
	private AtomicInteger reportedErrors = new AtomicInteger();
	private AtomicInteger registeredMembers = new AtomicInteger();
	private AtomicInteger onlineUsers = new AtomicInteger();

	@PostConstruct
	public void populate() {
		// TODO This method is run after the creation of the first instance of
		// this class. Should consult DB and initiate variables, but DAO is not
		// promptly available. Maybe a solution is related to
		// http://www.guj.com.br/posts/list/200676.java .
	}

	public int getOnlineMembers() {
		return this.onlineMembers.get();
	}

	public int getReportedErrors() {
		return this.reportedErrors.get();
	}

	public int getOnlineVisits() {
		return this.onlineUsers.get() - this.onlineMembers.get();
	}

	public int getTotalMembers() {
		return this.registeredMembers.get();
	}

	public String getVersion() {
		return "Versão TESTE";
	}

	/*
	 * public void setOnlineMembers(int onlineMembers) { this.onlineMembers =
	 * onlineMembers; }
	 */

	public void setReportedErrors(int reportedErrors) {
		this.reportedErrors.set(reportedErrors);
	}

	public void setRegisteredMembers(int registeredMembers) {
		this.registeredMembers.set(registeredMembers);
	}

	public void setOnlineUsers(int onlineUsers) {
		this.onlineUsers.set(onlineUsers);
	}

	public int getRegisteredMembers() {
		return registeredMembers.get();
	}

	public int getOnlineUsers() {
		return onlineUsers.get();
	}

	public void incOnlineUsers() {
		this.onlineUsers.incrementAndGet();
	}

	public void decOnlineUsers() {
		this.onlineUsers.decrementAndGet();
	}

	public void incOnlineMembers() {
		this.onlineMembers.incrementAndGet();
	}

	public void decOnlineMembers() {
		this.onlineMembers.decrementAndGet();
	}
}

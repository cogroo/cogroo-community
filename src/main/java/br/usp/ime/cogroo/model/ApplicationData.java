package br.usp.ime.cogroo.model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class ApplicationData {

	private AtomicInteger reportedErrors = new AtomicInteger();
	private AtomicInteger dictionaryEntries = new AtomicInteger();
	private AtomicInteger registeredMembers = new AtomicInteger();
	
	private AtomicInteger onlineUsers = new AtomicInteger();
	private AtomicInteger onlineMembers = new AtomicInteger();
	
	private ArrayList<User> loggedUsers = new ArrayList<User>();

	@PostConstruct
	public void populate() {
		// TODO This method is run after the creation of the first instance of
		// this class. Should consult DB and initiate variables, but DAO is not
		// promptly available. Maybe a solution is related to
		// http://www.guj.com.br/posts/list/200676.java .
	}
	
	public int getReportedErrors() {
		return this.reportedErrors.get();
	}
	
	public void setReportedErrors(int reportedErrors) {
		this.reportedErrors.set(reportedErrors);
	}

	public int getDictionaryEntries() {
		return dictionaryEntries.get();
	}

	public void setDictionaryEntries(int dictionaryEntries) {
		this.dictionaryEntries.set(dictionaryEntries);
	}

	public void setRegisteredMembers(int registeredMembers) {
		this.registeredMembers.set(registeredMembers);
	}
	
	public int getRegisteredMembers() {
		return registeredMembers.get();
	}
	
	public int getOnlineUsers() {
		return onlineUsers.get();
	}
	
	public void setOnlineUsers(int onlineUsers) {
		this.onlineUsers.set(onlineUsers);
	}

	public void incOnlineUsers() {
		this.onlineUsers.incrementAndGet();
	}

	public void decOnlineUsers() {
		this.onlineUsers.decrementAndGet();
	}
	
	public int getOnlineMembers() {
		return this.onlineMembers.get();
	}
	
	/*
	 * public void setOnlineMembers(int onlineMembers) { this.onlineMembers =
	 * onlineMembers; }
	 */
	
	public void incOnlineMembers() {
		this.onlineMembers.incrementAndGet();
	}

	public void decOnlineMembers() {
		this.onlineMembers.decrementAndGet();
	}

	public int getOnlineVisits() {
		return this.onlineUsers.get() - this.onlineMembers.get();
	}
	
	public ArrayList<User> getLoggedUsers() {
		return loggedUsers;
	}
	
	public void addLoggedUser(User user) {
		this.loggedUsers.add(user);
	}
	
	public void removeLoggedUser(User user) {
		this.loggedUsers.remove(user);
	}
}

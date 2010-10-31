package br.usp.ime.cogroo.model;

import java.util.ArrayList;
import java.util.List;
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
	
	private List<User> loggedUsers = new ArrayList<User>();
	private List<User> idleUsers = new ArrayList<User>();
	private List<User> topUsers = new ArrayList<User>();

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
		return this.loggedUsers.size();
	}

	public int getOnlineVisits() {
		return this.onlineUsers.get() - this.loggedUsers.size();
	}
	
	public List<User> getLoggedUsers() {
		return loggedUsers;
	}
	
	public void addLoggedUser(User user) {
		this.loggedUsers.add(user);
	}
	
	public void removeLoggedUser(User user) {
		this.loggedUsers.remove(user);
	}

	public List<User> getIdleUsers() {
		return idleUsers;
	}
	
	public void setIdleUsers(List<User> idleUsers) {
		this.idleUsers = idleUsers;
	}

	public List<User> getTopUsers() {
		return topUsers;
	}

	public void setTopUsers(List<User> topUsers) {
		this.topUsers = topUsers;
	}
}

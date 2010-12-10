package br.usp.ime.cogroo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String login;
	
	@Column
	private long lastLogin;

	/**
	 * password cripto
	 */
	@Column(length = 32)
	private String password;
	
	@Column(length = 80)
	private String email;

	@Column(length = 80)
	private String name;
	
	@Transient
	private Date previousLogin;

	@Transient
	private Date cachedLastLogin = null;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<WordUser> wordUserList = new ArrayList<WordUser>();

	public User() {
	}

	public User(String login) {
		this.login = login;
	}

	public User(String login, long id) {
		this.login = login;
		this.id = id;
	}

	public Date getLastLogin() {
		if(cachedLastLogin == null) {
			cachedLastLogin = (lastLogin != 0) ? new Date(lastLogin) : null;
		}
		return cachedLastLogin;
	}
	
	public Date getPreviousLogin() {
		if(previousLogin == null) {
			previousLogin = (lastLogin != 0) ? new Date(lastLogin) : null;
		}
		return previousLogin;
	}

	public void setLastLogin(long lastLogin) {
		// save previous login
		this.previousLogin = getLastLogin();
		this.cachedLastLogin = null;
		this.lastLogin = lastLogin;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setWordUserList(List<WordUser> wordUserList) {
		this.wordUserList = wordUserList;
	}

	public List<WordUser> getWordUserList() {
		return wordUserList;
	}

	@Override
	public String toString() {
		return "[Login: " + getLogin() + "; id: " + getId() + "]";
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public String getLogin() {
		return login;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}

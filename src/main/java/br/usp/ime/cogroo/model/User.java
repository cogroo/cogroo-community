package br.usp.ime.cogroo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String name;

	/**
	 * password cripto
	 */
	@Column(length = 32)
	private String password;
	
	@Column(length = 80)
	private String email;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<WordUser> wordUserList = new ArrayList<WordUser>();

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public User(String aName, long id) {
		this.name = aName;
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
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
		return "[name: " + getName() + "; id: " + getId() + "]";
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

}

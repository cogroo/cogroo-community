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

import br.usp.ime.cogroo.security.Role;
import br.usp.ime.cogroo.security.RoleProvider;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String login;
	
	@Column
	private Long lastLogin;

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
	
	@Column(length = 10)
	private String roleName;
	
	@Column
	private Date dateRecoverCode;
	
	@Column(length = 32)
	private String recoverCode;

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
			cachedLastLogin = (lastLogin != null && lastLogin != 0) ? new Date(lastLogin) : null;
		}
		return cachedLastLogin;
	}
	
	public Date getPreviousLogin() {
		if(previousLogin == null) {
			previousLogin = (lastLogin != null && lastLogin != 0) ? new Date(lastLogin) : null;
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

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
	
	@Transient
	public Role getRole() {
		Role r = RoleProvider.getInstance().getRoleForName(roleName);
		if(r == null) {
			r = RoleProvider.getInstance().getRoleForName(br.usp.ime.cogroo.security.User.ROLE_NAME);
		}
		return r;
	}
	
	@Transient
	public void setRole(Role role) {
		this.roleName = role.getRoleName();
	}

	public void setDateRecoverCode(Date dateRecoverCode) {
		this.dateRecoverCode = dateRecoverCode;
	}

	public Date getDateRecoverCode() {
		return dateRecoverCode;
	}

	public void setRecoverCode(String recoverCode) {
		this.recoverCode = recoverCode;
	}

	public String getRecoverCode() {
		return recoverCode;
	}

}

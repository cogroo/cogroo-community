package br.usp.ime.cogroo.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RoleProvider {
	
	private static RoleProvider instance;
	
	private static Map<String, Role> roleMap; 
	
	private RoleProvider() {
		// prevents creation
	}
	
	public synchronized static RoleProvider getInstance() {
		if(instance != null) {
			return instance;
		} else {
			instance = new RoleProvider();
			init();
			return instance;
		}
	}

	private static void init() {
		Map<String, Role> m = new HashMap<String, Role>(4);
		
		Role admin = new Admin();
		m.put(admin.getRoleName(), admin);
		
		Role developer = new Developer();
		m.put(developer.getRoleName(), developer);
		
		Role linguist = new Linguist();
		m.put(linguist.getRoleName(), linguist);
		
		Role user = new User();
		m.put(user.getRoleName(), user);
		
		roleMap = Collections.unmodifiableMap(m);
	}
	
	public Role getRoleForName(String roleName) {
		return roleMap.get(roleName);
	}

	public Collection<Role> getRoles() {
		return roleMap.values();
	}
}

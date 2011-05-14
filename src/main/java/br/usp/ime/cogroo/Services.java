package br.usp.ime.cogroo;

import java.util.HashSet;
import java.util.Set;

public class Services {
	private static final Set<String> SERVICES = new HashSet<String>();
	static {
		SERVICES.add("facebook");
//		SERVICES.add("google");
//		SERVICES.add("hotmail");
		SERVICES.add("linkedin");
		SERVICES.add("twitter");
		SERVICES.add("yahoo");
	}
	
	public static boolean contains(String service) {
		return SERVICES.contains(service);
	}
}

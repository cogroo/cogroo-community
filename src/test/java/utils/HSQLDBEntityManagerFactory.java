package utils;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HSQLDBEntityManagerFactory {
	
	public static EntityManager createEntityManager(){
		Map<String, String> mapa = new HashMap<String, String>();
		EntityManagerFactory emf;
		EntityManager em;
		
		mapa.put("hibernate.connection.url", "jdbc:hsqldb:mem:test");
		mapa.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");   
		mapa.put("hibernate.connection.username", "sa");   
		mapa.put("hibernate.connection.password", "");
		mapa.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		mapa.put("hibernate.hbm2ddl.auto", "create");


		emf = Persistence.createEntityManagerFactory("default", mapa);
		em = emf.createEntityManager();
		emf.close();
		
		return em;
	}
}

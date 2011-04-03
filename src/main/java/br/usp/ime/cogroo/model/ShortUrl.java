package br.usp.ime.cogroo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ShortUrl {

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 80)
	private String url;
	
	@Column(length = 80)
	private String shortURL;
	
	
	public ShortUrl() {
	}

	public ShortUrl(String url, String shortURL) {
		this.shortURL = shortURL;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShortURL() {
		return shortURL;
	}

	public void setShortURL(String shortURL) {
		this.shortURL = shortURL;
	}
}

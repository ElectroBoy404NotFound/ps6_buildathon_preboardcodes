package me.electronicsboy.titly.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "invalid_jwts")
@Entity
public class InvalidatedJWT {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invalid_jwts_seq")
	@SequenceGenerator(name = "invalid_jwts_seq", sequenceName = "invalid_jwts_sequence", allocationSize = 1)
	@Column(nullable = false)
    private long id;
	
	@Column(nullable = false)
    private String token;
	
	@Column(nullable = false)
	private Date expiryTime;

	public InvalidatedJWT(long id, String token, Date expiryTime) {
		super();
		this.id = id;
		this.token = token;
		this.expiryTime = expiryTime;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the expiryTime
	 */
	public Date getExpiryTime() {
		return expiryTime;
	}

	/**
	 * @param expiryTime the expiryTime to set
	 */
	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
}

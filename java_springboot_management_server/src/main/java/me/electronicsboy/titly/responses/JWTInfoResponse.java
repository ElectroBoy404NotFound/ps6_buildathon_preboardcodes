package me.electronicsboy.titly.responses;

public class JWTInfoResponse {
	private String token;
	private boolean expired;
	private String username;
	private String issuedAt;
	
	public JWTInfoResponse(String token, boolean expired, String username, String issuedAt) {
		this.token = token;
		this.expired = expired;
		this.username = username;
		this.issuedAt = issuedAt;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean isExpired() {
		return expired;
	}
	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}
}

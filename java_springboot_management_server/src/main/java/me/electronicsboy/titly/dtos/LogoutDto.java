package me.electronicsboy.titly.dtos;

public class LogoutDto {
	private String refreshToken;
	private String jwtToken;	
	
	public LogoutDto(String refreshToken, String jwtToken) {
		this.refreshToken = refreshToken;
		this.jwtToken = jwtToken;
	}
	
	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	/**
	 * @return the jwtToken
	 */
	public String getJwtToken() {
		return jwtToken;
	}
	/**
	 * @param jwtToken the jwtToken to set
	 */
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}
}

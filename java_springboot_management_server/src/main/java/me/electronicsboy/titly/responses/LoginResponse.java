package me.electronicsboy.titly.responses;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;

    public String getToken() {
        return token;
    }
    public String getRefreshToken() {
    	return refreshToken;
    }

	public long getExpiresIn() {
		return expiresIn;
	}

	public LoginResponse setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
		return this;
	}

	public LoginResponse setToken(String token) {
		this.token = token;
		return this;
	}
	public LoginResponse setRefreshToken(String token) {
		this.refreshToken = token;
		return this;
	}
}
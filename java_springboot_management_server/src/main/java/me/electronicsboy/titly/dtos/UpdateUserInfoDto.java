package me.electronicsboy.titly.dtos;

import jakarta.validation.constraints.NotNull;
import me.electronicsboy.titly.data.PrivilegeLevel;

public class UpdateUserInfoDto {
    @NotNull
	private long userid;
	
	private String username; 
    private String password;
    private String email;
    private String fullname;
    private PrivilegeLevel privilageLevel;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public PrivilegeLevel getPrivilageLevel() {
		return privilageLevel;
	}

	public void setPrivilageLevel(PrivilegeLevel privilageLevel) {
		this.privilageLevel = privilageLevel;
	}
}
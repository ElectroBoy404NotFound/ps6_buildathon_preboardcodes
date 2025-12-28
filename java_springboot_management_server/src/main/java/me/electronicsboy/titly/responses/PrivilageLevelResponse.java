package me.electronicsboy.titly.responses;

import me.electronicsboy.titly.data.PrivilegeLevel;

public class PrivilageLevelResponse {
    private PrivilegeLevel privilageLevel;

	public PrivilegeLevel getPrivilageLevel() {
		return privilageLevel;
	}

	public PrivilageLevelResponse setPrivilageLevel(PrivilegeLevel privilageLevel) {
		this.privilageLevel = privilageLevel;
		return this;
	}
}
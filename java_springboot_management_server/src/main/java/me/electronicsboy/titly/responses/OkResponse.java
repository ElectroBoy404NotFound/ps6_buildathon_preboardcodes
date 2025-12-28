package me.electronicsboy.titly.responses;

public class OkResponse {
	private String message;

	public OkResponse(String string) {
		this.message = string;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
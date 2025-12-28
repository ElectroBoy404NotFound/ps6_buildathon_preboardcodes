package me.electronicsboy.titly.exceptions;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -4238757349107809305L;

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}

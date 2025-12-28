package me.electronicsboy.titly.exceptions;

public class InvalidJWTException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidJWTException(String message) {
		super(message);
	}

	public InvalidJWTException(Throwable cause) {
		super(cause);
	}

	public InvalidJWTException(String message, Throwable cause) {
		super(message, cause);
	}
}
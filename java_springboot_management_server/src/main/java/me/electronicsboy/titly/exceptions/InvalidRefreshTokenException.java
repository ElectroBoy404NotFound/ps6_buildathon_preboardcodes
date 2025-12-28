package me.electronicsboy.titly.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidRefreshTokenException(String message) {
		super(message);
	}

	public InvalidRefreshTokenException(Throwable cause) {
		super(cause);
	}

	public InvalidRefreshTokenException(String message, Throwable cause) {
		super(message, cause);
	}
}
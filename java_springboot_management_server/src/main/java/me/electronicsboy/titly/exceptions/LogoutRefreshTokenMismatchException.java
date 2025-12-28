package me.electronicsboy.titly.exceptions;

public class LogoutRefreshTokenMismatchException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public LogoutRefreshTokenMismatchException(String message) {
		super(message);
	}

	public LogoutRefreshTokenMismatchException(Throwable cause) {
		super(cause);
	}

	public LogoutRefreshTokenMismatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
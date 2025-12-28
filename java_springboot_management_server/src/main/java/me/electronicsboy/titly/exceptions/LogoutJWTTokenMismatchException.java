package me.electronicsboy.titly.exceptions;

public class LogoutJWTTokenMismatchException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public LogoutJWTTokenMismatchException(String message) {
		super(message);
	}

	public LogoutJWTTokenMismatchException(Throwable cause) {
		super(cause);
	}

	public LogoutJWTTokenMismatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
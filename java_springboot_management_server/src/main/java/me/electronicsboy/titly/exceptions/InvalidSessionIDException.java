package me.electronicsboy.titly.exceptions;

public class InvalidSessionIDException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidSessionIDException(String message) {
		super(message);
	}

	public InvalidSessionIDException(Throwable cause) {
		super(cause);
	}

	public InvalidSessionIDException(String message, Throwable cause) {
		super(message, cause);
	}
}
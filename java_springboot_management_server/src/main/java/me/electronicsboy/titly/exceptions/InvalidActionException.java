package me.electronicsboy.titly.exceptions;

public class InvalidActionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidActionException(String message) {
		super(message);
	}

	public InvalidActionException(Throwable cause) {
		super(cause);
	}

	public InvalidActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
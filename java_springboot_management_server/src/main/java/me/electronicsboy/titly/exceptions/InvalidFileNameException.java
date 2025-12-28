package me.electronicsboy.titly.exceptions;

public class InvalidFileNameException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidFileNameException(String message) {
		super(message);
	}

	public InvalidFileNameException(Throwable cause) {
		super(cause);
	}

	public InvalidFileNameException(String message, Throwable cause) {
		super(message, cause);
	}
}
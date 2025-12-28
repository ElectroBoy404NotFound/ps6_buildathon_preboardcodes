package me.electronicsboy.titly.exceptions;

public class InvalidFileException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidFileException(String message) {
		super(message);
	}

	public InvalidFileException(Throwable cause) {
		super(cause);
	}

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
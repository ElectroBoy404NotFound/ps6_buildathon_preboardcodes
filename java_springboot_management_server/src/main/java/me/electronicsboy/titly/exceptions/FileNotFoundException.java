package me.electronicsboy.titly.exceptions;

public class FileNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public FileNotFoundException(String message) {
		super(message);
	}

	public FileNotFoundException(Throwable cause) {
		super(cause);
	}

	public FileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
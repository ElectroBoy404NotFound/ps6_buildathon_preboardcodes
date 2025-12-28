package me.electronicsboy.titly.exceptions;

public class FileErrorException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public FileErrorException(String message) {
		super(message);
	}

	public FileErrorException(Throwable cause) {
		super(cause);
	}

	public FileErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}
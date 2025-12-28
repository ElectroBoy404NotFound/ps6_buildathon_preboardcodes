package me.electronicsboy.titly.exceptions;

public class ConversionFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public ConversionFailedException(String message) {
		super(message);
	}

	public ConversionFailedException(Throwable cause) {
		super(cause);
	}

	public ConversionFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
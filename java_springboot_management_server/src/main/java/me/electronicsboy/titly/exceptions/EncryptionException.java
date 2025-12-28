package me.electronicsboy.titly.exceptions;

public class EncryptionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public EncryptionException(String message) {
		super(message);
	}

	public EncryptionException(Throwable cause) {
		super(cause);
	}

	public EncryptionException(String message, Throwable cause) {
		super(message, cause);
	}
}
package me.electronicsboy.titly.exceptions;

public class InvalidOtpException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5531117888928941268L;

	public InvalidOtpException(String message) {
		super(message);
	}

	public InvalidOtpException(Throwable cause) {
		super(cause);
	}

	public InvalidOtpException(String message, Throwable cause) {
		super(message, cause);
	}
}
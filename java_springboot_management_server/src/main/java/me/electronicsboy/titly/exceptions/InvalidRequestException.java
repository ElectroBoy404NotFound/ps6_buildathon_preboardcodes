package me.electronicsboy.titly.exceptions;

public class InvalidRequestException extends RuntimeException {
	private static final long serialVersionUID = -4165692613033843652L;

	public InvalidRequestException(String message) {
		super(message);
	}

	public InvalidRequestException(Throwable cause) {
		super(cause);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}

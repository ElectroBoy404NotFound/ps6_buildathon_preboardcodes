package me.electronicsboy.titly.exceptions;

public class UnprivilagedExpection extends RuntimeException {
	private static final long serialVersionUID = 6378202378760713867L;

	public UnprivilagedExpection(String message) {
		super(message);
	}

	public UnprivilagedExpection(Throwable cause) {
		super(cause);
	}

	public UnprivilagedExpection(String message, Throwable cause) {
		super(message, cause);
	}
}

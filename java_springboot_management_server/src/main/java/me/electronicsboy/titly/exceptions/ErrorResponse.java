package me.electronicsboy.titly.exceptions;

public class ErrorResponse {
	private int error;
    private String message;
    private String timestamp;
    private String stackTrace;

    public ErrorResponse(ErrorCode ec, String message, String timestamp) {
        this.error = ec.errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public ErrorResponse(ErrorCode ec, String message, String timestamp, String stackTrace) {
        this.error = ec.errorCode;
        this.message = message;
        this.timestamp = timestamp;
        this.stackTrace = stackTrace;
    }

	public int getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getStackTrace() {
		return stackTrace;
	}
	
	public enum ErrorCode {
		GENERAL_MISSING_PARAMETER(1001),
		USER_ALREADY_EXISTS(1002),
		INVALID_USERNAME_OR_PASSWORD(1003),
		LOCKED_ACCOUNT(1004),
		INVALID_TOKEN_SIGNATURE(1005),
		TOKEN_EXPIRED(1006),
		UNPRIVILAGED_USER(1007),
		INVALID_REQUEST(1008),
		USER_NOT_FOUND(1009),
		INVALID_REFRESH_TOKEN(1010),
		INVALID_OTP(1011),
		FILE_NOT_FOUND(1012),
		FILE_ERROR(1013),
		INVALID_FILE(1014),
		INVALID_FILENAME(1015),
		CONVERSION_ERROR(1016),
		INVALID_ACTION(1017),
		
		BROKEN_HTTP(400),
		ACCESS_DENIED(403),
		HTTP_404_NOTFOUND(404),
		
		UNKNOWN_ERROR(9999);
		
		public int errorCode;
		ErrorCode(int errorCode) {
			this.errorCode = errorCode;
		}
	}
}

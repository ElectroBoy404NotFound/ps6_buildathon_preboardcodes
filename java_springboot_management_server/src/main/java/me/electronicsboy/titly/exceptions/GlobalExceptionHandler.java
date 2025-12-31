package me.electronicsboy.titly.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import me.electronicsboy.titly.exceptions.ErrorResponse.ErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private String exceptionAsString(Exception ex) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	ex.printStackTrace(pw);
    	return sw.toString();
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.UNKNOWN_ERROR,
                ex.getMessage(),
                getCurrentTimestamp(),
                exceptionAsString(ex)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.HTTP_404_NOTFOUND,
                "Resource not found",
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.HTTP_404_NOTFOUND,
                "Resource not found",
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleParameterNotFound(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.GENERAL_MISSING_PARAMETER,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUnreadableHTTPMessage(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.BROKEN_HTTP,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.BROKEN_HTTP,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDuplicantUser(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.USER_ALREADY_EXISTS,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_USERNAME_OR_PASSWORD,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(java.util.NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_USERNAME_OR_PASSWORD,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccountStatusException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.LOCKED_ACCOUNT,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleSignatureException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_TOKEN_SIGNATURE,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.TOKEN_EXPIRED,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(UnprivilagedExpection.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleUnprivilagedExpection(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.UNPRIVILAGED_USER,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_REQUEST,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.USER_NOT_FOUND,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.USER_NOT_FOUND,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_REFRESH_TOKEN,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(InvalidOtpException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleInvalidOtpException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_OTP,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.FILE_NOT_FOUND,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(FileErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleFileErrorException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.FILE_ERROR,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(InvalidActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidActionException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_ACTION,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidJWTException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> InvalidJWTExceptionException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.TOKEN_EXPIRED,
                ex.getMessage(),
                getCurrentTimestamp()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}

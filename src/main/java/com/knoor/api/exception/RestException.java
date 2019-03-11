package com.knoor.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Common Rest Controller Exception
 * 
 * @author ahajri
 * @version 1
 */
public class RestException extends Throwable {

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4021459255446898398L;
	
	
	private HttpStatus httpStatus;
	private String code;

	public RestException(Throwable ex) {
		super(ex);
	}

	public RestException(String message, Exception ex, HttpStatus httpStatus, String code) {
		super(message, ex);
		this.httpStatus = httpStatus;
		this.code = code;
	}

	public RestException(String message, Throwable ex, HttpStatus httpStatus, String code) {
		super(message, ex);
		this.httpStatus = httpStatus;
		this.code = code;
	}
	public RestException(String message, Throwable ex, HttpStatus httpStatus) {
		super(message, ex);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

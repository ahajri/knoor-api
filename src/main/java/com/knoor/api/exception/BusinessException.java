package com.knoor.api.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author ahajri
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class BusinessException extends Throwable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8062023690191852034L;
	
	private HttpStatus httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
	
	
	private String functionalMessage;

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String errorMsg) {
        super(errorMsg);
    }

    public BusinessException(Throwable cause, String functionalMessage) {
        super(cause);
        this.functionalMessage = functionalMessage;
    }
    
    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }


   
}

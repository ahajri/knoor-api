package com.knoor.soft.knoorapi.exception;


/**
 * 
 * @author ahajri
 *
 */
public class BusinessException extends Throwable {


	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8062023690191852034L;
	
	
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


    public String getFunctionalMessage() {
        return functionalMessage;
    }
}

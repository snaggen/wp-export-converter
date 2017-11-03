package com.github.snaggen;

public class MarshallingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4189407785708834081L;
	
	public MarshallingException(String reason) {
		super(reason);
	}

	public MarshallingException(String reason, Throwable cause) {
		super(reason, cause);
	}
}

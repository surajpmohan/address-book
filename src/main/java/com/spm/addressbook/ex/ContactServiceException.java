package com.spm.addressbook.ex;

public class ContactServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 6779186108526446163L;

	public ContactServiceException(String message) {
		super(message);
	}

}

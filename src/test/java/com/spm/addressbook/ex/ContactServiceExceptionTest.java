package com.spm.addressbook.ex;

import static org.junit.Assert.*;

import org.junit.Test;

public class ContactServiceExceptionTest {

	@Test
	public void testContactException() {
		final String string = "Test message";
		ContactServiceException ex = new ContactServiceException(string);
		assertTrue(ex.getMessage().equals(string));
	}

}

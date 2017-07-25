package com.spm.addressbook.bean;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.spm.addressbook.dao.bean.ContactEntity;

public class ContactTest {

	@Test
	public void testContactContactEntity() {
		ContactEntity contactEntity = new ContactEntity();
		Date today = new Date();
    	contactEntity.setId(1L);
		contactEntity.setFirstName("firstName");
		contactEntity.setLastName("lastName");
		contactEntity.setPhoneNumber("444-444-4444");
		contactEntity.setEmail("abc@abc.com");
		contactEntity.setAddressLine1("1020 abc");
		contactEntity.setAddressLine2("43");
		contactEntity.setZip("54635");
		contactEntity.setCity("San Ramon");
		contactEntity.setState("CA");
		contactEntity.setCountry("USA");
		contactEntity.setLastContactedDate(today);
		
		Contact contact = new Contact(contactEntity);
		assertEquals(contactEntity.getId(), contact.getId());
		assertEquals(contactEntity.getFirstName(), contact.getFirstName());
		assertEquals(contactEntity.getLastName(), contact.getLastName());
		assertEquals(contactEntity.getPhoneNumber(), contact.getPhoneNumber());
		assertEquals(contactEntity.getEmail(), contact.getEmail());
		assertEquals(contactEntity.getAddressLine1(), contact.getAddressLine1());
		assertEquals(contactEntity.getAddressLine2(), contact.getAddressLine2());
		assertEquals(contactEntity.getZip(), contact.getZip());
		assertEquals(contactEntity.getCity(), contact.getCity());
		assertEquals(contactEntity.getState(), contact.getState());
		assertEquals(contactEntity.getCountry(), contact.getCountry());
		assertEquals(contactEntity.getLastContactedDate(), contact.getLastContactedDate());
	}

}

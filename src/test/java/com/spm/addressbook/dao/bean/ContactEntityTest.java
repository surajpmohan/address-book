package com.spm.addressbook.dao.bean;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.spm.addressbook.bean.Contact;

public class ContactEntityTest {

	@Test
	public void testContactEntityContact() {
		Contact contact = new Contact();
		Date today = new Date();
    	contact.setId(1L);
		contact.setFirstName("firstName");
		contact.setLastName("lastName");
		contact.setPhoneNumber("444-444-4444");
		contact.setEmail("abc@abc.com");
		contact.setAddressLine1("1020 abc");
		contact.setAddressLine2("43");
		contact.setZip("54635");
		contact.setCity("San Ramon");
		contact.setState("CA");
		contact.setCountry("USA");
		contact.setLastContactedDate(today);
		
		ContactEntity entity = new ContactEntity(contact);
		assertEquals(contact.getId(), entity.getId());
		assertEquals(contact.getFirstName(), entity.getFirstName());
		assertEquals(contact.getLastName(), entity.getLastName());
		assertEquals(contact.getPhoneNumber(), entity.getPhoneNumber());
		assertEquals(contact.getEmail(), entity.getEmail());
		assertEquals(contact.getAddressLine1(), entity.getAddressLine1());
		assertEquals(contact.getAddressLine2(), entity.getAddressLine2());
		assertEquals(contact.getZip(), entity.getZip());
		assertEquals(contact.getCity(), entity.getCity());
		assertEquals(contact.getState(), entity.getState());
		assertEquals(contact.getCountry(), entity.getCountry());
		assertEquals(contact.getLastContactedDate(), entity.getLastContactedDate());

	}

}

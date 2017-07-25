package com.spm.addressbook.bo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spm.addressbook.bean.Contact;
import com.spm.addressbook.dao.bean.ContactEntity;
import com.spm.addressbook.dao.repo.ContactRepository;

@RunWith(MockitoJUnitRunner.class)
public class ContactServiceTest {
	
	@Mock
	ContactRepository repository;
	
	@InjectMocks
	ContactService service;
	
	private static List<Contact> contacts;
	private static List<ContactEntity> entities;
	private static ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		contacts = mapper.readValue(this.getClass().getResourceAsStream("/contacts.json"), 
				new TypeReference<List<Contact>>(){});
		contacts.get(0).setLastContactedDate(new Date());
		
		entities = contacts.stream().map(c -> new ContactEntity(c)).collect(Collectors.toList());
	}
	
	@Test
	public void testCreateContact() {
		Mockito.doReturn(entities.get(0)).when(repository).save(eq(entities.get(0)));
		Contact contact = service.create(contacts.get(0));
		assertEquals(contacts.get(0), contact);
	}

	@Test
	public void testUpdateContact() {
		Contact update = new Contact();
		update.setId(entities.get(0).getId());
		update.setFirstName("updated name");
		update.setLastName("updated lastname");
		
		ContactEntity updateEntity = new ContactEntity(update);

		Mockito.doReturn(true).when(repository).exists(eq(update.getId()));
		Mockito.doReturn(updateEntity).when(repository).save(eq(updateEntity));
		
		Contact contact = service.update(update);
		assertEquals(update, contact);
	}
	
	@Test
	public void testPatchContact() {
		Contact patchContact = new Contact();
		patchContact.setFirstName("patched");
		patchContact.setId(entities.get(0).getId());
		
		ContactEntity patchedContact = new ContactEntity();
		BeanUtils.copyProperties(entities.get(0), patchedContact);
		patchedContact.setFirstName("patched");
		
		Contact expectedContact = new Contact(patchedContact);
		Mockito.doReturn(true).when(repository).exists(eq(patchContact.getId()));
		Mockito.doReturn(entities.get(0)).when(repository).findOne(patchContact.getId());
		Mockito.doReturn(patchedContact).when(repository).save(eq(patchedContact));
		Contact contact = service.patch(patchContact);
		assertEquals(expectedContact, contact);
	}

	
	@Test
	public void testFindOne() {
		Mockito.doReturn(entities.get(0)).when(repository).findOne(eq(entities.get(0).getId()));
		Contact contact = service.findOne(contacts.get(0).getId());
		assertEquals(contacts.get(0), contact);
	}

	@Test
	public void testFindAll() {
		Mockito.doReturn(entities).when(repository).findAll();
		List<Contact> contactList = service.findAll();
		assertEquals(entities.size(), contactList.size());
		contactList.forEach(item -> entities.contains(item));
	}

	@Test
	public void testFindByState() {
		List<String> states = new ArrayList<>();
		states.add("TX");
		
		List<ContactEntity> filterd = entities.stream()
				.filter(e -> "TX".equals(e.getState()))
				.collect(Collectors.toList());
		
		Mockito
		.doReturn(filterd)
		.when(repository).findByStates(eq(states));
		List<Contact> txContacts = service.findByState(states);
		assertEquals(1, txContacts.size());
		txContacts.forEach(item -> contacts.contains(item));
	}

	@Test
	public void testFindByDateRange() {
		String startDate = "04/04/2017";
		String endDate = "06/04/2017";
		List<ContactEntity> filtered = new ArrayList<>();
		filtered.add(entities.get(1));
		Mockito
		.doReturn(filtered)
		.when(repository).findByRange(any(Date.class), any(Date.class));
		List<Contact> txContacts = service.findByDateRange(startDate, endDate);
		assertEquals(1, txContacts.size());
		txContacts.forEach(item -> contacts.contains(item));
	}

	@Test
	public void testFindByAreaCode() {
		String areaCode = "443";
		List<ContactEntity> filterd = entities.stream()
				.filter(e -> e.getPhoneNumber().startsWith(areaCode))
				.collect(Collectors.toList());
		
		Mockito
		.doReturn(filterd)
		.when(repository).findByAreaCode(eq(areaCode));
		List<Contact> txContacts = service.findByAreaCode(areaCode);
		assertEquals(2, txContacts.size());
		txContacts.forEach(item -> contacts.contains(item));
	}

	@Test
	public void testDelete() {
		Mockito.doReturn(true).when(repository).exists(eq(1L));
		service.delete(1L);
		Mockito.verify(repository).delete(eq(1L));
	}
	
	@Test
	public void testValidate() throws Exception {
		Method method = ContactService.class.getDeclaredMethod("validate", Contact.class);
		method.setAccessible(true);
		
		Contact invalidContact = contacts.get(0);
		
		method.invoke(service, invalidContact);
		
		
		invalidContact.setEmail("abc");
		try {
			method.invoke(service, invalidContact);
		} catch (Exception ex) {
			assertEquals("The email format is not valid.", ex.getCause().getMessage());
		}
		
		invalidContact.setEmail("abc@abc.com");
		invalidContact.setPhoneNumber("1234-23-2345");
		try {
			method.invoke(service, invalidContact);
		} catch (Exception ex) {
			assertEquals("The phone number should be in the format: xxx-xxx-xxxx", ex.getCause().getMessage());
		}
		
		invalidContact.setPhoneNumber("124-323-2345");
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		today.add(Calendar.DATE, 1);
		invalidContact.setLastContactedDate(today.getTime());
		try {
			method.invoke(service, invalidContact);
		} catch (Exception ex) {
			assertEquals("The lastContactedDate should not be a future date.", ex.getCause().getMessage());
		}
	}
	
	@Test
	public void testValidateForUpdate() throws Exception {
		Method method = ContactService.class.getDeclaredMethod("validateForUpdate", Contact.class);
		method.setAccessible(true);
		
		Contact validContact = contacts.get(0);
		Mockito.doReturn(true).when(repository).exists(eq(validContact.getId()));
		method.invoke(service, validContact);
		
		Contact nonExistingContact = contacts.get(1);
		Mockito.doReturn(false).when(repository).exists(eq(nonExistingContact.getId()));
		
		try {
			method.invoke(service, nonExistingContact);
		} catch (Exception ex) {
			assertEquals("Contact with id " + nonExistingContact.getId() + " is not present.", ex.getCause().getMessage());
		}
		
		Contact invalidContact = contacts.get(1);

		invalidContact.setId(null);
		
		try {
			method.invoke(service, invalidContact);
		} catch (Exception ex) {
			assertEquals("Id is a mandatory for update.", ex.getCause().getMessage());
		}
	}
	
	@Test
	public void testMergePatch() throws Exception {
		Method method = ContactService.class.getDeclaredMethod("mergePatch", ContactEntity.class, ContactEntity.class);
		method.setAccessible(true);
		
		ContactEntity existing = new ContactEntity();
		BeanUtils.copyProperties(entities.get(0), existing);;
		ContactEntity newObject = new ContactEntity();
		method.invoke(service, newObject, existing);
		assertEquals(entities.get(0), existing);
		
		existing = new ContactEntity();
		BeanUtils.copyProperties(entities.get(0), existing);
		newObject = entities.get(1);
		newObject.setId(existing.getId());
		method.invoke(service, newObject, existing);
		assertEquals(entities.get(1), existing);
		
	}

}

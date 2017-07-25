package com.spm.addressbook.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spm.addressbook.bean.Contact;
import com.spm.addressbook.dao.bean.ContactEntity;
import com.spm.addressbook.dao.repo.ContactRepository;
import com.spm.addressbook.ex.ContactServiceException;

@Service
public class ContactService {
	
	@Autowired
	private ContactRepository contactRepository;
	
	private static final EmailValidator emailValidator = EmailValidator.getInstance();
	private static final Pattern phonePattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
	
	public Contact create(Contact contact){
		this.validate(contact);
		ContactEntity contactEntity = new ContactEntity(contact);
		contactEntity = contactRepository.save(contactEntity);
		contact = new Contact(contactEntity);
		return contact;
	}
	
	public Contact update(Contact contact){
		this.validateForUpdate(contact);
		ContactEntity contactEntity = new ContactEntity(contact);
		contactEntity = contactRepository.save(contactEntity);
		contact = new Contact(contactEntity);
		return contact;
	}
	
	public Contact patch(Contact contact){
		this.validateForUpdate(contact);
		ContactEntity newEntity = new ContactEntity(contact);
		ContactEntity existingEntity = contactRepository.findOne(contact.getId());
		mergePatch(newEntity, existingEntity);
		newEntity = contactRepository.save(existingEntity);
		contact = new Contact(newEntity);
		return contact;
	}
	
	public Contact findOne(Long id) {
		ContactEntity contactEntity = contactRepository.findOne(id);
		Contact contact = null;
		if (contactEntity != null) {
			contact = new Contact(contactEntity);
		}
		return contact;
	}
	
	public List<Contact> findAll() {
		Iterable<ContactEntity> contactEntities = contactRepository.findAll();
		List<Contact> contacts = StreamSupport.stream(contactEntities.spliterator(), false)
				.map(entity -> new Contact(entity)).collect(Collectors.toList()); 
		return contacts;
	}
	
	public List<Contact> findByState(List<String> states) {
		Iterable<ContactEntity> contactEntities = contactRepository.findByStates(states);
		List<Contact> contacts = new ArrayList<>();
		if (contactEntities != null) {
			contacts = StreamSupport.stream(contactEntities.spliterator(), false)
				.map(entity -> new Contact(entity)).collect(Collectors.toList()); 
		}
		return contacts;
	}
	

	public List<Contact> findByDateRange(String start, String end) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		Date endDate;
		try {
			startDate = format.parse(start);
			endDate = format.parse(end);
		} catch (ParseException e) {
			throw new ContactServiceException(e.getMessage());
		}
		Iterable<ContactEntity> contactEntities = contactRepository.findByRange(startDate, endDate);
		List<Contact> contacts = StreamSupport.stream(contactEntities.spliterator(), false)
				.map(entity -> new Contact(entity)).collect(Collectors.toList()); 
		return contacts;
	}
	
	public List<Contact> findByAreaCode(String areaCode) {
		Iterable<ContactEntity> contactEntities = contactRepository.findByAreaCode(areaCode);
		List<Contact> contacts = StreamSupport.stream(contactEntities.spliterator(), false)
				.map(entity -> new Contact(entity)).collect(Collectors.toList()); 
		return contacts;
	}
	
	public void delete(Long id) {
		if (contactRepository.exists(id)) {
			contactRepository.delete(id);
		} else {
			throw new ContactServiceException("No contact with id " + id + " exists!");
		}
	}
	
	private void validate(Contact contact) {
		Date today = new Date();
		if (contact.getLastContactedDate() != null && contact.getLastContactedDate().compareTo(today) > 0) {
			throw new ContactServiceException("The lastContactedDate should not be a future date.");
		}
		if (contact.getEmail() != null && !emailValidator.isValid(contact.getEmail())) {
			throw new ContactServiceException("The email format is not valid.");
		}
		if (contact.getPhoneNumber() != null) {
			Matcher matcher = phonePattern.matcher(contact.getPhoneNumber()); 
			if (!matcher.matches()) {
				throw new ContactServiceException("The phone number should be in the format: xxx-xxx-xxxx");
			}
		}
	}
	
	private void validateForUpdate(Contact contact) {
		if (contact.getId() == null) {
			throw new ContactServiceException("Id is a mandatory for update.");
		}
		if (!contactRepository.exists(contact.getId())) {
			throw new ContactServiceException("Contact with id " + contact.getId() + " is not present.");
		}
		this.validate(contact);
	}
	
	private void mergePatch(ContactEntity newEntity, ContactEntity existingEntity) {
		if (newEntity.getAddressLine1() != null) {
			existingEntity.setAddressLine1(newEntity.getAddressLine1());
		}
		if (newEntity.getAddressLine2() != null) {
			existingEntity.setAddressLine2(newEntity.getAddressLine2());
		}
		if (newEntity.getCity() != null) {
			existingEntity.setCity(newEntity.getCity());
		}
		if (newEntity.getCountry() != null) {
			existingEntity.setCountry(newEntity.getCountry());
		}
		if (newEntity.getEmail() != null) {
			existingEntity.setEmail(newEntity.getEmail());
		}
		if (newEntity.getPhoneNumber() != null) {
			existingEntity.setPhoneNumber(newEntity.getPhoneNumber());
		}
		if (newEntity.getFirstName() != null) {
			existingEntity.setFirstName(newEntity.getFirstName());
		}
		if (newEntity.getLastContactedDate() != null) {
			existingEntity.setLastContactedDate(newEntity.getLastContactedDate());
		}
		if (newEntity.getLastName() != null) {
			existingEntity.setLastName(newEntity.getLastName());
		}
		if (newEntity.getState() != null) {
			existingEntity.setState(newEntity.getState());
		}
		if (newEntity.getZip() != null) {
			existingEntity.setZip(newEntity.getZip());
		}
	}

}

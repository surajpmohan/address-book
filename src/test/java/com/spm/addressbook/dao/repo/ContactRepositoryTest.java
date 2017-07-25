package com.spm.addressbook.dao.repo;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spm.addressbook.bean.Contact;
import com.spm.addressbook.dao.bean.ContactEntity;


@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class ContactRepositoryTest {
	
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;
    
    static List<ContactEntity> entities;
    
	private static ObjectMapper mapper = new ObjectMapper();
    
	@Before
	public void setUp() throws Exception {
		List<Contact> contacts = mapper.readValue(this.getClass().getResourceAsStream("/contacts.json"), 
				new TypeReference<List<Contact>>(){});
		contacts.get(0).setLastContactedDate(new Date());
		
		entities = contacts.stream().map(c -> new ContactEntity(c)).collect(Collectors.toList());
		
		contactRepository.save(entities);
	}
	
	@Test
	public void testFindByStates() {
		List<String> states = new ArrayList<>();
		states.add("ABC");
		List<ContactEntity> contacts = (List<ContactEntity>)contactRepository.findByStates(states);
		assertEquals(0, contacts.size());
		
		states.clear();
		states.add("TX");
		contacts = (List<ContactEntity>)contactRepository.findByStates(states);
		assertEquals(1, contacts.size());
		assertEquals(entities.get(2), contacts.get(0));
		
		states.clear();
		states.add("TX");
		states.add("CA");
		contacts = (List<ContactEntity>)contactRepository.findByStates(states);
		assertEquals(3, contacts.size());
		contacts.forEach( c -> assertTrue(entities.contains(c)));
	}

	@Test
	public void testFindByAreaCode() {
		List<ContactEntity> contacts = (List<ContactEntity>)contactRepository.findByAreaCode("324");
		assertEquals(1, contacts.size());
		assertEquals(entities.get(2), contacts.get(0));

		contacts = (List<ContactEntity>)contactRepository.findByAreaCode("443");
		assertEquals(2, contacts.size());
		contacts.forEach( c -> assertTrue(entities.contains(c)));	
	}

	@Test
	public void testFindByRange() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate = formatter.parse("07/04/2017");
		Date endDate = new Date();

		List<ContactEntity> contacts = (List<ContactEntity>)contactRepository.findByRange(startDate, endDate);
		assertEquals(1, contacts.size());
		assertEquals(entities.get(0), contacts.get(0));
		
		startDate = formatter.parse("07/03/2016");
		endDate = new Date();
		contacts = (List<ContactEntity>)contactRepository.findByRange(startDate, endDate);
		assertEquals(3, contacts.size());
		contacts.forEach( c -> assertTrue(entities.contains(c)));	
	}

}

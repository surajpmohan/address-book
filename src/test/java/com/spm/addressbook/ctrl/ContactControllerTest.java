package com.spm.addressbook.ctrl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spm.addressbook.bean.Contact;
import com.spm.addressbook.bo.ContactService;

@RunWith(SpringRunner.class)
@WebMvcTest(ContactController.class)
@AutoConfigureRestDocs(outputDir = "target")
public class ContactControllerTest {
	
	@Autowired
    private MockMvc mvc;
 
    @MockBean
    private ContactService service;
	
	private static List<Contact> contacts;
	private static ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		
		contacts = mapper.readValue(this.getClass().getResourceAsStream("/contacts.json"), 
				new TypeReference<List<Contact>>(){});
		//contacts.get(0).setLastContactedDate(new Date());
	}

	@Test
	public void testPost() throws Exception {
		given(service.create(contacts.get(1))).willReturn(contacts.get(1));
		MvcResult result = mvc.perform(post("/contact")
			    .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(contacts.get(1))))
		 		.andExpect(status().isOk()).andDo(document("rest/post") ).andReturn();
		Contact contact = mapper.readValue(result.getResponse().getContentAsString(), Contact.class);
		assertEquals(contacts.get(1), contact);
	}

	@Test
	public void testPut() throws Exception {
		given(service.update(contacts.get(1))).willReturn(contacts.get(1));
		MvcResult result = mvc.perform(put("/contact")
			    .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(contacts.get(1))))
		 		.andExpect(status().isOk()).andDo(document("rest/put") ).andReturn();
		Contact contact = mapper.readValue(result.getResponse().getContentAsString(), Contact.class);
		assertEquals(contacts.get(1), contact);
	}

	@Test
	public void testPatch() throws Exception {
		Contact patch = new Contact();
		patch.setFirstName("updated name");
		patch.setId(contacts.get(1).getId());
		contacts.get(1).setFirstName(patch.getFirstName());
		given(service.patch(patch)).willReturn(contacts.get(1));
		MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/contact")
			    .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(patch)))
		 		.andExpect(status().isOk()).andDo(document("rest/patch") ).andReturn();
		Contact contact = mapper.readValue(result.getResponse().getContentAsString(), Contact.class);
		assertEquals(contacts.get(1), contact);
		assertEquals("updated name", contact.getFirstName());
	}

	@Test
	public void testGet() throws Exception {
		given(service.findAll()).willReturn(contacts);
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/contact")
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andDo(document("rest/getall") ).andReturn();
		List<Contact> actualContacts = mapper.readValue(result.getResponse().getContentAsString(), 
				new TypeReference<List<Contact>>() {});
		assertEquals(contacts, actualContacts);
	}

	@Test
	public void testQuery() throws Exception {
		List<String> states = new ArrayList<>();
		states.add("TX");
		List<Contact> filtered = contacts.stream()
				.filter(e -> "TX".equals(e.getState()))
				.collect(Collectors.toList());
		given(service.findByState(states)).willReturn(filtered);
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/contact/filter?states=TX")
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andDo(document("rest/getbystates") ).andReturn();
		List<Contact> actualResults = mapper.readValue(result.getResponse().getContentAsString(), 
				new TypeReference<List<Contact>>() {});
		assertEquals(filtered, actualResults);
		
		
		filtered = new ArrayList<>();
		filtered.add(contacts.get(1));
		given(service.findByDateRange("04/04/2017", "06/04/2017")).willReturn(filtered);
		result = mvc.perform(MockMvcRequestBuilders.get("/contact/filter?startDate=04/04/2017&endDate=06/04/2017")
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andDo(document("rest/getbyrange") ).andReturn();
		actualResults = mapper.readValue(result.getResponse().getContentAsString(), 
				new TypeReference<List<Contact>>() {});
		assertEquals(filtered, actualResults);
		
		
		String areaCode = "443";
		filtered = contacts.stream()
				.filter(e -> e.getPhoneNumber().startsWith(areaCode))
				.collect(Collectors.toList());
		given(service.findByAreaCode(areaCode)).willReturn(filtered);
		result = mvc.perform(MockMvcRequestBuilders.get("/contact/filter?areaCode="+areaCode)
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andDo(document("rest/getbyareacode") ).andReturn();
		actualResults = mapper.readValue(result.getResponse().getContentAsString(), 
				new TypeReference<List<Contact>>() {});
		assertEquals(filtered, actualResults);
		
		
		result = mvc.perform(MockMvcRequestBuilders.get("/contact/filter?")
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andReturn();
		
		assertEquals(0, result.getResponse().getContentAsString().length());
	}

	@Test
	public void testGetOne() throws Exception {
		Contact contact = contacts.get(1);
		given(service.findOne(contact.getId())).willReturn(contact);
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/contact/"+contact.getId())
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andDo(document("rest/getone") ).andReturn();
		Contact contactItem = mapper.readValue(result.getResponse().getContentAsString(), Contact.class);
		assertEquals(contact, contactItem);

	}

	@Test
	public void testDelete() throws Exception {
		Contact contact = contacts.get(1);
		mvc.perform(MockMvcRequestBuilders.delete("/contact/"+contact.getId())
			    .contentType(MediaType.APPLICATION_JSON))
		 		.andExpect(status().isOk()).andDo(document("rest/delete") );
		verify(service).delete(contact.getId());
	}

}

package com.spm.addressbook.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.spm.addressbook.bean.Contact;
import com.spm.addressbook.bo.ContactService;

@RestController
public class ContactController {
	
	@Autowired
	private ContactService contactService;
	
	
	@RequestMapping(path="/contact", method=RequestMethod.POST)
	public @ResponseBody Contact post(@RequestBody Contact contact){
		return contactService.create(contact);
	}
	
	@RequestMapping(path="/contact", method=RequestMethod.PUT)
	public @ResponseBody Contact put(@RequestBody Contact contact){
		return contactService.update(contact);
	}
	
	@RequestMapping(path="/contact", method=RequestMethod.PATCH)
	public @ResponseBody Contact patch(@RequestBody Contact contact){
		return contactService.patch(contact);
	}
	
	@RequestMapping(path="contact", method=RequestMethod.GET)
	public @ResponseBody List<Contact> get(){
		return contactService.findAll();
	}
	
	@RequestMapping(path="contact/filter", method=RequestMethod.GET)
	public @ResponseBody List<Contact> query(
			@RequestParam(value = "states", required = false) List<String> states, 
			@RequestParam(value = "areaCode", required = false) String areaCode, 
			@RequestParam(value = "startDate", required = false) String  startDate, 
			@RequestParam(value = "endDate", required = false) String endDate){
		
		List<Contact> contacts = null;
		
		if (!CollectionUtils.isEmpty(states)) {
			contacts = contactService.findByState(states);
		} else if (!StringUtils.isEmpty(areaCode)) {
			contacts = contactService.findByAreaCode(areaCode);
		} else if (!StringUtils.isEmpty(startDate) || !StringUtils.isEmpty(endDate)) {
			contacts = contactService.findByDateRange(startDate, endDate);
		} 
		return contacts;
	}
	
	@RequestMapping(path="contact/{id}", method=RequestMethod.GET)
	public @ResponseBody Contact getOne(@PathVariable("id") Long id){
		return contactService.findOne(id);
	}
	
	@RequestMapping(path="contact/{id}", method=RequestMethod.DELETE)
	public void delete(@PathVariable Long id){
		contactService.delete(id);
	}
}

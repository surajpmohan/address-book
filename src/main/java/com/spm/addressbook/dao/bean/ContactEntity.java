package com.spm.addressbook.dao.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.spm.addressbook.bean.Contact;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContactEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String addressLine1;
	private String addressLine2;
	private String zip;
	private String city;
	private String state;
	private String country;
	private Date lastContactedDate;
	
	public ContactEntity(Contact bean){
		id = bean.getId();
		firstName = bean.getFirstName();
		lastName = bean.getLastName();
		email = bean.getEmail();
		phoneNumber = bean.getPhoneNumber();
		addressLine1 = bean.getAddressLine1();
		addressLine2 = bean.getAddressLine2();
		zip = bean.getZip();
		city = bean.getCity();
		state = bean.getState();
		country = bean.getCountry();
		lastContactedDate = bean.getLastContactedDate();
	}
}

package com.spm.addressbook.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spm.addressbook.dao.bean.ContactEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Contact {
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

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	private Date lastContactedDate;
	
	public Contact(ContactEntity entity){
		id = entity.getId();
		firstName = entity.getFirstName();
		lastName = entity.getLastName();
		email = entity.getEmail();
		phoneNumber = entity.getPhoneNumber();
		addressLine1 = entity.getAddressLine1();
		addressLine2 = entity.getAddressLine2();
		zip = entity.getZip();
		city = entity.getCity();
		state = entity.getState();
		country = entity.getCountry();
		lastContactedDate = entity.getLastContactedDate();
	}
}

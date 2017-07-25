package com.spm.addressbook.dao.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.spm.addressbook.dao.bean.ContactEntity;

public interface ContactRepository extends CrudRepository<ContactEntity, Long> {
	@Query("SELECT c FROM ContactEntity c where c.state in :states") 
	Iterable<ContactEntity> findByStates(@Param("states") List<String> states);
	
	@Query("SELECT c FROM ContactEntity c where c.phoneNumber like :areaCode%")
	Iterable<ContactEntity> findByAreaCode(@Param("areaCode") String areaCode);
	
	@Query("SELECT c FROM ContactEntity c where c.lastContactedDate BETWEEN :startDate AND :endDate") 
	Iterable<ContactEntity> findByRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

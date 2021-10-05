package com.ksteindl.contacts.domain.repository;

import com.ksteindl.contacts.domain.entities.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

}

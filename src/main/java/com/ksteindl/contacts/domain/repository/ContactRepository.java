package com.ksteindl.contacts.domain.repository;

import com.ksteindl.contacts.domain.Status;
import com.ksteindl.contacts.domain.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

    Page<Contact> findContactsByStatus(Status status, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.status = 'ACTIVE' and (" +
            "c.firstName like %?1% or " +
            "c.lastName like %?1% or " +
            "c.email like %?1% or " +
            "c.phone like %?1% or " +
            "c.company.name like %?1%)")
    Page<Contact> findContactsByQueryString(String queryString, Pageable paging);
}

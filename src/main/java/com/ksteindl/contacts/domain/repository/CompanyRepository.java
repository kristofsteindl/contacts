package com.ksteindl.contacts.domain.repository;

import com.ksteindl.contacts.domain.entities.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Optional<Company> findByName(String name);
}

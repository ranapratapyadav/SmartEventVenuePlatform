package com.smartevent.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartevent.customer.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}

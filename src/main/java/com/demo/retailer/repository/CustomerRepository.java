package com.demo.retailer.repository;

import org.springframework.data.repository.CrudRepository;

import com.demo.retailer.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	public Customer findByCustomerId(Long customerId);
}

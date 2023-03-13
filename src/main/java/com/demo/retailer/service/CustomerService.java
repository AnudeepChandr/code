package com.demo.retailer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.retailer.entity.Customer;
import com.demo.retailer.repository.CustomerRepository;

@Component
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	public Customer findCustomer(Long customerId) {
		Customer customer = customerRepository.findByCustomerId(customerId);
		if (customer == null) {
			throw new RuntimeException("Invalid customer Id ");
		}
		return customer;
	}
}

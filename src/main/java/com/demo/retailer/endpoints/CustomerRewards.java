package com.demo.retailer.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.demo.retailer.TO.CustomerRewardsTO;
import com.demo.retailer.entity.Customer;
import com.demo.retailer.service.CustomerService;
import com.demo.retailer.service.RewardService;

@RestController()
public class CustomerRewards {

	@Autowired
	CustomerService customerService;

	@Autowired
	RewardService rewardService;

	@GetMapping("/customer-rewards/{customerId}")
	public ResponseEntity<CustomerRewardsTO> getCustomerRewards(@PathVariable("customerId") Long customerId) {
		try {
			Customer customer = customerService.findCustomer(customerId);
			CustomerRewardsTO customerRewards = rewardService.getRewardsByCustomerId(customer.getCustomerId());
			return new ResponseEntity<CustomerRewardsTO>(customerRewards, HttpStatus.OK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

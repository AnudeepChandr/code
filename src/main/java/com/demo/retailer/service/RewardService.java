package com.demo.retailer.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.retailer.Constants.Constants;
import com.demo.retailer.TO.CustomerRewardsTO;
import com.demo.retailer.entity.Transaction;
import com.demo.retailer.repository.TransactionRepository;

@Service
public class RewardService {

	@Autowired
	TransactionRepository transactionRepository;

	public CustomerRewardsTO getRewardsByCustomerId(Long customerId) {

		Timestamp threeMonthsDurationTimestamp = getDateBasedOnOffSetDays(3 * Constants.daysInMonths);

		List<Transaction> threeMonthTransactions = transactionRepository.findAllByCustomerIdAndTransactionDateBetween(
				customerId, threeMonthsDurationTimestamp, Timestamp.from(Instant.now()));
		threeMonthTransactions.removeAll(Collections.singleton(null));

		Map<YearMonth, List<Transaction>> monthlyRewardsMap = getRewardsPerMonth(threeMonthTransactions);
		Set<YearMonth> yearMonth = monthlyRewardsMap.keySet();
		List<YearMonth> yearMonthList = sortYearMonth(yearMonth);

		long lastMonthRewards = monthlyRewardsMap.get(yearMonthList.get(2)).stream()
				.map(transaction -> {
					return calculateRewards(transaction);})
				.collect(Collectors.summingLong(rewards -> rewards.longValue()));

		long secondLastMonthRewards = monthlyRewardsMap.get(yearMonthList.get(1)).stream()
				.map(transaction -> calculateRewards(transaction))
				.collect(Collectors.summingLong(rewards -> rewards.longValue()));

		long thirdLastMonthRewards = monthlyRewardsMap.get(yearMonthList.get(0)).stream()
				.map(transaction -> calculateRewards(transaction))
				.collect(Collectors.summingLong(rewards -> rewards.longValue()));
		
		CustomerRewardsTO customerRewards = new CustomerRewardsTO();
		customerRewards.setCustomerId(customerId);
		customerRewards.setLastMonthRewardPoints(lastMonthRewards);
		customerRewards.setLastSecondMonthRewardPoints(secondLastMonthRewards);
		customerRewards.setLastThirdMonthRewardPoints(thirdLastMonthRewards);
		customerRewards.setTotalRewards(lastMonthRewards + secondLastMonthRewards + thirdLastMonthRewards);

		return customerRewards;

	}

	private Map<YearMonth, List<Transaction>> getRewardsPerMonth(List<Transaction> transactions) {
		Map<YearMonth, List<Transaction>> monthlyRewardsMap = transactions.stream().collect(Collectors.groupingBy(t -> {
			return YearMonth.of(t.getTransactionDate().getYear() + 1900, t.getTransactionDate().getMonth() + 1);
		}));
		return monthlyRewardsMap;
	}

	private Long calculateRewards(Transaction t) {
		if (t.getPrice() > Constants.firstRewardLimit && t.getPrice() <= Constants.secondRewardLimit) {
			return Math.round(t.getPrice() - Constants.firstRewardLimit);
		} else if (t.getPrice() > Constants.secondRewardLimit) {
			return Math.round(t.getPrice() - Constants.secondRewardLimit) * 1
					+ (Constants.secondRewardLimit - Constants.firstRewardLimit);
		} else
			return (long) 0;

	}

	public Timestamp timestamp(int days) {
		return Timestamp.valueOf(LocalDateTime.now().minusDays(days));
	}

	public Timestamp getDateBasedOnOffSetDays(int days) {
		return Timestamp.valueOf(LocalDateTime.now().minusDays(days));
	}

	private List<YearMonth> sortYearMonth(Set<YearMonth> yearMonths) {
		List<YearMonth> yearMonthList = yearMonths.parallelStream().sorted().collect(Collectors.toList());
		return yearMonthList;
	}
}

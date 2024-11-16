package com.shashi.service;

import com.shashi.beans.TransactionBean;

public interface TransService {

	public String getUserId(String transId);

	public TransactionBean applyDiscount(TransactionBean transaction, String discountCode);

	public boolean isValidDiscountCode(String discountCode);

	public double calculateFinalAmount(double initialAmount, String discountCode);
}

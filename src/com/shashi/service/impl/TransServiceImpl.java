package com.shashi.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.shashi.service.TransService;
import com.shashi.beans.TransactionBean;
import com.shashi.utility.DBUtil;

public class TransServiceImpl implements TransService {

	@Override
	public String getUserId(String transId) {
		String userId = "";

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select username from transactions where transid=?");

			ps.setString(1, transId);

			rs = ps.executeQuery();

			if (rs.next())
				userId = rs.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userId;
	}

	@Override
	public boolean isValidDiscountCode(String discountCode) {
		boolean isValid = false;

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT 1 FROM discount_codes WHERE code = ?");
			ps.setString(1, discountCode);
			rs = ps.executeQuery();

			if (rs.next())
				isValid = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return isValid;
	}

	@Override
	public double calculateFinalAmount(double initialAmount, String discountCode) {
		double finalAmount = initialAmount;

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT discount_value FROM discount_codes WHERE code = ?");
			ps.setString(1, discountCode);
			rs = ps.executeQuery();

			if (rs.next()) {
				double discountValue = rs.getDouble("discount_value");
				finalAmount = initialAmount - (initialAmount * (discountValue / 100));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return finalAmount;
	}

	@Override
	public TransactionBean applyDiscount(TransactionBean transaction, String discountCode) {
		if (!isValidDiscountCode(discountCode)) {
			throw new IllegalArgumentException("Invalid discount code.");
		}

		double initialAmount = transaction.getTransAmount();
		double discountAmount = 0.0;
		double finalAmount = initialAmount;

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT discount_value FROM discount_codes WHERE code = ?");
			ps.setString(1, discountCode);
			rs = ps.executeQuery();

			if (rs.next()) {
				double discountValue = rs.getDouble("discount_value");
				discountAmount = initialAmount * (discountValue / 100);
				finalAmount = initialAmount - discountAmount;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Update the transaction bean
		transaction.setDiscountCode(discountCode);
		transaction.setDiscountAmount(discountAmount);
		transaction.setFinalAmount(finalAmount);

		return transaction;
	}
}


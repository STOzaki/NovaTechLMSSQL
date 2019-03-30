package com.lms.model;

import java.util.Objects;
public class Borrower {
	private final int cardNo;
	private String name;
	private String address;
	private String phone;
	
	public Borrower(int cardNo, String name, String address, String phone) {
		this.cardNo = cardNo;
		this.name = name;
		this.address = address;
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getCardNo() {
		return cardNo;
	}

	@Override
	public int hashCode() {
		return cardNo;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Borrower) {
			return cardNo == ((Borrower) obj).getCardNo()
					&& Objects.equals(name, ((Borrower) obj).getName())
					&& Objects.equals(address, ((Borrower) obj).getAddress())
					&& Objects.equals(phone, ((Borrower) obj).getPhone());
		} else {
			return false;
		}
	}
}

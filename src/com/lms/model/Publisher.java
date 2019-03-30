package com.lms.model;

import java.util.Objects;

public final class Publisher {
	private final int id;
	private String name;
	private String address;
	private String phone;

	public Publisher(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Publisher(int id, String name, String address, String phone) {
		this.id = id;
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

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Publisher) {
			return id == ((Publisher) obj).getId()
					&& Objects.equals(name, ((Publisher) obj).getName())
					&& Objects.equals(address, ((Publisher) obj).getAddress())
					&& Objects.equals(phone, ((Publisher) obj).getPhone());
		} else {
			return false;
		}
	}
}

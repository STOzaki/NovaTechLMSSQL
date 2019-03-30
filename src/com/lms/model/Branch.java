package com.lms.model;

import java.util.Objects;

public class Branch {
	private final int id;
	private String name;
	private String address;
	
	public Branch(int id, String name, String address) {
		this.id = id;
		this.name = name;
		this.address = address;
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
		} else if (obj instanceof Branch) {
			return id == ((Branch) obj).getId()
					&& Objects.equals(name, ((Branch) obj).getName())
					&& Objects.equals(address, ((Branch) obj).getAddress());
		} else {
			return false;
		}
	}
}

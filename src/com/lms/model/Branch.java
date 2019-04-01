package com.lms.model;

<<<<<<< HEAD
import java.util.Objects;

=======
>>>>>>> models added
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
<<<<<<< HEAD
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
=======
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Branch other = (Branch) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
>>>>>>> models added
	}

	@Override
	public String toString() {
		return "Branch: " + name + "(" + id + ") at " + address;
	}
}

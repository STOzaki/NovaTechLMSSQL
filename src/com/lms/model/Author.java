package com.lms.model;

<<<<<<< HEAD
import java.util.Objects;

=======
>>>>>>> models added
public final class Author {
	private final int id;
	private String name;

	public Author(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		} else if (obj instanceof Author) {
			return id == ((Author) obj).getId()
					&& Objects.equals(name, ((Author) obj).getName());
		} else {
			return false;
		}
=======
		final int prime = 31;
		int result = 1;
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
		Author other = (Author) obj;
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
}

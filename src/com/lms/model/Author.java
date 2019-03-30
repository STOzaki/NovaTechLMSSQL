package com.lms.model;

import java.util.Objects;

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
	}
}

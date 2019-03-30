package com.lms.model;

<<<<<<< HEAD
import java.util.Objects;

=======
>>>>>>> models added
public class Book {
	private final int id;
	private String title;
	private Author author;
	private Publisher publisher;

	public Book(int id, String title, Author author, Publisher publisher) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
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
		} else if (obj instanceof Book) {
			return id == ((Book) obj).getId()
					&& Objects.equals(title, ((Book) obj).getTitle())
					&& Objects.equals(author, ((Book) obj).getAuthor())
					&& Objects.equals(publisher, ((Book) obj).getPublisher());
		} else {
			return false;
		}
=======
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + id;
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Book other = (Book) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (id != other.id)
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
>>>>>>> models added
	}
}

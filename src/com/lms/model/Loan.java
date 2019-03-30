package com.lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.Objects;

public class Loan {
	private final Book book;
	private final Borrower borrower;
	private final Branch branch;
=======

public class Loan {
	private Book book;
	private Borrower borrower;
	private Branch branch;
>>>>>>> models added
	private LocalDateTime dateOut;
	private LocalDate dueDate;

	public Loan(Book book, Borrower borrower, Branch branch, LocalDateTime dateOut, LocalDate dueDate) {
		this.book = book;
		this.borrower = borrower;
		this.branch = branch;
		this.dateOut = dateOut;
		this.dueDate = dueDate;
	}

	public Book getBook() {
		return book;
	}

<<<<<<< HEAD
=======
	public void setBook(Book book) {
		this.book = book;
	}

>>>>>>> models added
	public Borrower getBorrower() {
		return borrower;
	}

<<<<<<< HEAD
=======
	public void setBorrower(Borrower borrower) {
		this.borrower = borrower;
	}

>>>>>>> models added
	public Branch getBranch() {
		return branch;
	}

<<<<<<< HEAD
=======
	public void setBranch(Branch branch) {
		this.branch = branch;
	}

>>>>>>> models added
	public LocalDateTime getDateOut() {
		return dateOut;
	}

	public void setDateOut(LocalDateTime dateOut) {
		this.dateOut = dateOut;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public int hashCode() {
<<<<<<< HEAD
		return Objects.hash(book, borrower, branch);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Loan) {
			return Objects.equals(book, ((Loan) obj).getBook())
					&& Objects.equals(borrower, ((Loan) obj).getBorrower())
					&& Objects.equals(branch, ((Loan) obj).getBranch())
					&& Objects.equals(dateOut, ((Loan) obj).getDateOut())
					&& Objects.equals(dueDate, ((Loan) obj).getDueDate());
		} else {
			return false;
		}
=======
		final int prime = 31;
		int result = 1;
		result = prime * result + ((book == null) ? 0 : book.hashCode());
		result = prime * result + ((borrower == null) ? 0 : borrower.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((dateOut == null) ? 0 : dateOut.hashCode());
		result = prime * result + ((dueDate == null) ? 0 : dueDate.hashCode());
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
		Loan other = (Loan) obj;
		if (book == null) {
			if (other.book != null)
				return false;
		} else if (!book.equals(other.book))
			return false;
		if (borrower == null) {
			if (other.borrower != null)
				return false;
		} else if (!borrower.equals(other.borrower))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (dateOut == null) {
			if (other.dateOut != null)
				return false;
		} else if (!dateOut.equals(other.dateOut))
			return false;
		if (dueDate == null) {
			if (other.dueDate != null)
				return false;
		} else if (!dueDate.equals(other.dueDate))
			return false;
		return true;
>>>>>>> models added
	}
}

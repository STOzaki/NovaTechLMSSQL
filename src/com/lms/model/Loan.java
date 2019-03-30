package com.lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Loan {
	private final Book book;
	private final Borrower borrower;
	private final Branch branch;
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

	public Borrower getBorrower() {
		return borrower;
	}

	public Branch getBranch() {
		return branch;
	}

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
	}
}

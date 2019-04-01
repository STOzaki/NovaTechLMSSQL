package com.lms.dao.test;

import java.io.BufferedReader;
import java.sql.Connection;

import com.lms.dao.BookDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.model.Book;
import com.lms.model.Branch;

public class CopiesDaoTest {
	private static Connection conn = null;
	private static BufferedReader br;
	private static String table = "tbl_book_loans";
	private static String tableBookId = "bookId";

	private String title = "The Book Title";
	
	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private static BookDaoImpl bookDaoImpl;
	private static LibraryBranchDaoImpl branchDaoImpl;
	
	private Book testBook;
	private Branch testBranch;
}

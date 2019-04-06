package com.lms.menu.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.RetrieveException;
import com.lms.menu.MainMenu;
import com.lms.model.Publisher;
import com.lms.service.AdministratorServiceImpl;

public class MainMenuTest {

	private static AdministratorServiceImpl adminService;
	
	private final static String env = "test";
	
	private String publisherName = "Tester 1 Test";
	private String publisherAddress = "410 Terry Ave. North, Seattle, WA, 98109-5210";
	private String publisherPhone = "(206) 266-1000";
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException, CriticalSQLException {
		
		adminService = new AdministratorServiceImpl(env);
	}
	
	@BeforeEach
	public void init() {
		
	}
	
	@Test
	public void testingMenu() throws CriticalSQLException, RetrieveException, InsertException, DeleteException {
		Publisher lastPublisher = adminService.createPublisher(publisherName);
		int newIdIncrement = lastPublisher.getId() + 1;
		adminService.deletePublisher(lastPublisher);

		StringReader userInput = new StringReader("a\n1\n3\n" + publisherName + "\n" + publisherAddress +
				"\n" + publisherPhone + "\n6\n5\nq");
		MainMenu main = new MainMenu(userInput, System.out, env);
		main.start();

		List<Publisher> newPublishers = adminService.getAllPublishers().parallelStream()
				.filter(p -> p.getId() == newIdIncrement).collect(Collectors.toList());
		Publisher newRecord = newPublishers.get(0);

		assertEquals(newIdIncrement, newRecord.getId());
		assertEquals(publisherName, newRecord.getName());
		assertEquals(publisherAddress, newRecord.getAddress());
		assertEquals(publisherPhone, newRecord.getPhone());
		
		adminService.deletePublisher(newRecord);
	}
}

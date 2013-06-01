package com.guerrillamail.www;
/**
 * 
 */


import static org.junit.Assert.*;

import java.util.ArrayList;

import com.guerrillamail.www.GuerrillaMail;

/**
 * Test class for email address.
 * @author <a reef="https://github.com/DavidePastore">DavidePastore</a>
 *
 */
public class GuerrillaMailTest {
	
	private GuerrillaMail tester;
	private ArrayList<EMail> emails;
	
	/**
	 * Setup the test.
	 * @throws Exception 
	 */
	@org.junit.Before
	public void testSetup() throws Exception{
		tester = new GuerrillaMail();
		emails = new ArrayList<EMail>();
	}

	/**
	 * Test for email address.
	 */
	@org.junit.Test
	public void testEmailAddress() {
		assertNotNull("Email address must not be null", tester.getEmailAddress());
		try{
			tester.setEmailUser("pingas");
			assertNotNull("Email address must not be null after changing the user name", tester.getEmailAddress());
			
			assertNotNull("Email list must not be null after reading messages", tester.getEmailList());
			
			assertNotNull("Email list must not be null after checking messages",  tester.checkEmail());
			
			assertNotNull("Email must not be null after fetching message",  tester.fetchEmail(1));

		} catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}
	}

}

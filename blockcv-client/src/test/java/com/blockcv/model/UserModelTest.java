package com.blockcv.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.hyperledger.fabric_ca.sdk.Attribute;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.DataAccessStub;

public class UserModelTest {
	
	private UserModel model;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		model = new UserModel();
	}
	
	@Test
	public void testUserModel() {
		model = new UserModel("0", "email", "password", UserType.WORKER, new HashMap<>(), null);
		assertEquals("email", model.getEmail());
	}

	@Test
	public void testGetUserType() {
		model = new UserModel("0", "email", "password", UserType.WORKER, new HashMap<>(), null);
		assertEquals(UserType.WORKER, model.getUserType());
	}

	@Test
	public void testTryLogin() {
		assertTrue(UserModel.tryLogin("worker", "password"));
	}

	@Test
	public void testTrySignup() {
		assertTrue(UserModel.trySignup("worker", "password", new HashMap<>(), UserType.WORKER));
	}

	@Test
	public void testReset() {
		model = new UserModel("0", "email", "password", UserType.WORKER, new HashMap<>(), null);
		model.reset();
		assertEquals("-", model.getEmail());
	}

	@Test
	public void testSetData() {
		model = new UserModel("0", "email", "password", UserType.WORKER, new HashMap<>(), null);
		assertEquals("email", model.getEmail());
	}
	
	@Test
	public void testToString() {
		model = new UserModel("0", "email", "password", UserType.WORKER, new HashMap<>(), null);
		assertEquals("ID:0 Email:emailUserType: WORKER logged:true", model.toString());
	}
	
}

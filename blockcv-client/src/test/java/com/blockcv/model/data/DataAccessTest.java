package com.blockcv.model.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric_ca.sdk.Attribute;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blockcv.model.UserModel;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.blockchain.HFBasicSDK;

public class DataAccessTest {
	
	private static final String emailTest = "userTest6";
	private static final String passwordTest = "userTest";
	private static final Map<String, String> personalInfoTest = new HashMap<String, String>();
	private static final UserType userTypeTest = UserType.WORKER;
	
	/*@BeforeClass
	public static void setUp() {
		try {
			HFBasicSDK hfsdk = new HFBasicSDK();
			ArrayList<Attribute> attrs = new ArrayList<Attribute>();
			attrs.add(new Attribute("user_id", UserModel.generateUserId(emailTest)));
			attrs.add(new Attribute("user_type", userTypeTest.toString()));
			hfsdk.registerUser(emailTest, passwordTest, attrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTryLogin() {
		assertTrue(DataAccess.getInstance().tryLogin(emailTest, passwordTest).isLoggedIn());
	}
	
	@Test
	public void testTrySignup() {
		assertTrue(DataAccess.getInstance().trySignup(emailTest+"new", passwordTest, personalInfoTest, userTypeTest));
	}*/
}

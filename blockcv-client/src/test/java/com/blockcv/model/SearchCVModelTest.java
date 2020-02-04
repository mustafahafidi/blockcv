package com.blockcv.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.SearchCVModel.Curriculum;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.DataAccessStub;

public class SearchCVModelTest {

	private SearchCVModel model;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		model = new SearchCVModel();
	}
	
	@Test
	public void testGetFilteredCVs() {
		UserModel userModel = new UserModel();
		assertNotNull(model.getFilteredCVs(userModel, new HashMap<>()));
	}

	@Test
	public void testGetCV() {
		Curriculum cv = new Curriculum(true, null, null, null);
		Map<String,Curriculum> cvMap = new HashMap<>();
		cvMap.put("cv001", cv);
		model.setCvs(cvMap);
		assertEquals(cv, model.getCV("cv001"));		
	}

	@Test
	public void testCertifyCV() {
		Curriculum cv = new Curriculum(true, null, null, null);
		Map<String,Curriculum> cvMap = new HashMap<>();
		cvMap.put("cv001", cv);
		model.setCvs(cvMap);
		assertTrue(model.certifyCV("cv001", ""));
		assertFalse(cv.isCertifiable());
	}
	
}

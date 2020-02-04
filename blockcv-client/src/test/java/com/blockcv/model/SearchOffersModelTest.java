package com.blockcv.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.SearchOffersModel.WorkOffer;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.DataAccessStub;

public class SearchOffersModelTest {

	private SearchOffersModel model;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		model = new SearchOffersModel();
	}

	@Test
	public void testGetFilteredOffers() {
		Map<String, WorkOffer> offers = model.getFilteredOffers(new UserModel(),new HashMap<>());
		assertEquals(model.getOffers(), offers);
	}

	@Test
	public void testCandidateToOffer() {
		Map<String, WorkOffer> offers = model.getFilteredOffers(new UserModel(), new HashMap<>());
		offers.put("off001", new WorkOffer("","", "", "", "", "", "", "", "", "", "","", false));
		assertTrue(model.candidateToOffer(new UserModel(), "off001"));
		assertTrue(model.getOffers().get("off001").alreadyCandidated());
	}

}

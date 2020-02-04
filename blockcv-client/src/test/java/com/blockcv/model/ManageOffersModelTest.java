package com.blockcv.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.ManageOffersModel.Offer;
import com.blockcv.model.ManageOffersModel.Offer.Candidate;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.DataAccessStub;

public class ManageOffersModelTest {

	private ManageOffersModel model;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		model = new ManageOffersModel();
	}
	
	@Test
	public void testInitialize() {
		Map<String,Offer> offers = model.getOffers();
		model.initialize(new UserModel());
		assertNotEquals(offers, model.getOffers());
	}

	@Test
	public void testGetCandidates() {
		model.initialize(new UserModel());
		List<Candidate> candidates = new ArrayList<>();
		String offID = model.addNewOffer(new Offer("","","","", 2, "", "", "", "", "", "", "", "", candidates));
		assertEquals(candidates, model.getCandidates(offID));
	}

	@Test
	public void testAddNewOffer() {
		model.initialize(new UserModel());
		assertEquals(0, model.getOffers().size());
		model.addNewOffer(new Offer("","","","", 1, "", "", "", "", "", "", "", "", new ArrayList<>()));
		assertEquals(1, model.getOffers().size());
	}

	@Test
	public void testRemoveOffer() {
		model.initialize(new UserModel());
		String offerID = model.addNewOffer(new Offer("","","","", 1, "", "", "", "", "", "", "", "", new ArrayList<>()));
		model.removeOffer(offerID);
		assertEquals(0, model.getOffers().size());
	}

	@Test
	public void testSaveOffers() {
		assertNull(model.getOffers());
		assertTrue(model.saveOffers(new UserModel()));
		assertNotNull(model.getOffers());
	}

}

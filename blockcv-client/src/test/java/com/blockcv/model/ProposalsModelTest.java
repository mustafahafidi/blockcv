package com.blockcv.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.model.ProposalsModel.Proposal;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.DataAccessStub;

public class ProposalsModelTest {

	private ProposalsModel model;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		model = new ProposalsModel();
	}
	
	@Test
	public void testInitialize() {
		model.initialize(new UserModel());
		assertNotNull(model.getProposals());
	}

	@Test
	public void testAcceptProposal() {
		model.initialize(new UserModel());
		model.addProposal(new UserModel(), new Proposal("","","","", "","sender", "comment", "experienceTitle", "pending"));
		assertTrue(model.acceptProposal(new UserModel(), "p001"));
		assertFalse(model.getProposals().containsKey("p001"));
	}

	@Test
	public void testRejectProposal() {
		model.initialize(new UserModel());
		model.addProposal(new UserModel(), new  Proposal("","","","", "","sender", "comment", "experienceTitle", "pending"));
		assertTrue(model.rejectProposal(new UserModel(), "p001", "motivation"));
		assertFalse(model.getProposals().containsKey("p001"));
	}
	
	@Test
	public void testRemoveProposal() {
		model.initialize(new UserModel());
		model.addProposal(new UserModel(), new  Proposal("","","","","","sender", "comment", "experienceTitle", "pending"));
		model.removeProposal("p001");
		assertFalse(model.getProposals().containsKey("p001"));
	}
	
	@Test
	public void testAddProposal() {
		model.initialize(new UserModel());
		model.addProposal(new UserModel(), new  Proposal("","","","","","sender", "comment", "experienceTitle", "pending"));
		assertTrue(model.getProposals().containsKey("p001"));
	}

}

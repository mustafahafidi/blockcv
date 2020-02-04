package com.blockcv.view.pages;

import static org.junit.Assert.*;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.view.curriculum.ProposalView;

public class ProposalsPageViewTest {

	private ProposalsPageView view;
	
	@Before
	public void setUp() {
		view = new ProposalsPageView(new EventBus());
	}
	
	@Test
	public void testAddProposal() {
		assertEquals(0, view.getProposals().getComponentCount());
		view.addProposal(new ProposalView("p001", view, "", "", "", ""));
		assertEquals(1, view.getProposals().getComponentCount());
	}

	@Test
	public void testGetUriFragment() {
		assertEquals(ProposalsPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(ProposalsPageView.MENU_NAME, view.getMenuName());
	}

}

package com.blockcv.view.pages;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.data.DataAccess;
import com.blockcv.presenter.App;
import com.blockcv.presenter.LoginPresenter;
import com.blockcv.presenter.MainPagePresenter;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.offer.CandidateView;
import com.blockcv.view.offer.OfferView;
import com.vaadin.ui.VerticalLayout;

public class ManageOffersPageViewTest {

	private ManageOffersPageView view;
	
	@Before
	public void setUp() {
		view = new ManageOffersPageView(new EventBus());
	}
	
	@Test
	public void testSetAddOfferViewFields() {
		view.setAddOfferViewFields(Arrays.asList("title", "2", "", "", "", "", "", "", "", ""));
		assertEquals("title", view.getAddOfferView().getFields().get("title"));
	}

	@Test
	public void testDeselectOffers() {
		OfferView offerView = new OfferView("", view, "", "", 2, "", "", "", "", "", "", "", "", false);
		offerView.addStyleName("selectedOffer");
		view.addOffer(offerView);
		view.deselectOffers();
		assertFalse(view.getOffersList().getComponent(0).getStyleName().contains("selectedOffer"));
	}
	
	@Test
	public void testSetSelectedOff() {
		VerticalLayout off = new VerticalLayout();
		view.setSelectedOff(off);
		assertEquals(off,  view.getSelectedOff());
	}

	@Test
	public void testAddCandidate() {
		view.addCandidate(new CandidateView("", "", ""));
		assertEquals(1, view.getCandidatesLayout().getComponentCount());
	}

	@Test
	public void testRemoveSelectedOff() {
		OfferView offerView = new OfferView("", view, "", "", 2, "", "", "", "", "", "", "", "", false);
		view.addOffer(offerView);
		view.setSelectedOff(offerView);
		view.removeSelectedOff();
		assertEquals(0,view.getOffersList().getComponentCount());
	}

	@Test
	public void testSetAddMode() {
		view.setAddMode(false);
		assertFalse(view.getAddOfferButton().isVisible());
		view.setAddMode(true);
		assertTrue(view.getAddOfferButton().isVisible());
	}

	@Test
	public void testEmptyOfferView() {
		view.setAddOfferViewFields(Arrays.asList("title", "2", "", "", "", "", "", "", "", ""));
		view.emptyOfferView();
		assertEquals("", view.getAddOfferView().getFields().get("title"));
	}

	@Test
	public void testAddOffer() {
		assertEquals(0, view.getOffersList().getComponentCount());
		view.addOffer(new OfferView("", view, "", "", 2, "", "", "", "", "", "", "", "", false));
		assertEquals(1, view.getOffersList().getComponentCount());
	}

	@Test
	public void testGetUriFragment() {
		assertEquals(ManageOffersPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(ManageOffersPageView.MENU_NAME, view.getMenuName());
	}

}

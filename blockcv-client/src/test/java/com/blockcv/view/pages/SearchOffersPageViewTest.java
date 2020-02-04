package com.blockcv.view.pages;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.view.offer.OfferView;

public class SearchOffersPageViewTest {

	private SearchOffersPageView view;
	
	@Before
	public void setUp() {
		view = new SearchOffersPageView(new EventBus());
	}
	
	@Test
	public void testGetFilters() {
		view.getKeywords().setValue("keyword");
		assertEquals("keyword", view.getFilters().get("keywords"));
	}

	@Test
	public void testSetOffers() {
		assertEquals(0, view.getOffers().getComponentCount());
		OfferView offer = new OfferView("off001", view, "", "", 2, "", "", "", "", "", "", "", "", false);
		view.setOffers(Arrays.asList(offer));
		assertEquals(offer, view.getOffers().getComponent(0));
	}

	@Test
	public void testSetCandidate() {
		view.setCandidate("company", "title", "off001", true);
		assertEquals("title, company", view.getSelectedOffer().getValue());
	}

	@Test
	public void testDeselectOffers() {
		OfferView offer = new OfferView("off001", view, "", "", 2, "", "", "", "", "", "", "", "", false);
		offer.addStyleName("selectedOffer");
		view.setOffers(Arrays.asList(offer));
		assertTrue(view.getOffers().getComponent(0).getStyleName().contains("selectedOffer"));
		view.deselectOffers();
		assertFalse(view.getOffers().getComponent(0).getStyleName().contains("selectedOffer"));
	}

	@Test
	public void testGetUriFragment() {
		assertEquals(SearchOffersPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(SearchOffersPageView.MENU_NAME, view.getMenuName());
	}

}

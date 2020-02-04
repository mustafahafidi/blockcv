package com.blockcv.presenter;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.CandidateEvent;
import com.blockcv.events.SearchOfferEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.SearchOffersModel;
import com.blockcv.model.SearchOffersModel.WorkOffer;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.SearchOffersPageView;

public class SearchOffersPresenterTest {
	
	private App app;
	private MainPagePresenter mainPresenter;
	private SearchOffersPresenter searchOffersPresenter;
	private SearchOffersPageView searchOffersPageView;
	private SearchOffersModel searchOffersModel;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		searchOffersPresenter = (SearchOffersPresenter)app.getMainPagePresenter().getPresenters().get(6);
		searchOffersPageView = (SearchOffersPageView)searchOffersPresenter.getPageView();
		searchOffersModel = searchOffersPresenter.getSearchOffersModel();
		// LOGIN
		mainPresenter.getEventBus().post(new TryLoginEvent("email", "password"));
	}

	@Test
	public void testOnSearchOfferEvent() {
		assertFalse(searchOffersPageView.getOffers().isVisible());
		assertNull(searchOffersModel.getOffers());
		searchOffersPresenter.onSearchOfferEvent(new SearchOfferEvent(new HashMap<>()));
		assertNotNull(searchOffersModel.getOffers());
		assertFalse(searchOffersPageView.getOffers().isVisible());
	}

	@Test
	public void testOnCandidateEvent() {
		searchOffersPresenter.onSearchOfferEvent(new SearchOfferEvent(new HashMap<>()));
		searchOffersModel.addOffer("off001", new WorkOffer("","", "", "", "", "", "", "", "", "", "","", false));
		assertFalse(searchOffersModel.getOffers().get("off001").alreadyCandidated());
		searchOffersPresenter.onCandidateEvent(new CandidateEvent("off001"));
		assertTrue(searchOffersModel.getOffers().get("off001").alreadyCandidated());
	}

	@Test
	public void testGetPageView() {
		assertNotNull(searchOffersPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = searchOffersPresenter.getPageView();
		searchOffersPresenter.clean();
		NavigableView after = searchOffersPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

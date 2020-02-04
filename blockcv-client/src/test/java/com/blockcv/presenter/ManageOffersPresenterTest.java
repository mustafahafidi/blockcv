package com.blockcv.presenter;

import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.AddExpEvent;
import com.blockcv.events.AddOfferEvent;
import com.blockcv.events.RemoveExpEvent;
import com.blockcv.events.RemoveOfferEvent;
import com.blockcv.events.SaveOffersEvent;
import com.blockcv.events.SelectedOfferEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.ManageOffersModel;
import com.blockcv.model.ManageOffersModel.Offer;
import com.blockcv.model.ManageOffersModel.Offer.Candidate;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.ManageCVPageView;
import com.blockcv.view.pages.ManageOffersPageView;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class ManageOffersPresenterTest {

	private App app;
	private MainPagePresenter mainPresenter;
	private ManageOffersPresenter manageOffersPresenter;
	private ManageOffersPageView manageOffersPageView;
	private ManageOffersModel manageOffersModel;
	
	private Map<String,String> data;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		manageOffersPresenter = (ManageOffersPresenter)app.getMainPagePresenter().getPresenters().get(3);
		manageOffersPageView = (ManageOffersPageView)manageOffersPresenter.getPageView();
		manageOffersModel = manageOffersPresenter.getManageOffersModel();
		// LOGIN
		mainPresenter.getEventBus().post(new TryLoginEvent("email", "password"));
		// OFFER DATA
		data = new HashMap<>();
		data.put("title", "");
		data.put("maxCandidates", "2");
		data.put("employmentSector", "");
		data.put("workFunction", "");
		data.put("requiredStudyTitle", "");
		data.put("requiredCert", "");
		data.put("expirationDate", "");
		data.put("contractType", "");
		data.put("salaryRange", "");
		data.put("description", "");
	}
	
	@Test
	public void testOnChangedOrgEvent() {
		assertNotNull(manageOffersModel.getOffers());
	}

	@Test
	public void testOnSelectedOfferEvent() {
		manageOffersPresenter.onAddOfferEvent(new AddOfferEvent(data));
		String offID = manageOffersModel.getOffers().keySet().iterator().next();
		manageOffersModel.getOffers().get(offID).addCandidate(new Candidate("", "", ""));
		manageOffersPresenter.onSelectedOfferEvent(new SelectedOfferEvent(offID));
		assertEquals(1, manageOffersPageView.getCandidatesLayout().getComponentCount());
	}

	@Test
	public void testOnAddOfferEvent() {
		assertEquals(0, manageOffersPageView.getOffersList().getComponentCount());
		assertEquals(0, manageOffersModel.getOffers().size());
		manageOffersPresenter.onAddOfferEvent(new AddOfferEvent(data));
		assertEquals(1, manageOffersModel.getOffers().size());
		assertEquals(1, manageOffersPageView.getOffersList().getComponentCount());
		assertEquals("", manageOffersPageView.getAddOfferView().getFields().get("title"));
	}

	@Test
	public void testOnRemoveOfferEvent() {
		manageOffersPresenter.onAddOfferEvent(new AddOfferEvent(data));
		Component insertedOffer = manageOffersPageView.getOffersList().getComponent(0);
		manageOffersPageView.setSelectedOff((VerticalLayout)insertedOffer);
		manageOffersPresenter.onRemoveOfferEvent(new RemoveOfferEvent(insertedOffer.getId()));
		assertEquals(0, manageOffersModel.getOffers().size());
		assertEquals(0, manageOffersPageView.getOffersList().getComponentCount());
	}

	@Test
	public void testOnSaveOffersEvent() {
		manageOffersModel.addNewOffer(new Offer("","","","", 2, "", "", "", "", "", "", "", "", new ArrayList<>()));
		Map<String,Offer> before = manageOffersModel.getOffers();
		manageOffersPresenter.onSaveOffersEvent(new SaveOffersEvent());
		Map<String,Offer> after = manageOffersModel.getOffers();
		assertNotEquals(before, after);
	}

	@Test
	public void testGetPageView() {
		assertNotNull(manageOffersPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = manageOffersPresenter.getPageView();
		manageOffersPresenter.clean();
		NavigableView after = manageOffersPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

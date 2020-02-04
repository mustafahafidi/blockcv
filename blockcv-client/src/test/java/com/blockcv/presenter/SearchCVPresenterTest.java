package com.blockcv.presenter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.CertAssignEvent;
import com.blockcv.events.SearchCVEvent;
import com.blockcv.events.SelectedCVEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.ManageOffersModel;
import com.blockcv.model.SearchCVModel;
import com.blockcv.model.SearchCVModel.Curriculum;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.curriculum.CompactCVView;
import com.blockcv.view.curriculum.CurriculumView;
import com.blockcv.view.pages.ManageOffersPageView;
import com.blockcv.view.pages.SearchCVPageView;

public class SearchCVPresenterTest {
	
	private App app;
	private MainPagePresenter mainPresenter;
	private SearchCVPresenter searchCVPresenter;
	private SearchCVPageView searchCVPageView;
	private SearchCVModel searchCVModel;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.ORGANIZATION);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		searchCVPresenter = (SearchCVPresenter)app.getMainPagePresenter().getPresenters().get(5);
		searchCVPageView = (SearchCVPageView)searchCVPresenter.getPageView();
		searchCVModel = searchCVPresenter.getSearchCVModel();
		// LOGIN
		mainPresenter.getEventBus().post(new TryLoginEvent("email", "password"));
	}

	@Test
	public void testOnSearchCVEvent() {
		assertNull(searchCVModel.getCvs());
		CurriculumView before = searchCVPageView.getSelectedCV();
		searchCVPresenter.onSearchCVEvent(new SearchCVEvent(new HashMap<>()));
		CurriculumView after = searchCVPageView.getSelectedCV();
		assertNotEquals(before, after);
		assertNotNull(searchCVModel.getCvs());
	}

	@Test
	public void testOnSelectedCVEvent() {
		Map<String,Curriculum> cvs = new HashMap<>();
		cvs.put("cv001", new Curriculum(false, new HashMap<>(), new ArrayList<>(), new ArrayList<>()));
		searchCVModel.setCvs(cvs);
		CompactCVView ccv = new CompactCVView(searchCVPageView, "cv001", "", "", "", false);
		searchCVPageView.setCompactCVs(Arrays.asList(ccv));
		searchCVPageView.setSelectedCompactCV(ccv);
		searchCVPresenter.onSelectedCVEvent(new SelectedCVEvent("cv001"));
		assertEquals("cv001", searchCVPageView.getSelectedCV().getId());
		assertFalse(searchCVPageView.getCertify().isVisible());
	}

	@Test
	public void testOnCertAssignEvent() {
		Map<String,Curriculum> cvs = new HashMap<>();
		cvs.put("cv001", new Curriculum(true, new HashMap<>(), new ArrayList<>(), new ArrayList<>()));
		searchCVModel.setCvs(cvs);
		CompactCVView ccv = new CompactCVView(searchCVPageView, "cv001", "", "", "", true);
		searchCVPageView.setCompactCVs(Arrays.asList(ccv));
		searchCVPageView.setSelectedCompactCV(ccv);
		assertTrue(searchCVPageView.getSelectedCompactCV().isCertifiable());
		assertTrue(searchCVModel.getCV("cv001").isCertifiable());
		searchCVPresenter.onCertAssignEvent(new CertAssignEvent("cv001", ""));
		assertFalse(searchCVModel.getCV("cv001").isCertifiable());
		assertFalse(searchCVPageView.getSelectedCompactCV().isCertifiable());
	}

	@Test
	public void testGetPageView() {
		assertNotNull(searchCVPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = searchCVPresenter.getPageView();
		searchCVPresenter.clean();
		NavigableView after = searchCVPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

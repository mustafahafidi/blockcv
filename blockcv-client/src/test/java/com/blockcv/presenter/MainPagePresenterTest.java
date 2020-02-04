package com.blockcv.presenter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.NavigateEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.stub.NavigatorStub;
import com.blockcv.view.pages.HomePageView;
import com.blockcv.view.pages.LoginPageView;
import com.blockcv.view.pages.ManageCVPageView;
import com.blockcv.view.pages.SearchOffersPageView;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class MainPagePresenterTest {

	private App app;
	private MainPagePresenter presenter;
	
	private String getCurrentURI() {
		return ((NavigatorStub)app.getNavigator()).getCurrentURI();
	}
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		presenter = app.getMainPagePresenter();
	}

	@Test
	public void testGetUserModel() {
		assertNotNull(presenter.getUserModel());
	}

	@Test
	public void testGetEventBus() {
		assertNotNull(presenter.getEventBus());
	}

	@Test
	public void testSetLoggedInView() {
		presenter.getEventBus().post(new TryLoginEvent("email", "password"));
		assertEquals(ManageCVPageView.URI, getCurrentURI());
	}

	@Test
	public void testSetLoggedOutView() {
		presenter.getEventBus().post(new TryLoginEvent("email", "password"));
		presenter.setLoggedOutView();
		assertEquals(App.FIRSTPAGE, getCurrentURI());
		assertEquals("-", presenter.getUserModel().getEmail());
	}

	@Test
	public void testShowNotification() {
		Notification n = MainPagePresenter.showNotification("message", Type.WARNING_MESSAGE);
		assertEquals(5000, n.getDelayMsec());
		assertEquals(Position.BOTTOM_LEFT, n.getPosition());
	}
	
	@Test
	public void testGetContentContainer() {
		assertNotNull(presenter.getContentContainer());
	}

	@Test
	public void testGetPageView() {
		assertNotNull(presenter.getPageView());
	}

	@Test // INTEGRATION TEST
	public void testOnNavigateEvent() {
		assertEquals(HomePageView.URI, getCurrentURI());
		presenter.onNavigateEvent(new NavigateEvent(LoginPageView.URI));
		assertEquals(LoginPageView.URI, getCurrentURI());
		presenter.getEventBus().post(new TryLoginEvent("email", "password"));
		assertEquals(ManageCVPageView.URI, getCurrentURI());
		presenter.onNavigateEvent(new NavigateEvent(LoginPageView.URI));
		assertEquals(ManageCVPageView.URI, getCurrentURI());
	}

}

package com.blockcv.presenter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.WelcomePageHideEvent;
import com.blockcv.events.WelcomePageShowEvent;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.stub.NavigatorStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.main.MainPageView;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewLeaveAction;

public class HomePresenterTest {

	private App app;
	private MainPagePresenter mainPresenter;
	private HomePresenter homePresenter;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		homePresenter = (HomePresenter)app.getMainPagePresenter().getPresenters().get(0);
	}
	
	@Test
	public void testGetPageView() {
		assertNotNull(homePresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = homePresenter.getPageView();
		homePresenter.clean();
		NavigableView after = homePresenter.getPageView();
		assertNotEquals(before, after);
	}

	@Test // INTEGRATION TEST
	public void testOnWelcomePageShowEvent() {
		assertTrue(((MainPageView)mainPresenter.getPageView()).getHeaderView().getMenu().isVisible());
		assertTrue(((MainPageView)mainPresenter.getPageView()).getFooterView().isVisible());
		assertNotEquals("v-scrollable", mainPresenter.getContentContainer().getStyleName());
		homePresenter.onWelcomePageShowEvent(new WelcomePageShowEvent());
		assertEquals("v-scrollable", mainPresenter.getContentContainer().getStyleName());
		assertFalse(((MainPageView)mainPresenter.getPageView()).getHeaderView().getMenu().isVisible());	
		assertFalse(((MainPageView)mainPresenter.getPageView()).getFooterView().isVisible());
	}

	@Test // INTEGRATION TEST
	public void testOnWelcomePageHideEvent() {
		homePresenter.onWelcomePageShowEvent(new WelcomePageShowEvent());
		homePresenter.onWelcomePageHideEvent(new WelcomePageHideEvent(new ViewBeforeLeaveEvent(app.getNavigator(), () -> {})));
		assertNotEquals("v-scrollable", mainPresenter.getContentContainer().getStyleName());
		assertTrue(((MainPageView)mainPresenter.getPageView()).getHeaderView().getMenu().isVisible());
		assertTrue(((MainPageView)mainPresenter.getPageView()).getFooterView().isVisible());
	}

}

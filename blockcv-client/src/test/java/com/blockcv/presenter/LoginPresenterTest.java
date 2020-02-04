package com.blockcv.presenter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.LogoutEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.UserModel;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.stub.NavigatorStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.main.MainPageView;
import com.blockcv.view.pages.HomePageView;
import com.blockcv.view.pages.ManageCVPageView;

public class LoginPresenterTest {

	/*
	private static JettyServer server;
	
	@BeforeClass
	public static void setUp() {
		try {
			server = new JettyServer(8080, "target/classes");
			if(server.isStarted())
				server.stop();
			server.start();
			
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}*/
	
	private App app;
	private MainPagePresenter mainPresenter;
	private LoginPresenter loginPresenter;
	
	private String getCurrentURI() {
		return ((NavigatorStub)app.getNavigator()).getCurrentURI();
	}
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		loginPresenter = (LoginPresenter)app.getMainPagePresenter().getPresenters().get(1);
	}

	@Test // INTEGRATION TEST
	public void testOnTryLoginEvent() {
		assertEquals(HomePageView.URI, getCurrentURI());
		assertEquals(3,(((MainPageView)mainPresenter.getPageView()).getHeaderView().getMenu().getItems().size()));
		assertFalse(mainPresenter.getUserModel().isLoggedIn());
		loginPresenter.onTryLoginEvent(new TryLoginEvent("email", "password"));
		assertTrue(mainPresenter.getUserModel().isLoggedIn());
		assertEquals(5,(((MainPageView)mainPresenter.getPageView()).getHeaderView().getMenu().getItems().size()));
		assertEquals(ManageCVPageView.URI, getCurrentURI());
	}

	@Test // INTEGRATION TEST
	public void testOnLogoutEvent() {
		mainPresenter.getEventBus().post(new TryLoginEvent("email", "password"));
		loginPresenter.onLogoutEvent(new LogoutEvent());
		assertFalse(mainPresenter.getUserModel().isLoggedIn());
		assertEquals(3,(((MainPageView)mainPresenter.getPageView()).getHeaderView().getMenu().getItems().size()));
		assertEquals(HomePageView.URI, getCurrentURI());
	}

	@Test
	public void testGetPageView() {
		assertNotNull(loginPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = loginPresenter.getPageView();
		loginPresenter.clean();
		NavigableView after = loginPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

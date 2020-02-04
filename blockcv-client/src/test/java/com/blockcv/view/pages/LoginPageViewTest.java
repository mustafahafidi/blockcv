package com.blockcv.view.pages;

import static org.junit.Assert.*;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

public class LoginPageViewTest {

	private LoginPageView view;
	
	@Before
	public void setUp() {
		view = new LoginPageView(new EventBus());
	}
	
	@Test
	public void testGetUriFragment() {
		assertEquals(LoginPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(LoginPageView.MENU_NAME, view.getMenuName());
	}

}

package com.blockcv.view.pages;

import static org.junit.Assert.*;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

public class HomePageViewTest {

	private HomePageView view;
	
	@Before
	public void setUp() {
		view = new HomePageView(new EventBus());
	}

	@Test
	public void testGetUriFragment() {
		assertEquals(HomePageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(HomePageView.MENU_NAME, view.getMenuName());
	}

}

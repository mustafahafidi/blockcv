package com.blockcv.view.pages;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

public class SignupPageViewTest {

	private SignupPageView view;
	
	@Before
	public void setUp() {
		view = new SignupPageView(new EventBus());
	}
	
	@Test
	public void testGetPersonalInfo() {
		view.getFirstname().setValue("firstname");
		view.getDateOfBirth().setValue(LocalDate.parse("01-01-2000", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		assertEquals("firstname", view.getPersonalInfo().get("firstname"));
		assertEquals("2000-01-01", view.getPersonalInfo().get("dateOfBirth"));
	}
	
	@Test
	public void testGetOrganizationInfo() {
		view.getOrgName().setValue("orgName");
		assertEquals("orgName", view.getOrganizationInfo().get("orgName"));
	}
	
	@Test
	public void testChangeTypeForm() {
		assertTrue(view.getPersonalInfoLayout().isVisible());
		view.changeTypeForm("Organizzazione");
		assertFalse(view.getPersonalInfoLayout().isVisible());
	}
	
	@Test
	public void testGetUriFragment() {
		assertEquals(SignupPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(SignupPageView.MENU_NAME, view.getMenuName());
	}

}

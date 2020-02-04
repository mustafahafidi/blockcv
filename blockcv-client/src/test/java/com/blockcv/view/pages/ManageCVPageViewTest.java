package com.blockcv.view.pages;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.view.curriculum.EducationalExpView;
import com.blockcv.view.curriculum.PersonalInfoView;
import com.blockcv.view.curriculum.WorkingExpView;

public class ManageCVPageViewTest {

	private ManageCVPageView view;
	private PersonalInfoView piView;
	
	@Before
	public void setUp() {
		view = new ManageCVPageView(new EventBus());
		Map<String,Boolean> vis = new HashMap<>();
		vis.put("curriculum", false);
		vis.put("dateOfBirth", false);
		vis.put("placeOfBirth", false);
		vis.put("phoneNumber", false);
		vis.put("gender", false);
		vis.put("fiscalCode", false);
		piView = new PersonalInfoView("firstname", "", "2000-01-01", "", "", "", "Maschio", "", vis);
	}

	@Test
	public void testSetPersonalInfo() {
		PersonalInfoView before = view.getPersonalInfo();
		view.setPersonalInfo(piView);
		PersonalInfoView after = view.getPersonalInfo();
		assertNotEquals(before, after);
		assertEquals("firstname", after.getFirstname().getValue());
	}

	@Test
	public void testSetPublicCV() {
		view.setPersonalInfo(piView);
		assertFalse(view.getPersonalInfo().getDateOfBirthV().isVisible());
		view.setPublicCV(true);
		assertTrue(view.getPersonalInfo().getDateOfBirthV().isVisible());
		assertFalse(view.getPublicCV().isVisible());
	}

	@Test
	public void testGetVisibility() {
		view.setPersonalInfo(piView);
		assertEquals(false, view.getVisibility().get("curriculum"));
	}

	@Test
	public void testSetAddExpViewFields() {
		view.setAddExpViewFields("edu", Arrays.asList("studyTitle","","","","",""));
		assertEquals("studyTitle", view.getAddExperienceView().getFields().get("studyTitle"));
	}

	@Test
	public void testDeselectExps() {
		EducationalExpView exp = new EducationalExpView("exp001", "0", view, "", "", "", "", "", "", false, new EventBus());
		view.addEducationalExp(exp, false);
		exp.addStyleName("selectedExp");
		view.deselectExps();
		assertFalse(exp.getStyleName().contains("selectedExp"));
	}

	@Test
	public void testSetSelectedExp() {
		EducationalExpView exp = new EducationalExpView("exp001", "0", view, "", "", "", "", "", "", false, new EventBus());
		view.addEducationalExp(exp, false);
		view.setSelectedExp(exp);
		assertEquals(exp, view.getSelectedExp());
	}

	@Test
	public void testRemoveAllExps() {
		EducationalExpView exp = new EducationalExpView("exp001", "0", view, "", "", "", "", "", "", false, new EventBus());
		view.addEducationalExp(exp, false);
		view.removeAllExps();
		assertEquals(0, view.getEducationalExps().getComponentCount());
	}

	@Test
	public void testRemoveSelectedExp() {
		EducationalExpView exp = new EducationalExpView("exp001", "0", view, "", "", "", "", "", "", false, new EventBus());
		view.addEducationalExp(exp, false);
		view.setSelectedExp(exp);
		view.removeSelectedExp();
		assertEquals(0, view.getEducationalExps().getComponentCount());
	}

	@Test
	public void testSetAddMode() {
		view.setAddMode(true);
		assertTrue(view.getAddExpButton().isVisible());
	}

	@Test
	public void testEmptyExperienceView() {
		view.setAddExpViewFields("edu", Arrays.asList("studyTitle","","","","",""));
		view.emptyExperienceView();
		assertEquals("", view.getAddExperienceView().getFields().get("studyTitle"));
	}

	@Test
	public void testAddEducationalExp() {
		assertEquals(0, view.getEducationalExps().getComponentCount());
		EducationalExpView exp = new EducationalExpView("exp001", "0", view, "", "", "", "", "", "", false, new EventBus());
		view.addEducationalExp(exp, false);
		assertEquals(1, view.getEducationalExps().getComponentCount());
	}

	@Test
	public void testAddWorkingExp() {
		assertEquals(0, view.getWorkingExps().getComponentCount());
		WorkingExpView exp = new WorkingExpView("exp001", view, "", "", "", "", "", false, new EventBus());
		view.addWorkingExp(exp, false);
		assertEquals(1, view.getWorkingExps().getComponentCount());
	}

	@Test
	public void testGetUriFragment() {
		assertEquals(ManageCVPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(ManageCVPageView.MENU_NAME, view.getMenuName());
	}

}

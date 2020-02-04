package com.blockcv.view.pages;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.view.curriculum.CompactCVView;
import com.blockcv.view.curriculum.CurriculumView;

public class SearchCVPageViewTest {

	private SearchCVPageView view;
	
	@Before
	public void setUp() {
		view = new SearchCVPageView(new EventBus());
	}
	
	@Test
	public void testGetFilters() {
		view.getKeywords().setValue("keyword");
		assertEquals("keyword", view.getFilters().get("keywords"));
	}

	@Test
	public void testSetCompactCVs() {
		CompactCVView cv = new CompactCVView(view, "cv001", "", "", "", true);
		view.setCompactCVs(Arrays.asList(cv));
		assertEquals(1, view.getCvs().getComponentCount());
	}

	@Test
	public void testDeselectCVs() {
		CompactCVView cv = new CompactCVView(view, "cv001", "", "", "", true);
		view.setCompactCVs(Arrays.asList(cv));
		cv.addStyleName("selectedCV");
		view.deselectCVs();
		assertFalse(view.getCvs().getComponent(0).getStyleName().contains("selectedCV"));
	}

	@Test
	public void testSetSelectedCV() {
		CurriculumView cv = new CurriculumView("cv001", view, new HashMap<>());
		view.setSelectedCV(cv);
		assertEquals(cv, view.getSelectedCV());
	}

	@Test
	public void testSetSelectedCVCertifiable() {
		CompactCVView cv = new CompactCVView(view, "cv001", "", "", "", true);
		view.setCompactCVs(Arrays.asList(cv));
		view.setSelectedCompactCV(cv);
		view.setSelectedCVCertifiable(false);
		assertFalse(cv.isCertifiable());
	}

	@Test
	public void testSetCertifyVisible() {
		assertFalse(view.getCertify().isVisible());
		view.setCertifyVisible(true);
		assertTrue(view.getCertify().isVisible());
	}

	@Test
	public void testGetUriFragment() {
		assertEquals(SearchCVPageView.URI, view.getUriFragment());
	}

	@Test
	public void testGetMenuName() {
		assertEquals(SearchCVPageView.MENU_NAME, view.getMenuName());
	}

}

package com.blockcv.presenter;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.AddExpEvent;
import com.blockcv.events.CertRequestEvent;
import com.blockcv.events.ExportRequestEvent;
import com.blockcv.events.ImportEvent;
import com.blockcv.events.PublicCVEvent;
import com.blockcv.events.RemoveExpEvent;
import com.blockcv.events.SaveCVEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.ManageCVModel;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.ManageCVPageView;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class ManageCVPresenterTest {

	private App app;
	private MainPagePresenter mainPresenter;
	private ManageCVPresenter manageCVPresenter;
	private ManageCVPageView manageCVPageView;
	private ManageCVModel manageCVModel;
	
	private Map<String,String> data;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		manageCVPresenter = (ManageCVPresenter)app.getMainPagePresenter().getPresenters().get(2);
		manageCVPageView = (ManageCVPageView)manageCVPresenter.getPageView();
		manageCVModel = manageCVPresenter.getManageCVModel();
		// LOGIN
		mainPresenter.getEventBus().post(new TryLoginEvent("email", "password"));
		//EXP DATA
		data = new HashMap<>();
		data.put("type", "Formativa");
		data.put("visibility", "false");
		data.put("status", "pending");
		data.put("studyTitle", "");
		data.put("institute", "");
		data.put("specialization", "");
		data.put("venue", "");
		data.put("periodFrom", "");
		data.put("periodTo", "");
	}

	@Test // INTEGRATION TEST
	public void testOnChangedWorkerEvent() {
		assertNotNull(manageCVModel.getEducationalExps());
		assertEquals("Firstname", manageCVPageView.getPersonalInfo().getFirstname().getValue());
		assertEquals(manageCVModel.getInfoVisibility().get("curriculum"), !manageCVPageView.getPublicCV().isVisible());
	}

	@Test // INTEGRATION TEST
	public void testOnPublicCVEvent() {
		manageCVPresenter.onPublicCVEvent(new PublicCVEvent());
		assertFalse(manageCVPageView.getPublicCV().isVisible());
	}

	
	@Test // INTEGRATION TEST
	public void testOnExportRequestEvent() {
		manageCVPresenter.onExportRequestEvent(new ExportRequestEvent());
		assertNotNull(manageCVPresenter.getToExport());
		assertEquals(manageCVPresenter.getToExport(), manageCVPageView.getFileDownloader().getFileDownloadResource());
	}

	@Test // INTEGRATION TEST
	public void testOnAddExpEvent() {
		assertEquals(0, manageCVPageView.getEducationalExps().getComponentCount());
		assertEquals(0, manageCVModel.getEducationalExps().size());
		manageCVPresenter.onAddExpEvent(new AddExpEvent(data));
		assertEquals(1, manageCVModel.getEducationalExps().size());
		assertEquals(1, manageCVPageView.getEducationalExps().getComponentCount());
	}

	@Test // INTEGRATION TEST
	public void testOnRemoveExpEvent() {
		manageCVPresenter.onAddExpEvent(new AddExpEvent(data));
		Component insertedExp = manageCVPageView.getEducationalExps().getComponent(0);
		manageCVPageView.setSelectedExp((VerticalLayout)insertedExp);
		manageCVPresenter.onRemoveExpEvent(new RemoveExpEvent(insertedExp.getId()));
		assertEquals(0, manageCVModel.getEducationalExps().size());
		assertEquals(0, manageCVPageView.getEducationalExps().getComponentCount());
	}

	@Test // INTEGRATION TEST
	public void testOnCertRequestEvent() {
		manageCVPresenter.onAddExpEvent(new AddExpEvent(data));
		String expID = manageCVPageView.getEducationalExps().getComponent(0).getId();
		manageCVPresenter.onCertRequestEvent(new CertRequestEvent(expID, "", "", "", ""));
		assertTrue(manageCVModel.requestExpCertification(mainPresenter.getUserModel(), expID, "", "", "",""));
	}

	@Test // INTEGRATION TEST
	public void testOnSaveCVEvent() {
		Map<String,Boolean> vis = new HashMap<>();
		vis.put("curriculum", true);
		vis.put("dateOfBirth", false);
		vis.put("placeOfBirth", false);
		vis.put("phoneNumber", false);
		vis.put("gender", false);
		vis.put("fiscalCode", false);
		assertFalse(manageCVModel.getInfoVisibility().get("curriculum"));
		manageCVPresenter.onSaveCVEvent(new SaveCVEvent(vis));
		assertTrue(manageCVModel.getInfoVisibility().get("curriculum"));
	}

	@Test
	public void testGetPageView() {
		assertNotNull(manageCVPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = manageCVPresenter.getPageView();
		manageCVPresenter.clean();
		NavigableView after = manageCVPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

package com.blockcv.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.pages.ManageOffersPageView;

public class ManageCVModelTest {
	
	private ManageCVModel model;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		model = new ManageCVModel();
	}

	@Test
	public void testLoadExperiences() {
		Map<String,Map<String,String>> exps = model.getEducationalExps();
		model.loadExperiences(new UserModel());
		assertNotEquals(exps, model.getEducationalExps());
	}

	@Test
	public void testAddExperience() {
		model.loadExperiences(new UserModel());
		String expID = model.addExperience(ExperienceType.EDUCATIONAL, new HashMap<>());
		assertTrue(model.getEducationalExps().containsKey(expID));
	}

	@Test
	public void testRemoveExperience() {
		model.loadExperiences(new UserModel());
		String expID = model.addExperience(ExperienceType.EDUCATIONAL, new HashMap<>());
		model.removeExperience(expID);
		assertFalse(model.getEducationalExps().containsKey(expID));
	}

	@Test
	public void testRequestExpCertification() {
		UserModel userModel = new UserModel();
		model.loadExperiences(userModel);
		String expID = model.addExperience(ExperienceType.EDUCATIONAL, new HashMap<>());
		assertTrue(model.requestExpCertification(userModel, expID, "", "", "", ""));
	}
	
	@Test
	public void testSaveCurriculum() {
		UserModel userModel = new UserModel();
		assertTrue(model.saveCurriculum(userModel, new HashMap<>()));
	}
	
	@Test
	public void testSetData() {
		model.setData(new ManageCVModel(new HashMap<>(), new HashMap<>(), new HashMap<>()));
		assertNotNull(model.getEducationalExps());
	}
	
	@Test
	public void testGetExperienceById() {
		model.loadExperiences(new UserModel());
		String expID = model.addExperience(ExperienceType.EDUCATIONAL, new HashMap<>());
		assertNotNull(model.getExperienceById(expID, ExperienceType.EDUCATIONAL));
	}
	
	@Test
	public void testGetInfoVisibility() {
		model.loadExperiences(new UserModel());
		assertNotNull(model.getInfoVisibility());
	}
	
}

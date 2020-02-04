package com.blockcv.stub;

import java.util.HashMap;
import java.util.Map;

import com.blockcv.model.ManageCVModel;
import com.blockcv.model.ManageOffersModel;
import com.blockcv.model.UserModel;
import com.blockcv.model.ManageOffersModel.Offer;
import com.blockcv.model.ProposalsModel.Proposal;
import com.blockcv.model.SearchCVModel.Curriculum;
import com.blockcv.model.SearchOffersModel.WorkOffer;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;

public class DataAccessStub extends DataAccess {
	
	private static DataAccess singleInstance;
	private static UserType userType;
	
	private DataAccessStub() {
		super("stub");
	}
	
	public static DataAccess getInstance() {
		if(singleInstance == null)
			singleInstance = new DataAccessStub();
		return singleInstance;
	}
	
	public static void setUserType(UserType userType) {
		DataAccessStub.userType = userType;
	}
	
	@Override
	public UserModel tryLogin(String user, String pass) {
		Map<String,String> personalInfo = new HashMap<>();
		personalInfo.put("firstname", "Firstname");
		personalInfo.put("lastname", "Lastname");
		personalInfo.put("dateOfBirth", "01-01-2000");
		personalInfo.put("placeOfBirth", "Place");
		personalInfo.put("address", "Address");
		personalInfo.put("phoneNumber", "0123456789");
		personalInfo.put("gender", "Maschio");
		personalInfo.put("fiscalCode", "ABCDEFGHIJKLMNOP");
		return new UserModel("0", "email", "password", userType, personalInfo, null);
	}
	
	@Override
	public boolean trySignup(String user, String pass, Map<String,String> info, UserType userType) {
		return true;
	}

	@Override
	public boolean loadExperiences(UserModel userModel, ManageCVModel manageCVModel) {
		Map<String,Boolean> vis = new HashMap<>();
		vis.put("curriculum", false);
		vis.put("dateOfBirth", false);
		vis.put("placeOfBirth", false);
		vis.put("phoneNumber", false);
		vis.put("gender", false);
		vis.put("fiscalCode", false);
		manageCVModel.setEducationalExps(new HashMap<>());
		manageCVModel.setWorkingExps(new HashMap<>());
		manageCVModel.setInfoVisibility(vis);
		return true;
	}
	
	/*@Override
	public boolean loadUserInfo(UserModel userModel) {
		Map<String,String> personalInfo = new HashMap<>();
		personalInfo.put("firstname", "Firstname");
		personalInfo.put("lastname", "Lastname");
		personalInfo.put("dateOfBirth", "2000-01-01");
		personalInfo.put("placeOfBirth", "Place");
		personalInfo.put("address", "Address");
		personalInfo.put("phoneNumber", "0123456789");
		personalInfo.put("gender", "Maschio");
		personalInfo.put("fiscalCode", "ABCDEFGHIJKLMNOP");
		userModel.setPersonalInfo(personalInfo);
		return true;
	}*/
	
	@Override
	public boolean saveCurriculum(UserModel userModel, ManageCVModel manageCVModel) {
		return true;
	}

	@Override
	public boolean requestCertifyExperience(UserModel userModel, Proposal proposal) {
		return true;
	}
	
	@Override
	public boolean certifyExperience(UserModel userModel, String expID) {
		return true;
	}
	
	@Override
	public boolean certifyCV(String cvID, String comment) {
		return true;
	}
	
	@Override
	public Map<String,Offer> getOrgOffers(UserModel userModel) {
		return new HashMap<>();
	}
	
	@Override
	public boolean saveOffers(UserModel userModel, ManageOffersModel manageOffersModel) {
		return true;
	}
	
	@Override
	public boolean candidateToOffer(UserModel userModel, WorkOffer wOffer) {
		return true;
	}

	@Override
	public Map<String,WorkOffer> getFilteredOffers(UserModel userModel, Map<String,String> filter) {
		return new HashMap<>();
	}

	@Override
	public Map<String,Curriculum> getFilteredCVs(UserModel userModel, Map<String,String> filter) {
		return new HashMap<>();
	}
	
	@Override
	public Map<String,Proposal> getProposals(UserModel userModel) {
		return new HashMap<>();
	}
	
	@Override
	public boolean acceptProposal(UserModel userModel, String id) {
		return true;
	}
	
	@Override
	public boolean rejectProposal(UserModel userModel, String id, String message) {
		return true;
	}
}

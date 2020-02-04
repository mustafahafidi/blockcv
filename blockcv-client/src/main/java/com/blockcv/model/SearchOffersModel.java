package com.blockcv.model;

import java.util.Map;

import com.blockcv.model.ManageOffersModel.Offer;
import com.blockcv.model.data.DataAccess;

public class SearchOffersModel implements Model {
	
	public static class WorkOffer {
		private final String id;
		private final String idOrg;
		private final String company;
		private final String title;
		private final String employmentSector;
		private final String workFunction;
		private final String requiredStudyTitle;
		private final String requiredCert;
		private final String expirationDate;
		private final String contractType;
		private final String salaryRange;
		private final String description;
		private boolean candidated;
		public WorkOffer(String id, String idOrg,  String c, String t, String es, String wf, String rst, String rc, String ed, String ct, String sr, String d, boolean candidated) {
			this.id = id;
			this.idOrg = idOrg;
			company = c;
			title = t;
			employmentSector = es;
			workFunction = wf;
			requiredStudyTitle = rst;
			requiredCert = rc;
			expirationDate = ed;
			contractType = ct;
			salaryRange = sr;
			description = d;
			this.candidated = candidated;
		}
		public String getCompany() {
			return company;
		}
		public String getIdOrg() {
			return idOrg;
		}
		public String getTitle() {
			return title;
		}
		public String getEmploymentSector() {
			return employmentSector;
		}
		public String getWorkFunction() {
			return workFunction;
		}
		public String getRequiredStudyTitle() {
			return requiredStudyTitle;
		}
		public String getRequiredCert() {
			return requiredCert;
		}
		public String getExpirationDate() {
			return expirationDate;
		}
		public String getContractType() {
			return contractType;
		}
		public String getSalaryRange() {
			return salaryRange;
		}
		public String getDescription() {
			return description;
		}
		public boolean alreadyCandidated() {
			return candidated;
		}
		public String getId() {
			return id;
		}
		public void setCandidated(boolean candidated) {
			this.candidated = candidated;
		}
	}
	
	Map<String,WorkOffer> offers;
	
	public SearchOffersModel() {}
	
	public Map<String,WorkOffer> getFilteredOffers(UserModel userModel, Map<String,String> filter) {
		offers = DataAccess.getInstance().getFilteredOffers(userModel, filter);
		return offers;
	}
	
	public boolean candidateToOffer(UserModel userModel, String offerID) {
		WorkOffer wOffer = offers.get(offerID);
		boolean candidated = DataAccess.getInstance().candidateToOffer(userModel, wOffer);
		wOffer.setCandidated(candidated);
		return candidated;
	}
	
	public Map<String, WorkOffer> getOffers() {
		return offers;
	}
	
	public void addOffer(String id, WorkOffer wo) {
		offers.put(id, wo);
	}
}

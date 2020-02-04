package com.blockcv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

import com.blockcv.model.ManageOffersModel.Offer.Candidate;
import com.blockcv.model.data.DataAccess;

public class ManageOffersModel implements Model {
	private static final Logger log = Logger.getLogger(ManageOffersModel.class);
	
	public static class Offer {
		
		public static class Candidate {
			private String name;
			private String address;
			private String email;
			public Candidate(String name, String address, String email) {
				this.name = name;
				this.address = address;
				this.email = email;
			}
			public String getName() {
				return name;
			}
			public String getAddress() {
				return address;
			}
			public String getEmail() {
				return email;
			}
		}
		private String id;
		private final String idOrg;
		private final String company;
		private final String title;
		private final Integer maxCandidates;
	
		private final String employmentSector;
		private final String workFunction;
		private final String requiredStudyTitle;
		private final String requiredCert;
		private final String expirationDate;
		private final String contractType;
		private final String salaryRange;
		private final String description;
		private final List<Candidate> candidates;
		public Offer(String id, String idOrg, String company,  String t, Integer mc, String es, String wf, String rst, String rc, String ed, String ct, String sr, String d, List<Candidate> c) {
			this.id = id;
			this.idOrg = idOrg;
			this.company = company;
			title = t;
			maxCandidates = mc;
			employmentSector = es;
			workFunction = wf;
			requiredStudyTitle = rst;
			requiredCert = rc;
			expirationDate = ed;
			contractType = ct;
			salaryRange = sr;
			description = d;
			candidates = c;
		}
		public String getTitle() {
			return title;
		}
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getIdOrg() {
			return idOrg;
		}
		public String getCompany() {
			return company;
		}
		public Integer getMaxCandidates() {
			return maxCandidates;
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
		public List<Candidate> getCandidates() {
			return candidates;
		}
		public void addCandidate(Candidate candidate) {
			candidates.add(candidate);
		}
		@Override
		public String toString() {
			return "Offer [id=" + id + ", idOrg=" + idOrg + ", company=" + company + ", title=" + title
					+ ", maxCandidates=" + maxCandidates + ", employmentSector=" + employmentSector + ", workFunction="
					+ workFunction + ", requiredStudyTitle=" + requiredStudyTitle + ", requiredCert=" + requiredCert
					+ ", expirationDate=" + expirationDate + ", contractType=" + contractType + ", salaryRange="
					+ salaryRange + ", description=" + description + ", candidates=" + candidates + "]";
		}
	}
	
	private final OfferIDGenerator generator = new OfferIDGenerator();
	private Map<String,Offer> offers;
	
	public void initialize(UserModel userModel) {
		offers = DataAccess.getInstance().getOrgOffers(userModel);
		generator.setCurrentID(offers.size()+1);
	}
	
	public Map<String,Offer> getOffers() {
		return offers;
	}
	
	public List<Candidate> getCandidates(String id) {
		return offers.get(id).getCandidates();
	}
	
	public String addNewOffer(Offer offer) {
		String offID = generator.getNextID();
		offer.setId(offID);

		log.info("AddNewOffer: received: "+offer.toString());
		offers.put(offID, offer);
		return offID;
	}
	
	public void removeOffer(String offerID) {
		offers.remove(offerID);
	}
	
	public boolean saveOffers(UserModel userModel) {
		if(DataAccess.getInstance().saveOffers(userModel, this)) {
			initialize(userModel);
			return true;
		}
		else return false;
	}
	
	
	// UTILITY INNER CLASS
	
	private class OfferIDGenerator {
		
		private int nextID;
		
		public OfferIDGenerator() {
			nextID = 1000;
		}
		
		public void setCurrentID(int id) {
			nextID=id;
		}
		
		public String getNextID() {
			return "offer" + nextID++;
		}
	}
}

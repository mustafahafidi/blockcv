package com.blockcv.model;

import java.util.Map;

import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.model.data.DataAccess;

public class ProposalsModel implements Model {
	
	public static class Proposal {
		private final String idProposal;
		private final String idWorker;
		private final String idExp;
		private final String idOrg;
		private final String expType;
		
		private final String senderName;
		private final String comment;
		private final String experienceTitle;
		private final String status;
		
		public Proposal(String idProposal, String idWorker, String idExp, String idOrg, String expType, String senderName, String comment, String experienceTitle, String status) {
			this.idProposal = idProposal;
			this.idWorker = idWorker;
			this.idExp = idExp;
			this.expType = expType;
			this.senderName = senderName;
			this.comment = comment;
			this.experienceTitle = experienceTitle;
			this.status = status;
			this.idOrg = idOrg;
		}

		public String getIdProposal() {
			return idProposal;
		}

		public String getIdWorker() {
			return idWorker;
		}

		public String getIdExp() {
			return idExp;
		}

		public String getExpType() {
			return expType;
		}

		public String getStatus() {
			return status;
		}

		public String getIdOrg() {
			return idOrg;
		}

		public String getSenderName() {
			return senderName;
		}
		
		public String getComment() {
			return comment;
		}
		
		public String getExperienceTitle() {
			return experienceTitle;
		}
	}
	
	private Map<String,Proposal> proposals;
	
	public void initialize(UserModel userModel) {
		proposals = DataAccess.getInstance().getProposals(userModel);
	}
	
	public Map<String,Proposal> getProposals() {
		return proposals;
	}
	
	public boolean acceptProposal(UserModel userModel, String propID) {
		boolean ok = DataAccess.getInstance().acceptProposal(userModel, propID);
		/*if(ok)
			removeProposal(propID);*/
		return ok;
	}
	
	public boolean rejectProposal(UserModel userModel, String propID, String motivation) {
		boolean ok = DataAccess.getInstance().rejectProposal(userModel, propID, motivation);
		/*if(ok)
			removeProposal(propID);*/
		return ok;
	}
	
	public void removeProposal(String id) {
		proposals.remove(id);
	}
	
	
	// TEST METHODS
	public void addProposal(UserModel userModel, Proposal proposal) {
		proposals.put(proposal.getIdProposal(), proposal);
		DataAccess.getInstance().requestCertifyExperience(userModel, proposal);
	}
}

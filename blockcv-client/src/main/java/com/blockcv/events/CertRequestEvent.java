package com.blockcv.events;

import com.blockcv.model.ManageCVModel;

public class CertRequestEvent {


	private final String idOrg;
	private final String expType;
	private final String selectedExpID;
	private final String experienceTitle;


	private final String comment;

	
	public CertRequestEvent(String idExp, String idOrg, String expType, String comment, String expTitle) {
		selectedExpID = idExp;
		this.idOrg = idOrg;
		this.expType = expType;
		this.experienceTitle = expTitle;
		this.comment = comment;
	}
	public String getComment() {
		return comment;
	}

	public String getIdOrg() {
		return idOrg;
	}

	public String getExpType() {
		return expType;
	}

	public String getExperienceTitle() {
		return experienceTitle;
	}
	public String getSelectedExpID() {
		return selectedExpID;
	}
}

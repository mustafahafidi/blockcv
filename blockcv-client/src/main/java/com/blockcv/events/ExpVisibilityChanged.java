package com.blockcv.events;

import com.blockcv.model.ManageCVModel.ExperienceType;

public class ExpVisibilityChanged {
	
	private boolean status;
	

	private ExperienceType exptype;
	private String expid;
	
	public ExpVisibilityChanged(ExperienceType exptype, String expid, boolean st) {
		this.status = st;
		this.exptype = exptype;
		this.expid = expid;
	}
	public boolean getStatus() {
		return status;
	}

	public ExperienceType getExptype() {
		return exptype;
	}

	public String getExpid() {
		return expid;
	}
}

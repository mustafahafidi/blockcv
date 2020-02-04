package com.blockcv.events;

public class CertAssignEvent {
	
	private final String cvID;
	private final String comment;
	
	public CertAssignEvent(String id, String c) {
		cvID = id;
		comment = c;
	}
	public String getCvID() {
		return cvID;
	}
	public String getComment() {
		return comment;
	}
}

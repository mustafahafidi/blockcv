package com.blockcv.events;

public class RejectProposalEvent {
	
	private final String propID;
	private final String motivation;
	
	public RejectProposalEvent(String id, String mot) {
		propID = id;
		motivation = mot;
	}
	public String getPropID() {
		return propID;
	}
	public String getMotivation() {
		return motivation;
	}
}
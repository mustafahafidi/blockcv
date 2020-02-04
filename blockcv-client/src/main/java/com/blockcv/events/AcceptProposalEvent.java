package com.blockcv.events;

public class AcceptProposalEvent {
	
	private final String propID;
	
	public AcceptProposalEvent(String id) {
		propID = id;
	}
	public String getPropID() {
		return propID;
	}
}
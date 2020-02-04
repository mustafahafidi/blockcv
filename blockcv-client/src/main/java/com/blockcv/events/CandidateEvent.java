package com.blockcv.events;

public class CandidateEvent {
	
	private final String selectedOffID;
	
	public CandidateEvent(String id) {
		selectedOffID = id;
	}
	public String getSelectedOffID() {
		return selectedOffID;
	}
}

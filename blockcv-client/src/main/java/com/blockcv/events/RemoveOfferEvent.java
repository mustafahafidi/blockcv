package com.blockcv.events;

public class RemoveOfferEvent {

	private final String selectedOffID;
	
	public RemoveOfferEvent(String id) {
		selectedOffID = id;
	}
	public String getSelectedOffID() {
		return selectedOffID;
	}
}

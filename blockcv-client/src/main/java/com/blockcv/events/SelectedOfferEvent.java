package com.blockcv.events;

public class SelectedOfferEvent {

	private final String selectedOffID;
	
	public SelectedOfferEvent(String id) {
		selectedOffID = id;
	}
	public String getSelectedOffID() {
		return selectedOffID;
	}
}

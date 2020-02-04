package com.blockcv.events;

public class RemoveExpEvent {

	private final String selectedExpID;
	
	public RemoveExpEvent(String id) {
		selectedExpID = id;
	}
	public String getSelectedExpID() {
		return selectedExpID;
	}
}

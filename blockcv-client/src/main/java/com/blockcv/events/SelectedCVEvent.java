package com.blockcv.events;

public class SelectedCVEvent {

	private final String selectedCVID;
	
	public SelectedCVEvent(String id) {
		selectedCVID = id;
	}
	public String getSelectedCVID() {
		return selectedCVID;
	}
}

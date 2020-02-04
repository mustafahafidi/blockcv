package com.blockcv.events;

import java.util.Map;

public class SaveCVEvent {
	
	private final Map<String,Boolean> visibility;
	
	public SaveCVEvent(Map<String,Boolean> vis) {
		visibility = vis;
	}
	public Map<String,Boolean> getVisibility() {
		return visibility;
	}
}

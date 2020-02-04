package com.blockcv.events;

import java.util.Map;

public class AddOfferEvent {

	private final Map<String,String> offData;
	
	public AddOfferEvent(Map<String,String> data) {
		offData = data;
	}
	public Map<String,String> getOffData() {
		return offData;
	}
}

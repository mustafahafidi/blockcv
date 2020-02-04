package com.blockcv.events;

import java.util.Map;

public class AddExpEvent {
	
	private final Map<String,String> expData;
	
	public AddExpEvent(Map<String,String> data) {
		expData = data;
	}
	public Map<String,String> getExpData() {
		return expData;
	}
}

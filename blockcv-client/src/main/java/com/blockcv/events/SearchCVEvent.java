package com.blockcv.events;

import java.util.Map;

public class SearchCVEvent {

	private final Map<String,String> filters;
	
	public SearchCVEvent(Map<String,String> filters) {
		this.filters = filters;
	}
	public Map<String,String> getFilters() {
		return filters;
	}
}
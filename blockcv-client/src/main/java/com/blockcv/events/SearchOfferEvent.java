package com.blockcv.events;

import java.util.Map;

public class SearchOfferEvent {

	private final Map<String,String> filters;
	
	public SearchOfferEvent(Map<String,String> filters) {
		this.filters = filters;
	}
	public Map<String,String> getFilters() {
		return filters;
	}
}

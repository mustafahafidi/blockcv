package com.blockcv.events;

public class NavigateEvent {
	
	private final String targetPage;
	
	public NavigateEvent(String pTarget) {
		targetPage = pTarget;
	}
	public String getTargetPage() {
		return targetPage;
	}
}

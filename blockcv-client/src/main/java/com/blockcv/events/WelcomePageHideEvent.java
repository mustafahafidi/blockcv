package com.blockcv.events;

import com.vaadin.navigator.ViewBeforeLeaveEvent;

public class WelcomePageHideEvent {
	private final ViewBeforeLeaveEvent event;
	public WelcomePageHideEvent(ViewBeforeLeaveEvent ev) {
		event = ev;
	}
	public ViewBeforeLeaveEvent getBeforeLeaveEvent() {
		return event;
	}
}

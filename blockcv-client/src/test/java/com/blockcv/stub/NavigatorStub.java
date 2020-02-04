package com.blockcv.stub;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

public class NavigatorStub extends Navigator {

	private String currentURI;
	
	public NavigatorStub(UI ui, ComponentContainer container) {
		super(ui, container);
	}
	
	@Override
	public void navigateTo(String navigationState) {
		currentURI = navigationState;
	}
	
	public String getCurrentURI() {
		return currentURI;
	}
}

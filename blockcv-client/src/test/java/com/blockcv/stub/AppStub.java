package com.blockcv.stub;

import com.blockcv.presenter.App;
import com.blockcv.presenter.MainPagePresenter;
import com.vaadin.navigator.Navigator;

public class AppStub extends App {
	
	@Override
	public Navigator generateNavigator(MainPagePresenter mainPresenter) {
    	return new NavigatorStub(this, mainPresenter.getContentContainer());
    }
	
}

package com.blockcv.presenter;

import javax.servlet.annotation.WebServlet;

import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.view.pages.HomePageView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.LoadingIndicatorConfiguration;
import com.vaadin.ui.UI;

@Theme("mytheme")
@PreserveOnRefresh
@Viewport("user-scalable=no,initial-scale=1.0")
public class App extends UI {
	
	public static final String FIRSTPAGE = HomePageView.URI;
	
	private MainPagePresenter mainPagePresenter;
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	LoadingIndicatorConfiguration x = getLoadingIndicatorConfiguration();
    	x.setFirstDelay(300);
    	x.setSecondDelay(1500);
    	x.setThirdDelay(5000);
    	
    	setStyleName("blockcv");
    	setMainPagePresenter();
    	
    	getPage().setTitle("BlockCV Web App");
    }
    
    public Navigator generateNavigator(MainPagePresenter mainPresenter) {
    	return new Navigator(this, mainPresenter.getContentContainer());
    }
    
    public void setMainPagePresenter() {
		mainPagePresenter = new MainPagePresenter(this);
	}
    
    public MainPagePresenter getMainPagePresenter() {
		return mainPagePresenter;
	}
    
    // Main servlet
    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false , ui = App.class)
    public static class MainServlet extends VaadinServlet {}
    
}

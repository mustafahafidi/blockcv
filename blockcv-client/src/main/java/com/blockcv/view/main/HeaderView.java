package com.blockcv.view.main;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.LogoutEvent;
import com.blockcv.events.NavigateEvent;
import com.blockcv.view.pages.HomePageView;
import com.blockcv.view.pages.LoginPageView;
import com.blockcv.view.pages.SignupPageView;
import com.github.appreciated.material.MaterialTheme;
import com.blockcv.view.pages.ManageCVPageView;
import com.blockcv.view.pages.SearchCVPageView;
import com.blockcv.view.pages.ManageOffersPageView;
import com.blockcv.view.pages.ProposalsPageView;
import com.blockcv.view.pages.SearchOffersPageView;
import com.blockcv.view.View;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;

public class HeaderView extends CssLayout implements View {
	
	private EventBus eventBus;
	
	private final MenuBar menu;
	
	public HeaderView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		menu = new MenuBar();
		//setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		menu.addItem(HomePageView.MENU_NAME, getCommand(HomePageView.URI));
		menu.addItem(LoginPageView.MENU_NAME, getCommand(LoginPageView.URI));
		menu.addItem(SignupPageView.MENU_NAME, getCommand(SignupPageView.URI));
		menu.setStyleName(MaterialTheme.MENUBAR_PRIMARY);
		setResponsive(true);
		Responsive.makeResponsive(this);
		/*menu.setWidth("100%");*/
		//setWidth("100%");  menu.setWidth("100%");
		
		addComponent(menu);
	}
	
	private Command getCommand(String uri) {
		return (m) -> eventBus.post(new NavigateEvent(uri));
	}
	
	public void setInitialMenu() {
		menu.removeItems();
		menu.addItem(HomePageView.MENU_NAME, getCommand(HomePageView.URI));
		menu.addItem(LoginPageView.MENU_NAME, getCommand(LoginPageView.URI));
		menu.addItem(SignupPageView.MENU_NAME, getCommand(SignupPageView.URI));
	} 
	
	public void setWorkerMenu() {
		menu.removeItems();
		menu.addItem(HomePageView.MENU_NAME, getCommand(HomePageView.URI));
		menu.addItem(ManageCVPageView.MENU_NAME, getCommand(ManageCVPageView.URI));
		menu.addItem(SearchOffersPageView.MENU_NAME, getCommand(SearchOffersPageView.URI));
		//menu.addItem(ProposalsPageView.MENU_NAME, getCommand(ProposalsPageView.URI));
		menu.addItem("Logout", event -> eventBus.post(new LogoutEvent()));
	}
	
	public void setOrganizationMenu() {
		menu.removeItems();
		menu.addItem(HomePageView.MENU_NAME, getCommand(HomePageView.URI));
		menu.addItem(ManageOffersPageView.MENU_NAME, getCommand(ManageOffersPageView.URI));
		menu.addItem(SearchCVPageView.MENU_NAME, getCommand(SearchCVPageView.URI));
		menu.addItem(ProposalsPageView.MENU_NAME, getCommand(ProposalsPageView.URI));
		menu.addItem("Logout", event -> eventBus.post(new LogoutEvent()));
	}

	public void setWelcomeView() {
		/*menu.setStyleName("");
		menu.addStyleNames(MaterialTheme.MENUBAR_BORDERLESS);*/
		menu.setVisible(false);
	}
	public void unsetWelcomeView() {
		//menu.setStyleName(MaterialTheme.MENUBAR_PRIMARY);
		menu.setVisible(true);
	}
	
	
	// TEST GETTERS
	
	public MenuBar getMenu() {
		return menu;
	}

}

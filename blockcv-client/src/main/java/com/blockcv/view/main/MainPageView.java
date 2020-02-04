package com.blockcv.view.main;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.HomePageView;
import com.blockcv.view.pages.ManageCVPageView;
import com.blockcv.view.pages.ManageOffersPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

public class MainPageView extends VerticalLayout implements NavigableView {
	
	public static final String DASHBOARD_WORKER = ManageCVPageView.URI;
	public static final String DASHBOARD_ORGANIZATION = ManageOffersPageView.URI;
	public static final String HOMEPAGE = HomePageView.URI;
	
	private HeaderView headerView;
	private ContentView contentView;
	private FooterView footerView;
	
	private final String initialStyle = "v-scrollable scrollable mainPageView "+MaterialTheme.CARD_0;
	
	public MainPageView(EventBus eventBus) {
		headerView = new HeaderView(eventBus);
		contentView = new ContentView();
		footerView = new FooterView();	
		
		//setSizeFull();
		
		addComponents(headerView, contentView, footerView);
    	
		//contentView.setPrimaryStyleName("valo-content");

		contentView.addStyleNames("v-scrollable",MaterialTheme.CARD_0);
    	//setExpandRatio(contentView, 1);
		setStyleName(initialStyle);
		
		setComponentAlignment(headerView, Alignment.MIDDLE_CENTER);
		setComponentAlignment(contentView, Alignment.MIDDLE_CENTER);
		setComponentAlignment(footerView, Alignment.BOTTOM_CENTER);
	}
	
	public ContentView getContentView() {
		return contentView;
	}
	
	public void setInitialHeader() {
		headerView.setInitialMenu();
	}
	
	public void setWorkerHeader() {
		headerView.setWorkerMenu();
	}
	
	public void setOrgHeader() {
		headerView.setOrganizationMenu();
	}
	
	public void setWelcomePage() {
		headerView.setWelcomeView();
		contentView.setWelcomeView();
		footerView.setWelcomeView();
		setStyleName("homePageBackground");
	}
	
	public void unsetWelcomePage() {
		headerView.unsetWelcomeView();
		contentView.unsetWelcomeView();
		footerView.unsetWelcomeView();
		setStyleName(initialStyle);
	}
	
	
	@Override
	public String getUriFragment() {
		return null;
	}

	@Override
	public String getMenuName() {
		return null;
	}
	

	// TEST GETTERS
	
	public HeaderView getHeaderView() {
		return headerView;
	}
	
	public FooterView getFooterView() {
		return footerView;
	}
	
}

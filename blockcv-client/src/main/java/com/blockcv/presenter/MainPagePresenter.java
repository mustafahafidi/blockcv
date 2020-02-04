package com.blockcv.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blockcv.view.pages.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.ChangedOrgEvent;
import com.blockcv.events.ChangedWorkerEvent;
import com.blockcv.events.NavigateEvent;
import com.blockcv.model.UserModel;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.view.NavigableView;
import com.blockcv.view.main.ContentView;
import com.blockcv.view.main.MainPageView;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

public class MainPagePresenter implements Presenter {
	
	private static App ui;
	private EventBus mainEventBus;
	
	private MainPageView mainPageView;
	private Navigator navigator;
	private List<Presenter> presenters;
	
	private UserModel userModel;
	
	public MainPagePresenter(App ui) {
		
		MainPagePresenter.ui = ui;
		mainEventBus = new EventBus();
    	mainEventBus.register(this);//register for events
    	
		mainPageView = new MainPageView(mainEventBus);
		navigator = ui.generateNavigator(this);
    	presenters = new ArrayList<Presenter>();
    	userModel = new UserModel();
    	
    	presenters.add(new HomePresenter(this));
    	presenters.add(new LoginPresenter(this));
    	presenters.add(new ManageCVPresenter(this));
    	presenters.add(new ManageOffersPresenter(this));
    	presenters.add(new ProposalsPresenter(this));
    	presenters.add(new SearchCVPresenter(this));
    	presenters.add(new SearchOffersPresenter(this));
    	presenters.add(new SignupPresenter(this));
    	
    	presenters.forEach((presenter) -> {
    		navigator.addView(presenter.getPageView().getUriFragment(), presenter.getPageView());
    	});

    	ui.setContent(mainPageView);
    	navigator.navigateTo(App.FIRSTPAGE);
    	
    	//been undeprecated for nonreplacement by vaadin
    	ui.getPage().addUriFragmentChangedListener(new Page.UriFragmentChangedListener() {
			@Override
			public void uriFragmentChanged(UriFragmentChangedEvent event) {
				onNavigateEvent(new NavigateEvent(event.getUriFragment().replace("!","")));
			}
		});
	}
	
	public UserModel getUserModel() {
		return userModel;
	}
	
	public EventBus getEventBus() {
		return mainEventBus;
	}
	
	public void setWelcomeView() {
		mainPageView.setWelcomePage();
	}
	
	public void unsetWelcomeView() {
		mainPageView.unsetWelcomePage();
	}
	
	public void setLoggedInView(UserType userType) {
		if(userType==UserType.ORGANIZATION) {
			mainPageView.setOrgHeader();
			mainEventBus.post(new ChangedOrgEvent());
			navigator.navigateTo(ManageOffersPageView.URI);
		} else {
			mainPageView.setWorkerHeader();
			navigator.navigateTo(ManageCVPageView.URI);
			mainEventBus.post(new ChangedWorkerEvent());
		}
	}
	
	public void setLoggedOutView() {
		navigator = ui.generateNavigator(this);
		presenters.forEach(presenter -> {presenter.clean(); navigator.addView(presenter.getPageView().getUriFragment(), presenter.getPageView());});
		navigator.navigateTo(App.FIRSTPAGE);
		mainPageView.setInitialHeader();
		userModel.reset();
	}
	
	public static Notification showNotification(String message, Notification.Type type) {
		Position position = Position.BOTTOM_LEFT;//(type==Type.WARNING_MESSAGE ||
							// type==Type.ERROR_MESSAGE) ? Position.BOTTOM_LEFT : Position.BOTTOM_RIGHT;
		int delay = 5000;
		
		Notification notif = new Notification(message, type);
		notif.setDelayMsec(delay);
		notif.setPosition(position);
		notif.show(ui.getPage());
		return notif;
	}
	
	public ContentView getContentContainer() {
		return mainPageView.getContentView();
	}
	
	public List<Presenter> getPresenters() {
		return presenters;
	}
	
	@Override
	public NavigableView getPageView() {
		return mainPageView;
	}

	@Override
	public void clean() {}
	
	/* event subscribes */ 
	
	@Subscribe
    public void onNavigateEvent(NavigateEvent ev) {
		String[] notLoggedInPages = new String[] { LoginPageView.URI,
												   SignupPageView.URI,
												   HomePageView.URI
												   };
		if(Arrays.asList(notLoggedInPages).contains(ev.getTargetPage()) && userModel.isLoggedIn()) {
			if(ev.getTargetPage().equals(ProposalsPageView.URI) && userModel.getUserType()==UserType.ORGANIZATION)
				navigator.navigateTo(ev.getTargetPage());
			else
				navigator.navigateTo((userModel.getUserType() == UserType.WORKER) ? MainPageView.DASHBOARD_WORKER : MainPageView.DASHBOARD_ORGANIZATION);

		} else
			navigator.navigateTo(ev.getTargetPage());


		
    }
	
}

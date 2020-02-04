package com.blockcv.presenter;

import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.WelcomePageHideEvent;
import com.blockcv.events.WelcomePageShowEvent;
import com.blockcv.model.HomeModel;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.HomePageView;

public class HomePresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private HomePageView homeView;
	private HomeModel homeModel;
	
	public HomePresenter(MainPagePresenter pMainPresenter) {
		mainPresenter = pMainPresenter;
		mainPresenter.getEventBus().register(this);
		
		homeView = new HomePageView(mainPresenter.getEventBus());
		homeModel = new HomeModel();
	}

	@Override
	public NavigableView getPageView() {
		return homeView;
	}

	@Override
	public void clean() {
		homeView = new HomePageView(mainPresenter.getEventBus());
		homeModel = new HomeModel();
	}
	
	@Subscribe
	public void onWelcomePageShowEvent(WelcomePageShowEvent ev) {
		mainPresenter.setWelcomeView();
	}
	
	@Subscribe
	public void onWelcomePageHideEvent(WelcomePageHideEvent ev) {
		mainPresenter.unsetWelcomeView();
		ev.getBeforeLeaveEvent().navigate();
	}
}

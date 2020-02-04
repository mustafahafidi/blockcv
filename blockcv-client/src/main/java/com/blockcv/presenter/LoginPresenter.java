package com.blockcv.presenter;

import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.LogoutEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.UserModel;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.LoginPageView;
import com.vaadin.ui.Notification.Type;

public class LoginPresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private LoginPageView loginView;
	
	public LoginPresenter(MainPagePresenter mainPresenter) {
		this.mainPresenter = mainPresenter;
		mainPresenter.getEventBus().register(this);
		
		loginView = new LoginPageView(mainPresenter.getEventBus());
	}

	@Subscribe
	public void onTryLoginEvent(TryLoginEvent ev) {

		UserModel userModel = mainPresenter.getUserModel();
		boolean loggedIn = userModel.setEmail(ev.getEmail()).setPassword(ev.getPassword()).tryLogin();
		
		if(loggedIn) {
			mainPresenter.setLoggedInView(userModel.getUserType());
		} else {
			MainPagePresenter.showNotification("Dati forniti errati, riprovare.", Type.WARNING_MESSAGE);//loginView.showNotification("Dati forniti errati, riprovare.");
		}
	}
	
	@Subscribe
    public void onLogoutEvent(LogoutEvent ev) {
		mainPresenter.setLoggedOutView();
    }
	
	@Override
	public NavigableView getPageView() {
		return loginView;
	}

	@Override
	public void clean() {
		loginView = new LoginPageView(mainPresenter.getEventBus());
	}
}

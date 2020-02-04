package com.blockcv.presenter;

import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.NavigateEvent;
import com.blockcv.events.TrySignupEvent;
import com.blockcv.model.UserModel;
import com.blockcv.view.pages.LoginPageView;
import com.blockcv.view.pages.SignupPageView;
import com.vaadin.ui.Notification.Type;
import com.blockcv.view.NavigableView;

public class SignupPresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private SignupPageView signupView;
	
	public SignupPresenter(MainPagePresenter mainPresenter) {
		this.mainPresenter = mainPresenter;
		mainPresenter.getEventBus().register(this);
		
		signupView = new SignupPageView(mainPresenter.getEventBus());
	}

	@Subscribe
	public void onTrySignupEvent(TrySignupEvent ev) {
		
		if(!(ev.getRepeat().equals(ev.getPassword()))) {
			MainPagePresenter.showNotification("Le password inserite non corrispondono", Type.WARNING_MESSAGE);
		}
		else if(UserModel.trySignup(ev.getEmail(), ev.getPassword(), ev.getInfo(), ev.getUserType())) {
			MainPagePresenter.showNotification("Registrazione avvenuta correttamente", Type.HUMANIZED_MESSAGE);
			mainPresenter.getEventBus().post(new NavigateEvent(LoginPageView.URI));
			//mainPresenter.setLoggedInView(userModel.getUserType());
		}
		else
			MainPagePresenter.showNotification("Errore nella registrazione, utente già presente", Type.WARNING_MESSAGE);
	}
	
	@Override
	public NavigableView getPageView() {
		return signupView;
	}

	@Override
	public void clean() {
		signupView = new SignupPageView(mainPresenter.getEventBus());
	}
}
package com.blockcv.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.CandidateEvent;
import com.blockcv.events.SearchOfferEvent;
import com.blockcv.model.SearchOffersModel;
import com.blockcv.model.SearchOffersModel.WorkOffer;
import com.blockcv.view.NavigableView;
import com.blockcv.view.offer.OfferView;
import com.blockcv.view.pages.SearchOffersPageView;

public class SearchOffersPresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private SearchOffersPageView searchOffersView;
	private SearchOffersModel searchOffersModel;
	
	public SearchOffersPresenter(MainPagePresenter mainPagePresenter) {
		
		mainPresenter = mainPagePresenter;
		mainPresenter.getEventBus().register(this);
		
		searchOffersView = new SearchOffersPageView(mainPresenter.getEventBus());
		searchOffersModel = new SearchOffersModel();
	}

	@Subscribe
	public void onSearchOfferEvent(SearchOfferEvent ev) {
		Map<String,WorkOffer> offers = searchOffersModel.getFilteredOffers(mainPresenter.getUserModel(), ev.getFilters());
		if(offers.size()!=0) {
			List<OfferView> offerViews = new ArrayList<>();
			offers.forEach((id,o) -> offerViews.add(new OfferView(id, searchOffersView, o.getCompany(), o.getTitle(),
				1, o.getEmploymentSector(), o.getWorkFunction(), o.getRequiredStudyTitle(),
				o.getRequiredCert(), o.getExpirationDate(), o.getContractType(), o.getSalaryRange(), o.getDescription(), o.alreadyCandidated(),mainPresenter.getEventBus())));
			searchOffersView.setOffers(offerViews);
			MainPagePresenter.showNotification("Scorri per visualizzare i risultati", Type.HUMANIZED_MESSAGE);
		}
		else MainPagePresenter.showNotification("Nessuna offerta trovata", Type.WARNING_MESSAGE);
	}
	
	@Subscribe
	public void onCandidateEvent(CandidateEvent ev) {
		if(searchOffersModel.candidateToOffer(mainPresenter.getUserModel(), ev.getSelectedOffID()))
			MainPagePresenter.showNotification("Candidatura avvenuta correttamente", Type.HUMANIZED_MESSAGE);
		else
			MainPagePresenter.showNotification("Errore candidatura non avvenuta", Notification.Type.ERROR_MESSAGE);
	}
	
	@Override
	public NavigableView getPageView() {
		return searchOffersView;
	}

	@Override
	public void clean() {
		searchOffersView = new SearchOffersPageView(mainPresenter.getEventBus());
		searchOffersModel = new SearchOffersModel();
	}
	
	
	// TEST GETTERS
	
	public SearchOffersModel getSearchOffersModel() {
		return searchOffersModel;
	}
	
}

package com.blockcv.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.AddOfferEvent;
import com.blockcv.events.ChangedOrgEvent;
import com.blockcv.events.RemoveOfferEvent;
import com.blockcv.events.SaveOffersEvent;
import com.blockcv.events.SelectedOfferEvent;
import com.blockcv.model.ManageOffersModel;
import com.blockcv.model.ManageOffersModel.Offer;
import com.blockcv.model.ManageOffersModel.Offer.Candidate;
import com.blockcv.model.UserModel;
import com.blockcv.view.NavigableView;
import com.blockcv.view.offer.CandidateView;
import com.blockcv.view.offer.OfferView;
import com.blockcv.view.pages.ManageOffersPageView;
import com.vaadin.ui.Notification.Type;

public class ManageOffersPresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private ManageOffersPageView manageOffersView;
	private ManageOffersModel manageOffersModel;
	
	public ManageOffersPresenter(MainPagePresenter mainPagePresenter) {
		
		mainPresenter = mainPagePresenter;
		mainPresenter.getEventBus().register(this);
		
		manageOffersView = new ManageOffersPageView(mainPresenter.getEventBus());
		manageOffersModel = new ManageOffersModel();
	}

	@Subscribe
	public void onChangedOrgEvent(ChangedOrgEvent ev) {
		// MODEL & VIEW INITIALIZATION
		manageOffersModel.initialize(mainPresenter.getUserModel());
		manageOffersModel.getOffers().forEach((id,v) -> manageOffersView.addOffer(new OfferView(id, manageOffersView, "", v.getTitle(), v.getMaxCandidates(), v.getEmploymentSector(), v.getWorkFunction(), v.getRequiredStudyTitle(), v.getRequiredCert(), v.getExpirationDate(), v.getContractType(), v.getSalaryRange(), v.getDescription(), true, mainPresenter.getEventBus())));

	}
	
	@Subscribe
	public void onSelectedOfferEvent(SelectedOfferEvent ev) {
		List<Candidate> candidates = manageOffersModel.getCandidates(ev.getSelectedOffID());
		candidates.forEach(c -> manageOffersView.addCandidate(new CandidateView(c.getName(), c.getAddress(), c.getEmail())));
	}
	
	@Subscribe
	public void onAddOfferEvent(AddOfferEvent ev) {
		Map<String,String> o = ev.getOffData();
		String offID = manageOffersModel.addNewOffer(new Offer("2345678",
																mainPresenter.getUserModel().getId(), 
																mainPresenter.getUserModel().getPersonalInfo().get("orgName"), 
																o.get("title"), 
																Integer.valueOf(o.get("maxCandidates")), 
																o.get("employmentSector"),
																o.get("workFunction"),
																o.get("requiredStudyTitle"), 
																o.get("requiredCert"), 
																o.get("expirationDate"), 
																o.get("contractType"), 
																o.get("salaryRange"), 
																o.get("description"), 
																new ArrayList<Candidate>()));
		manageOffersView.addOffer(new OfferView(offID, manageOffersView, "", o.get("title"), Integer.parseInt(o.get("maxCandidates")), o.get("employmentSector"), o.get("workFunction"), o.get("requiredStudyTitle"), o.get("requiredCert"), o.get("expirationDate"), o.get("contractType"), o.get("salaryRange"), o.get("description"), true, mainPresenter.getEventBus()));
		manageOffersView.emptyOfferView();
	}
	
	@Subscribe
	public void onRemoveOfferEvent(RemoveOfferEvent ev) {
		manageOffersView.removeSelectedOff();
		String offID = ev.getSelectedOffID();
		manageOffersModel.removeOffer(offID);
	}
	
	@Subscribe
	public void onSaveOffersEvent(SaveOffersEvent ev) {
		UserModel userModel = mainPresenter.getUserModel();
		if(manageOffersModel.saveOffers(userModel))
			MainPagePresenter.showNotification("Offerte salvate correttamente", Type.HUMANIZED_MESSAGE);
		else
			MainPagePresenter.showNotification("Errore salvataggio offerte", Type.ERROR_MESSAGE);
	}
	
	@Override
	public NavigableView getPageView() {
		return manageOffersView;
	}

	@Override
	public void clean() {
		manageOffersView = new ManageOffersPageView(mainPresenter.getEventBus());
		manageOffersModel = new ManageOffersModel();
	}
	
	
	// TEST GETTERS
	
	public ManageOffersModel getManageOffersModel() {
		return manageOffersModel;
	}
	
}

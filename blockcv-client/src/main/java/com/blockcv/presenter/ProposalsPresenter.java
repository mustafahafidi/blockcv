package com.blockcv.presenter;

import com.blockcv.events.ChangedOrgEvent;
import com.vaadin.ui.Component;
import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.AcceptProposalEvent;
import com.blockcv.events.ChangedUserEvent;
import com.blockcv.events.RejectProposalEvent;
import com.blockcv.model.ProposalsModel;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.view.NavigableView;
import com.blockcv.view.curriculum.ProposalView;
import com.blockcv.view.pages.ProposalsPageView;
import com.vaadin.ui.Notification.Type;

public class ProposalsPresenter implements Presenter {

	private MainPagePresenter mainPresenter;
	private ProposalsPageView proposalsView;
	private ProposalsModel proposalsModel;
	
	public ProposalsPresenter(MainPagePresenter mainPagePresenter) {
		
		this.mainPresenter = mainPagePresenter;
		mainPresenter.getEventBus().register(this);
		
		proposalsView = new ProposalsPageView(mainPresenter.getEventBus());
		proposalsModel = new ProposalsModel();
	}

	@Subscribe
	public void onChangedOrgEvent(ChangedOrgEvent ev){
		// MODEL & VIEW INITIALIZATION
		if(mainPresenter.getUserModel().getUserType() == UserType.ORGANIZATION) {
			proposalsModel.initialize(mainPresenter.getUserModel());
			proposalsModel.getProposals().forEach((id,v) -> proposalsView.addProposal(new ProposalView(id, proposalsView, v.getSenderName(), v.getExperienceTitle(), v.getComment(), v.getStatus())));
		}
	}
	
	@Subscribe
	public void onAcceptProposalEvent(AcceptProposalEvent ev) {
		if(proposalsModel.acceptProposal(mainPresenter.getUserModel(), ev.getPropID())) {
			MainPagePresenter.showNotification("Proposta accettata", Type.HUMANIZED_MESSAGE);
			for (Component component : proposalsView.getProposals()) {
				ProposalView proposal = (ProposalView)component;
				if(proposal.getPropId().equals(ev.getPropID()))
					proposal.changeStatus(true);
			}
		}else
			MainPagePresenter.showNotification("Errore nell'accettazione", Type.ERROR_MESSAGE);
	}
	
	@Subscribe 
	public void onRejectProposalEvent(RejectProposalEvent ev) {
		if(proposalsModel.rejectProposal(mainPresenter.getUserModel(), ev.getPropID(), ev.getMotivation())) {
			MainPagePresenter.showNotification("Proposta rifiutata", Type.HUMANIZED_MESSAGE);
			for (Component component : proposalsView.getProposals()) {
				ProposalView proposal = (ProposalView) component;
				if (proposal.getPropId().equals(ev.getPropID()))
					proposal.changeStatus(false);
			}
		} else
			MainPagePresenter.showNotification("Errore nel rifiuto", Type.ERROR_MESSAGE);
	}

	/*public void refreshProposals() {
		//proposalsView.getProposals().removeAllComponents();

		//proposalsModel.initialize(mainPresenter.getUserModel());
		//proposalsModel.getProposals().forEach((id,v) -> proposalsView.addProposal(new ProposalView(id, proposalsView, v.getSenderName(), v.getExperienceTitle(), v.getComment(), v.getStatus())));

	}*/
	@Override
	public NavigableView getPageView() {
		return proposalsView;
	}
	
	@Override
	public void clean() {
		proposalsView = new ProposalsPageView(mainPresenter.getEventBus());
		proposalsModel = new ProposalsModel();
	}
	
	
	// TEST GETTERS
	
	public ProposalsModel getProposalsModel() {
		return proposalsModel;
	}
	
}

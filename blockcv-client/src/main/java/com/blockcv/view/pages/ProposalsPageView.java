package com.blockcv.view.pages;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.view.NavigableView;
import com.blockcv.view.curriculum.ProposalView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ProposalsPageView extends VerticalLayout implements NavigableView {

	public static final String URI = "proposals";
	public static final String MENU_NAME = "Proposte di certificazione";
	
	private EventBus eventBus;
	
	private final Label pageTitle;
	private final VerticalLayout proposals;
	
	public ProposalsPageView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		pageTitle = new Label("PROPOSTE DI CERTIFICAZIONE");
		proposals = new VerticalLayout();
		
		pageTitle.setStyleName("pageTitle");
		addComponents(pageTitle, new Label("Visualizza le proposte di certificazione. Puoi accettarle oppure rifiutarle inserendo una motivazione"), proposals);
	}
	
	public void addProposal(ProposalView pw) {
		proposals.addComponent(pw);
	}
	
	public void removeProposal(ProposalView pw) {
		proposals.removeComponent(pw);
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}
	
	@Override
	public String getUriFragment() {
		return URI;
	}

	@Override
	public String getMenuName() {
		return MENU_NAME;
	}
	
	
	// TEST GETTERS
	
	public VerticalLayout getProposals() {
		return proposals;
	}
	
}
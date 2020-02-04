package com.blockcv.view.curriculum;

import com.blockcv.events.AcceptProposalEvent;
import com.blockcv.events.RejectProposalEvent;
import com.blockcv.view.View;
import com.blockcv.view.pages.ProposalsPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ProposalView extends HorizontalLayout implements View {
	
	public static class ConfirmWindow extends Window {
		public ConfirmWindow(TextArea mot, Button confirm) {
			super("Conferma rifiuto");
			setContent(new VerticalLayout(mot, confirm));
			center();
			setModal(true);
		}
	}
	
	private final ProposalsPageView parent;
	
	private final Label description;
	private final Label sender;
	private final Label comment;
	private final Button acceptButton;
	private final Button refuseButton;
	
	private final ConfirmWindow confirmWindow;
	private final Button confirmButton;
	private final TextArea motivation;
	HorizontalLayout right;
	String propID;
	
	public ProposalView(String propID, ProposalsPageView view, String sender, String expTitle, String comment, String status) {
		this.propID = propID;
		setId(propID);
		parent = view;
		
		description = new Label("Proposta di certificazione per l'esperienza " + expTitle);
		this.sender = new Label(sender);
		this.sender.setCaption("Mittente:");
		this.comment = new Label(comment);
		this.comment.setCaption("Commento del mittente:");

		motivation = new TextArea();
		motivation.setPlaceholder("Motivazione in caso di rifiuto");
		acceptButton = new Button("Accetta", event -> {
			parent.getEventBus().post(new AcceptProposalEvent(getId()));
			//parent.removeProposal(this);
		});
		confirmButton = new Button("Conferma");
		confirmWindow = new ConfirmWindow(motivation, confirmButton);
		
		refuseButton = new Button("Rifiuta", event -> UI.getCurrent().addWindow(confirmWindow));
		confirmButton.addListener(event -> {
			parent.getEventBus().post(new RejectProposalEvent(getId(), motivation.getValue()));
			//parent.removeComponent(this);
			confirmWindow.close();
		});
		
		addStyleName(MaterialTheme.CARD_2);
		VerticalLayout left = new VerticalLayout(description, this.sender, this.comment);

		if(status.equals("pending"))
			 right = new HorizontalLayout(acceptButton, refuseButton);
		else if(status.equals("approved"))
			right = new HorizontalLayout(new Label("Proposta Accettata"));
		else
			right = new HorizontalLayout(new Label("Proposta Rifiutata"));
		addComponents(left, right);
		setComponentAlignment(left, Alignment.MIDDLE_LEFT);
		setComponentAlignment(right, Alignment.MIDDLE_RIGHT);
		setWidth(800, Unit.PIXELS);
	}

	public String getPropId(){
		return propID;
	}

	public void changeStatus(boolean approved) {
		right.removeAllComponents();
		if(approved) {
			right.addComponent(new Label("Proposta Accettata"));
		} else
			right.addComponent(new Label("Proposta Rifiutata"));
	}
}

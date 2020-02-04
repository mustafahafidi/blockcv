package com.blockcv.view.offer;

import java.util.Arrays;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.SelectedOfferEvent;
import com.blockcv.view.pages.ManageOffersPageView;
import com.blockcv.view.pages.SearchOffersPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OfferView extends VerticalLayout implements com.blockcv.view.View {

	private final View parent;
	
	private final Label company;
	private final Label title;
	private final Label maxCandidates;
	private final Label employmentSector;
	private final Label workFunction;
	private final Label requiredStudyTitle;
	private final Label requiredCert;
	private final Label expirationDate;
	private final Label contractType;
	private final Label salaryRange;
	private final Label description;
	private final boolean candidable;
	private final EventBus eventBus;

	public OfferView(String id, View view, String c, String t, int mc, String es, String wf, String rst, String rc, String ed, String ct, String sr, String d, boolean alreadyCandidated, EventBus eventBus) {
		this.eventBus = eventBus;
		setId(id);
		parent = view;
		company = new Label(c); company.setStyleName("offerCompany");
		title = new Label(t); title.setStyleName("offerTitle");
		maxCandidates = new Label(String.valueOf(mc)); maxCandidates.setCaption("N. massimo di candidati");
		employmentSector = new Label(es); employmentSector.setCaption("Settore di impiego");
		workFunction = new Label(wf); workFunction.setCaption("Funzione lavorativa");
		requiredStudyTitle = new Label(rst); requiredStudyTitle.setCaption("Titolo di studio richiesto");
		requiredCert = new Label(rc); requiredCert.setCaption("Certificazione richiesta");
		expirationDate = new Label(ed); expirationDate.setCaption("Data di scadenza");
		contractType = new Label(ct); contractType.setCaption("Tipo di contratto");
		salaryRange = new Label(sr); salaryRange.setCaption("Range dello stipendio");
		description = new Label(d); description.setCaption("Descrizione");
		candidable = !alreadyCandidated;
		
		title.setWidth("100%");
		description.setWidth("100%");
		addComponents(company, title, maxCandidates, new HorizontalLayout(employmentSector, workFunction), requiredStudyTitle, requiredCert, expirationDate, contractType, salaryRange, description);
		addStyleName(MaterialTheme.CARD_1);
		// SearchOffersPageView
		if(parent instanceof SearchOffersPageView) {
			maxCandidates.setVisible(false);
			SearchOffersPageView p = (SearchOffersPageView)parent;
			addLayoutClickListener(event -> {
				p.setCandidate(company.getValue(), title.getValue(), getId(), candidable);
				p.deselectOffers();
				addStyleName("selectedOffer");
			});
		}
		else { // ManageOffersPageView
			company.setVisible(false);
			ManageOffersPageView p = (ManageOffersPageView)parent;
			addLayoutClickListener(event -> {
				p.setAddOfferViewFields(Arrays.asList(title.getValue(), maxCandidates.getValue(), employmentSector.getValue(), workFunction.getValue(), requiredStudyTitle.getValue(), requiredCert.getValue(), expirationDate.getValue(), contractType.getValue(), salaryRange.getValue(), description.getValue()));
				p.deselectOffers();
				p.setAddMode(false);
				p.setSelectedOff(this);
				addStyleName("selectedOffer");
				eventBus.post(new SelectedOfferEvent(getId()));
			});
		}
	}
}

package com.blockcv.view.pages;

import java.time.LocalDate;
import java.util.*;

import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.CandidateEvent;
import com.blockcv.events.SearchOfferEvent;
import com.blockcv.presenter.MainPagePresenter;
import com.blockcv.view.NavigableView;
import com.blockcv.view.offer.OfferView;
import com.github.appreciated.material.MaterialTheme;

public class SearchOffersPageView extends VerticalLayout implements NavigableView{
	
	public static String URI = "searchoffers";
	public static String MENU_NAME = "Ricerca offerte";
	
	private EventBus eventBus;
	
	private Label pageTitle;
	private Label description;
	
	private VerticalLayout filter;
	private TextField keywords;
	private TextField location;
	private CheckBox qualification; // da cambiare
	private ComboBox<String> contractType;
	private TextField scope;
	private TextField job;
	private DateField expirationDate;
	private Button searchButton;
	
	private VerticalLayout candidate;
	private Label selectedOffer;
	private Button candidateButton;
	private PopupView popup;
	
	private VerticalLayout offers;
	
	public SearchOffersPageView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		pageTitle = new Label("RICERCA OFFERTE LAVORATIVE");
		pageTitle.setStyleName("pageTitle");
		description=new Label("Visualizza le offerte disponibili, filtrandole in base alle tue esigenze. Seleziona un'offerta per candidarti.");
		
		constructFilter();
		constructPopup();
		
		offers = new VerticalLayout();
		offers.setVisible(false);
		offers.addStyleName(MaterialTheme.CARD_2);
		
		addComponents(pageTitle, description, filter, popup, offers);
		setComponentAlignment(popup, Alignment.MIDDLE_CENTER);
	}

	public Map<String,String> getFilters() {
		Map<String,String> filters = new HashMap<>();
		if(!keywords.isEmpty())
			filters.put("keywords", keywords.getValue());
		if(!location.isEmpty())
			filters.put("location", location.getValue());
		if(!scope.isEmpty())
			filters.put("scope",scope.getValue());
		if(!job.isEmpty())
			filters.put("job",job.getValue());
		if(qualification.getValue())
			filters.put("qualification","yes");
		if(!contractType.isEmpty())
			filters.put("contractType", contractType.getValue());
		if(!expirationDate.isEmpty())
			filters.put("expirationDate", expirationDate.getValue().toString());
		return filters;
	}
	
	public void setOffers(List<OfferView> off) {
		removeComponent(offers);
		offers.removeAllComponents();
		off.iterator().forEachRemaining(component -> {
			offers.addComponent(component);
			component.addStyleNames(MaterialTheme.CARD_3, MaterialTheme.CARD_HOVERABLE);
		});
		addComponent(offers);
		offers.setVisible(true);
	}
	
	public void setCandidate(String company, String title, String id, boolean candidable) {
		selectedOffer.setValue(title + ", " + company);
		selectedOffer.setId(id);
		if(candidable) {
			popup.setVisible(true); popup.setPopupVisible(true); }
		else
			MainPagePresenter.showNotification("Ti sei già candidato a questa offerta", Type.WARNING_MESSAGE);
	}
	
	public void deselectOffers() {
		offers.iterator().forEachRemaining(component -> {
			component.removeStyleName("selectedOffer");
			component.setEnabled(false);
		});
	}
	
	private void constructFilter() {
		keywords = new TextField("Titolo richiesto, settore d'impiego o funzione lavorativa");
		keywords.setPlaceholder("Es. Diploma superiore");
		location = new TextField("Località");
		location.setPlaceholder("Es. Venezia");
		qualification = new CheckBox("Mostra solo le offerte che richiedono un titolo specifico");
		qualification.setSizeFull();
		contractType = new ComboBox<>("Tipo di contratto",Arrays.asList("A tempo indeterminato","A tempo determinato",
			"Di somministrazione","A chiamata","Di lavoro accessorio","Apprendistato","Part-time","A progetto","Tirocinio"));
		contractType.setPlaceholder("Es. Part-time");
		scope = new TextField("Settore d'impiego");
		scope.setPlaceholder("Es. Metallurgia");
		job = new TextField("Mansione");
		job.setPlaceholder("Es. Saldatore");
		expirationDate = new DateField("Mostra solo le offerte che scadono entro la data");
		expirationDate.setDateFormat("dd-MM-yyyy");
		expirationDate.setValue(LocalDate.now());
		expirationDate.setTextFieldEnabled(false);
		searchButton = new Button("Cerca", event -> eventBus.post(new SearchOfferEvent(getFilters())));
		searchButton.setStyleName(MaterialTheme.BUTTON_PRIMARY);
		
		filter = new VerticalLayout(keywords, location, qualification, scope, job, contractType, expirationDate, searchButton);
		filter.addStyleName(MaterialTheme.CARD_2);
	}
	
	private void constructPopup() {
		selectedOffer = new Label();
		selectedOffer.setWidth("100%");
		candidateButton = new Button("Candidati all'offerta");

		candidate = new VerticalLayout(selectedOffer, candidateButton);
		candidate.setComponentAlignment(candidateButton, Alignment.MIDDLE_CENTER);
		candidate.setComponentAlignment(selectedOffer, Alignment.MIDDLE_CENTER);
		candidateButton.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_PRIMARY);
		candidateButton.addListener(event -> {
			eventBus.post(new CandidateEvent(selectedOffer.getId()));
			popup.setPopupVisible(false);
			popup.setVisible(false);
		});
		
		popup = new PopupView(null, candidate);
		popup.setHideOnMouseOut(false);
		popup.setVisible(false);
		popup.setPopupVisible(false);
		popup.setStyleName("selectedOffer");
		popup.addPopupVisibilityListener(event -> {
			offers.iterator().forEachRemaining(component -> component.setEnabled(!event.isPopupVisible()));
		});
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
	
	public TextField getKeywords() {
		return keywords;
	}
	
	public VerticalLayout getOffers() {
		return offers;
	}
	
	public Label getSelectedOffer() {
		return selectedOffer;
	}
	
}
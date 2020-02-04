package com.blockcv.view.pages;

import java.util.Arrays;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.AddOfferEvent;
import com.blockcv.events.RemoveOfferEvent;
import com.blockcv.events.SaveOffersEvent;
import com.blockcv.presenter.MainPagePresenter;
import com.blockcv.view.NavigableView;
import com.blockcv.view.offer.AddOfferView;
import com.blockcv.view.offer.CandidateView;
import com.blockcv.view.offer.OfferView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;

public class ManageOffersPageView extends VerticalLayout implements NavigableView{

	public static final String URI = "manageoffers";
	public static final String MENU_NAME = "Gestisci offerte";

	private EventBus eventBus;
	
	private final Label pageTitle;
	private final VerticalLayout offers;
	private final VerticalLayout addAndSave;
	private final HorizontalSplitPanel split;
	private final VerticalLayout candidatesLayout;

	private final Label offersTitle;
	private final VerticalLayout offersList;
	private VerticalLayout selectedOff;
	private HorizontalLayout subContent;

	private final AddOfferView addOfferView;
	private final Button addOfferButton;
	private final Button remOfferButton;
	private final Button saveOfferChanges;
	private final Button newOfferButton;
	private final Button saveButton;
	private Window subWindow;
	
	public ManageOffersPageView(EventBus eventBus) {
		this.eventBus = eventBus;

		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		offersTitle = new Label("Le tue offerte");
		offersList = new VerticalLayout();

		addOfferView = new AddOfferView();
		addOfferButton = new Button("Aggiungi offerta", event -> {
			if(addOfferView.controlFields())
				eventBus.post(new AddOfferEvent(addOfferView.getFields()));
			else
				MainPagePresenter.showNotification("Compilare tutti i campi correttamente", Type.WARNING_MESSAGE);
		});

		subWindow = new Window("Conferma rimozione offerta");
		subContent = new HorizontalLayout();

		subWindow.setContent(subContent);
		subContent.addComponent(new Button("Conferma", event -> {
			eventBus.post(new RemoveOfferEvent(selectedOff.getId()));
			subWindow.close();
		}));
		subContent.addComponent(new Button("Annulla", event -> subWindow.close()));
		subWindow.setHeight("200px");
		subWindow.setWidth("400px");
		subWindow.center();
		subWindow.setModal(true);

		remOfferButton = new Button("Rimuovi offerta", event -> UI.getCurrent().addWindow(subWindow));
		saveOfferChanges = new Button("Salva modifiche offerta", event -> {
			if(addOfferView.controlFields()) {
				eventBus.post(new AddOfferEvent(addOfferView.getFields()));
				eventBus.post(new RemoveOfferEvent(selectedOff.getId()));
			}
			else
				MainPagePresenter.showNotification("Compilare tutti i campi correttamente", Type.WARNING_MESSAGE);
		});
		newOfferButton = new Button("Nuova offerta", event -> {
			setAddMode(true);
			emptyOfferView();
			deselectOffers();
		});
		saveButton = new Button("Salva offerte", event -> eventBus.post(new SaveOffersEvent()));

		pageTitle = new Label("GESTISCI LE TUE OFFERTE");
		offers = new VerticalLayout(offersTitle, offersList);
		addAndSave = new VerticalLayout(addOfferView, addOfferButton, remOfferButton, saveOfferChanges, newOfferButton, saveButton);
		split = new HorizontalSplitPanel(offers, addAndSave);

		candidatesLayout = new VerticalLayout();

		pageTitle.setStyleName("pageTitle");
		split.setSplitPosition(50, Unit.PERCENTAGE);
		split.setHeight(1100, Unit.PIXELS);
		split.setStyleName("manageOfferSplit");
		candidatesLayout.setStyleName(MaterialTheme.CARD_2);
		candidatesLayout.setCaption("Seleziona un offerta per vedere i candidati");
		addComponents(pageTitle, new Label("Seleziona un'offerta presente per modificarne i dati, oppure inseriscine una nuova"), split, candidatesLayout);
		setWidth(1000, Unit.PIXELS);
		setAddMode(true);
	}
	
	public void setAddOfferViewFields(List<String> fields) {
		addOfferView.setFields(fields);
	}

	public void deselectOffers() {
		offersList.iterator().forEachRemaining(component -> component.removeStyleName("selectedOffer"));
		candidatesLayout.removeAllComponents();
	}

	public void setSelectedOff(VerticalLayout off) {
		selectedOff = off;
	}
	
	public void addCandidate(CandidateView c) {
		candidatesLayout.addComponent(c);
	}

	public void removeSelectedOff() {
		offersList.removeComponent(selectedOff);
		selectedOff = null;
		emptyOfferView();
		deselectOffers();
		setAddMode(true);
	}

	public void setAddMode(boolean visible) {
		addOfferButton.setVisible(visible);
		remOfferButton.setVisible(!visible);
		saveOfferChanges.setVisible(!visible);
		newOfferButton.setVisible(!visible);
	}

	public void emptyOfferView() {
		addOfferView.setFields(Arrays.asList("","4","","","","","01-01-2020","A tempo indeterminato","",""));
	}
	
	public void addOffer(OfferView offer) {
		offersList.addComponent(offer);
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
	
	public AddOfferView getAddOfferView() {
		return addOfferView;
	}
	
	public VerticalLayout getOffersList() {
		return offersList;
	}
	
	public VerticalLayout getSelectedOff() {
		return selectedOff;
	}
	
	public VerticalLayout getCandidatesLayout() {
		return candidatesLayout;
	}
	
	public Button getAddOfferButton() {
		return addOfferButton;
	}
	
}
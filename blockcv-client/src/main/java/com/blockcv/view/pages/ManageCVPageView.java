package com.blockcv.view.pages;
import java.io.*;
import java.util.*;

import com.blockcv.events.*;
import com.blockcv.view.curriculum.*;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.view.NavigableView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.icons.VaadinIcons;

public class ManageCVPageView extends VerticalLayout implements NavigableView {
	
	public static final String URI = "managecv";
	public static final String MENU_NAME = "Gestisci CV";
	
	private EventBus eventBus;
	
	private final Label pageTitle;
	private final Upload importButton;
	private final Button exportButton;
	private FileDownloader fileDownloader;
	private final Button publicCV;
	private final VerticalLayout curriculum;
	private final VerticalLayout addAndSave;

	private PersonalInfoView personalInfo;
	private final VerticalLayout educationalExps;
	private final VerticalLayout workingExps;
	private VerticalLayout selectedExp;
	
	private final AddExperienceView addExperienceView;
	private final Button addExpButton;
	private final Button remExpButton;
	private final Button requestCertButton;
	private final Button saveExpChanges;
	private final Button newExpButton;
	private final Button saveButton;
	
	PopupView popup;
	File xmlUpload;

	public class XmlCVManager implements Upload.Receiver, SucceededListener {

		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				if (mimeType.equals("text/xml")){
					xmlUpload = new File("importedCV"+new Random().nextInt()+".xml");
					return new FileOutputStream(xmlUpload);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public void uploadSucceeded(SucceededEvent event) {
	        // Show the uploaded file in the image viewer
			eventBus.post(new ImportEvent(xmlUpload));
	    }
	}

	public ManageCVPageView(EventBus eventBus) {
		this.eventBus = eventBus;

		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		educationalExps = new VerticalLayout();
		educationalExps.setCaption("<h4 class=\\\"h4\\\">Esperienze formative</h4>");
		educationalExps.setCaptionAsHtml(true);

		workingExps = new VerticalLayout();
		workingExps.setCaption("<h4 class=\\\"h4\\\">Esperienze lavorative</h4>");
		workingExps.setCaptionAsHtml(true);

		addExperienceView = new AddExperienceView();
		addExperienceView.addStyleNames(MaterialTheme.BUTTON_PRIMARY);
		addExpButton = new Button("Aggiungi esperienza", 
		event -> {
			eventBus.post(new AddExpEvent(addExperienceView.getFields()));
			popup.setPopupVisible(false);
		});
		addExpButton.addStyleNames(MaterialTheme.BUTTON_PRIMARY);
		remExpButton = new Button("Rimuovi esperienza", event -> {eventBus.post(new RemoveExpEvent(selectedExp.getId()));
		popup.setPopupVisible(false);});
		remExpButton.addStyleNames(MaterialTheme.BUTTON_PRIMARY);
		
		requestCertButton = new Button("Richiedi certificazione", event -> {
			eventBus.post(new CertRequestEvent(selectedExp.getId(), addExperienceView.getFields().get("idOrg"),
					addExperienceView.getFields().get("type"), "", addExperienceView.getFields().get("studyTitle")));
			popup.setPopupVisible(false);
		});
		
		requestCertButton.addStyleNames(MaterialTheme.BUTTON_PRIMARY);
		saveExpChanges = new Button("Salva modifiche esperienza", event -> {
			eventBus.post(new AddExpEvent(addExperienceView.getFields()));
			eventBus.post(new RemoveExpEvent(selectedExp.getId()));
			popup.setPopupVisible(false);
		});
		saveExpChanges.addStyleNames(MaterialTheme.BUTTON_PRIMARY);
		newExpButton = new Button(VaadinIcons.PLUS, event -> {
			setAddMode(true);
			emptyExperienceView();
			deselectExps();
			popup.setPopupVisible(true);
		});
		newExpButton.addStyleNames(MaterialTheme.BUTTON_FLOATING_ACTION,MaterialTheme.BUTTON_PRIMARY);
		saveButton = new Button("Salva CV", event -> eventBus.post(new SaveCVEvent(getVisibility())));
		
		pageTitle = new Label("CREA E GESTISCI IL TUO CV");
		XmlCVManager xmlManager = new XmlCVManager();
		importButton = new Upload("", xmlManager);
        importButton.setButtonCaption("Importa CV");
        importButton.addSucceededListener(xmlManager);
        //importButton.setImmediateMode(false);
		exportButton = new Button("Esporta CV", event -> eventBus.post(new ExportRequestEvent()));
		fileDownloader = new FileDownloader(new FileResource(new File("")));
		fileDownloader.extend(exportButton);

		publicCV = new Button("Pubblica il tuo CV", event -> eventBus.post(new PublicCVEvent()));
		personalInfo = new PersonalInfoView();
		curriculum = new VerticalLayout(personalInfo, educationalExps, workingExps);
		curriculum.setStyleName(MaterialTheme.CARD_2);
		
		HorizontalLayout btnSet = new HorizontalLayout();
		btnSet.addComponents(addExpButton, remExpButton, requestCertButton, saveExpChanges);
		
		addAndSave = new VerticalLayout(addExperienceView,btnSet);
		addAndSave.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		addAndSave.setStyleName(MaterialTheme.CARD_2);
		
		pageTitle.setStyleName("pageTitle");
		
		
		// Content for the PopupView
		VerticalLayout popupContent = new VerticalLayout();
		popupContent.addComponent(addAndSave);
		popupContent.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		//popupContent.addComponent(new Button("Button"));

		// The component itself
		popup = new PopupView("", popupContent);
		popup.setHideOnMouseOut(false);
		popup.setPopupVisible(false);
		
		addComponents(pageTitle, new Label("L'importazione del CV non sostituisce le esperienze presenti, assicurati di compilare i campi mancanti per una maggiore completezza."),
				new Label("Il tuo curriculum può essere pubblico, e quindi visibile agli altri utenti, oppure privato"),
				importButton, exportButton, publicCV, curriculum, popup, saveButton, newExpButton);//, addAndSave);
		//setAddMode(true);
	}
	
	public EventBus getEventBus() {
		return this.eventBus;
	}
	
	public void downloadCV(StreamResource xmlCV){
		fileDownloader.setFileDownloadResource(xmlCV);
	}
	
	public void setPersonalInfo(PersonalInfoView view) {
		curriculum.removeComponent(personalInfo);
		personalInfo = view;
		curriculum.addComponent(personalInfo, 0);
	}
	
	public void setPublicCV(Boolean visible) {
		publicCV.setVisible(!visible);
		personalInfo.setVisibility(visible);
		educationalExps.iterator().forEachRemaining(component -> ((EducationalExpView)component).setCheckVisible(visible));
		workingExps.iterator().forEachRemaining(component -> ((WorkingExpView)component).setCheckVisible(visible));
	}
	
	public Map<String,Boolean> getVisibility() {
		Map<String,Boolean> vis = personalInfo.getVisibility();
		vis.put("curriculum", !publicCV.isVisible());		
		return vis;
	}
	
	public void setAddExpViewFields(String type, List<String> fields) {
		addExperienceView.setFields(type, fields);
	}
	
	public void deselectExps() {
		educationalExps.iterator().forEachRemaining(component -> component.removeStyleName("selectedExp"));
		workingExps.iterator().forEachRemaining(component -> component.removeStyleName("selectedExp"));
	}
	
	public void setSelectedExp(VerticalLayout exp) {
		selectedExp = exp;
	}
	
	public void removeAllExps() {
		educationalExps.removeAllComponents();
		workingExps.removeAllComponents();
	}
	
	public void removeSelectedExp() {
		educationalExps.removeComponent(selectedExp);
		workingExps.removeComponent(selectedExp);
		selectedExp = null;
		emptyExperienceView();
		setAddMode(true);
	}
	
	public void setAddMode(boolean visible) {
		addExpButton.setVisible(visible);
		remExpButton.setVisible(!visible);
		requestCertButton.setVisible(!visible);
		saveExpChanges.setVisible(!visible);
		/*newExpButton.setVisible(!visible);*/
		popup.setPopupVisible(true);
	}
	
	public void emptyExperienceView() {
		addExperienceView.setFields("edu", Arrays.asList("","","","","","",""));
	}
	public void setWorkView() {
		for (Component educationalExp : educationalExps) {
			((EducationalExpView)educationalExp).setCheckVisible(true);
		}
		for (Component workingExp : workingExps) {
			((WorkingExpView)workingExps).setCheckVisible(true);
		}
	}
	public void addEducationalExp(EducationalExpView exp, Boolean checkVisible) {
		exp.setCheckVisible(checkVisible);
		educationalExps.addComponent(exp);

	}
	
	public void addWorkingExp(WorkingExpView exp, Boolean checkVisible) {
		exp.setCheckVisible(checkVisible);
		workingExps.addComponent(exp);
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
	
	public PersonalInfoView getPersonalInfo() {
		return personalInfo;
	}
	
	public Button getPublicCV() {
		return publicCV;
	}
	
	public AddExperienceView getAddExperienceView() {
		return addExperienceView;
	}
	
	public VerticalLayout getSelectedExp() {
		return selectedExp;
	}
	
	public VerticalLayout getEducationalExps() {
		return educationalExps;
	}
	
	public VerticalLayout getWorkingExps() {
		return workingExps;
	}
	
	public Button getAddExpButton() {
		return addExpButton;
	}

	public void setOrgSuggestions(List<OrgSuggestion> orgSuggestions) {
		addExperienceView.setOrgSuggestions(orgSuggestions);
	}
	
	public FileDownloader getFileDownloader() {
		return fileDownloader;
	}
	
}

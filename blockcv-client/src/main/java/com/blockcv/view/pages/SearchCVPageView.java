package com.blockcv.view.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.CertAssignEvent;
import com.blockcv.events.SearchCVEvent;
import com.blockcv.view.NavigableView;
import com.blockcv.view.curriculum.CompactCVView;
import com.blockcv.view.curriculum.CurriculumView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class SearchCVPageView extends VerticalLayout implements NavigableView {
	
	public static final String URI = "searchcv";
	public static final String MENU_NAME = "Ricerca CV";
	
	private EventBus eventBus;
	
	private final Label pageTitle;
	private final VerticalLayout filter;
	private final VerticalLayout cvs;
	private final VerticalSplitPanel leftSplit;
	private final VerticalLayout selectedCVLayout;
	private final VerticalLayout certify;
	private final VerticalSplitPanel rightSplit;
	private final HorizontalSplitPanel split;
	
	private final TextField keywords;
	private final TextField location;
	private final CheckBox certifiableExps;
	private final Button searchButton;
	
	private final Label selectedCVTitle;
	private CurriculumView selectedCV;
	private CompactCVView selectedCompactCV;
	
	private final TextArea certifyComment;
	private final Button certifyButton;
	
	public SearchCVPageView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		keywords = new TextField("Titolo di studio, specializzazione o azienda");
		keywords.setPlaceholder("Es. Diploma superiore");
		location = new TextField("Località");
		location.setPlaceholder("Es. Venezia");
		certifiableExps = new CheckBox("Mostra solo i CV che riportano esperienze certificabili");
		certifiableExps.setSizeFull();
		searchButton = new Button("Cerca", event -> eventBus.post(new SearchCVEvent(getFilters())));
		
		selectedCVTitle = new Label("Selezionando un CV dalla lista, qui puoi vederne i dettagli");
		selectedCV = new CurriculumView();
		selectedCompactCV = null;
		
		certifyComment = new TextArea("Inserisci un commento o una valutazione");
		certifyButton = new Button("Certifica", event -> eventBus.post(new CertAssignEvent(selectedCV.getId(), certifyComment.getValue())));
		
		pageTitle = new Label("RICERCA CV");
		filter = new VerticalLayout(keywords, location, certifiableExps, searchButton);
		cvs = new VerticalLayout();
		leftSplit = new VerticalSplitPanel(filter, cvs);
		selectedCVLayout = new VerticalLayout(selectedCVTitle, selectedCV);
		certify = new VerticalLayout(certifyComment, certifyButton);
		rightSplit = new VerticalSplitPanel(selectedCVLayout, certify);
		split = new HorizontalSplitPanel(leftSplit, rightSplit);

		pageTitle.setStyleName("pageTitle");
		leftSplit.setSplitPosition(35, Unit.PERCENTAGE);
		rightSplit.setSplitPosition(70, Unit.PERCENTAGE);
		certify.setVisible(false);
		split.setSplitPosition(40, Unit.PERCENTAGE);
		split.setHeight(900, Unit.PIXELS);
		split.setStyleName("searchCVSplit");
		addComponents(pageTitle, new Label("Visualizza i CV resi pubblici, filtrandoli in base alle tue esigenze"), split);
	}
	
	public Map<String,String> getFilters() {
		Map<String,String> filters = new HashMap<>();
		filters.put("keywords", keywords.getValue());
		filters.put("location", location.getValue());
		filters.put("certifiableExps", certifiableExps.getValue().toString());
		return filters;
	}
	
	public void setCompactCVs(List<CompactCVView> newCVs) {
		leftSplit.removeComponent(cvs);
		cvs.removeAllComponents();
		newCVs.iterator().forEachRemaining(component -> cvs.addComponent(component));
		leftSplit.setSecondComponent(cvs);
	}
	
	public void deselectCVs() {
		cvs.iterator().forEachRemaining(component -> component.removeStyleName("selectedCV"));
	}
	
	public void setSelectedCV(CurriculumView cv) {
		selectedCVLayout.removeComponent(selectedCV);
		selectedCV = cv;

		selectedCVLayout.addComponent(selectedCV, 1);
	}
	
	public void setSelectedCompactCV(CompactCVView cv) {
		selectedCompactCV = cv;
	}
	
	public void setSelectedCVCertifiable(boolean visible) {
		selectedCompactCV.setCertifiable(visible);
		setCertifyVisible(visible);
	}
	
	public void setCertifyVisible(boolean visible) {
		certify.setVisible(visible);
		certifyComment.clear();
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
	
	public TextField getKeywords() {
		return keywords;
	}
	
	public VerticalLayout getCvs() {
		return cvs;
	}
	
	public CurriculumView getSelectedCV() {
		return selectedCV;
	}
	
	public VerticalLayout getCertify() {
		return certify;
	}
	
	public CompactCVView getSelectedCompactCV() {
		return selectedCompactCV;
	}
	
}
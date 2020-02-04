package com.blockcv.view.curriculum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jetty.util.log.Log;

import com.blockcv.view.View;
import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;

import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestion;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteEvents.SelectEvent;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteEvents.SelectListener;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;

public class AddExperienceView extends VerticalLayout implements View {

	private final RadioButtonGroup<String> type;
	private final FormLayout educationalExpFields;
	private final FormLayout workingExpFields;
	
	private final TextField studyTitle;
	//private final TextField institute;
	private final ComboBox<OrgSuggestion> institute;
	private final TextField specialization;
	private final TextField eVenue;
	private final TextField ePeriodFrom;
	private final TextField ePeriodTo;
	/*private final DateField ePeriodFrom;
	private final DateField ePeriodTo;*/
	
	private final TextField company;
	private final TextField wPeriodFrom;
	private final TextField wPeriodTo;
	/*private final DateField wPeriodFrom;
	private final DateField wPeriodTo;*/
	private final TextField wVenue;
	private final TextField role;
	
	private Map<String, String> orgSuggestions;
	//private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final Pattern dateWritingPattern = Pattern.compile("[0-9]*-?[0-9]*-?[0-9]*");

	public class StringDateField extends TextField implements HasValue.ValueChangeListener<String> {

		public String lastValue;

		public StringDateField(String name) {
			super(name);
			setValueChangeMode(ValueChangeMode.EAGER);
			addValueChangeListener(this);
			lastValue = "";
		}

		@Override
		public void valueChange(ValueChangeEvent<String> event) {
			String text = (String) event.getValue();
			if (!dateWritingPattern.matcher(text).matches()) setValue(lastValue);
			else lastValue=text;
		}
	}
	
	public AddExperienceView() {

		type = new RadioButtonGroup<>("Tipo di esperienza", Arrays.asList("Formativa","Lavorativa"));
		type.setSelectedItem("Formativa");
		type.addValueChangeListener(event -> changeType(event.getValue()));
		
		studyTitle = new TextField("Titolo di studio");
		institute  = new ComboBox<OrgSuggestion>("Istituto");//new TextField();
		specialization = new TextField("Specializzazione");
		eVenue = new TextField("Sede dell'istituto");
		ePeriodFrom = new StringDateField("Periodo: da");
		ePeriodFrom.setPlaceholder("AAAA-MM-DD");
		//ePeriodFrom.setDateFormat("yyyy-MM-dd");
		ePeriodTo = new StringDateField("A");
		ePeriodTo.setPlaceholder("AAAA-MM-DD o Lascia Vuoto");
		//ePeriodTo.setDateFormat("yyyy-MM-dd");
		
		company = new TextField("Azienda");
		wPeriodFrom = new StringDateField("Periodo lavorativo: da");
		wPeriodFrom.setPlaceholder("AAAA-MM-DD");
		//wPeriodFrom.setDateFormat("yyyy-MM-dd");
		wPeriodTo = new StringDateField("a");
		wPeriodTo.setPlaceholder("AAAA-MM-DD o Lascia Vuoto");
		//wPeriodTo.setDateFormat("yyyy-MM-dd");
		wVenue = new TextField("Sede aziendale");
		role = new TextField("Ruolo");
		
		educationalExpFields = new FormLayout(studyTitle, institute, specialization, eVenue, new HorizontalLayout(ePeriodFrom, ePeriodTo));
		workingExpFields = new FormLayout(company, new HorizontalLayout(wPeriodFrom, wPeriodTo), wVenue, role);
		
		orgSuggestions  = new HashMap<String, String>();
	

		
		addComponents(type, educationalExpFields, workingExpFields);
		workingExpFields.setVisible(false);
		setCaption("Dati esperienza");
		addStyleName("experienceView");
			
	}
	
	public void changeType(String type) {
		if(type.equals("Formativa")) {
			workingExpFields.setVisible(false);
			educationalExpFields.setVisible(true);
		}
		else {
			educationalExpFields.setVisible(false);
			workingExpFields.setVisible(true);
		}
	}
	
	public void setFields(String type, List<String> fields) {
		if(type.equals("edu")) {
			this.type.setSelectedItem("Formativa");
			studyTitle.setValue(fields.get(0));
			institute.setValue(new OrgSuggestion(fields.get(1), fields.get(2)));
			specialization.setValue(fields.get(3));
			eVenue.setValue(fields.get(4));
			/*ePeriodFrom.setValue(LocalDate.parse(fields.get(4),formatter));
			ePeriodTo.setValue(LocalDate.parse(fields.get(5),formatter));*/
			ePeriodFrom.setValue(fields.get(5));
			ePeriodTo.setValue(fields.get(6));

		}
		else { //work
			this.type.setSelectedItem("Lavorativa");
			company.setValue(fields.get(0));
			/*wPeriodFrom.setValue(LocalDate.parse(fields.get(1),formatter));
			wPeriodTo.setValue(LocalDate.parse(fields.get(2),formatter));*/
			wPeriodFrom.setValue(fields.get(1));
			wPeriodTo.setValue(fields.get(2));
			wVenue.setValue(fields.get(3));
			role.setValue(fields.get(4));
		}
	}
	
	public Map<String,String> getFields() {
		Map<String,String> fields = new HashMap<>();
		String t = type.getSelectedItem().get();
		fields.put("type",t);
		fields.put("visibility","false");
		fields.put("status", "pending");
		if(t.equals("Formativa")) {
			fields.put("studyTitle", studyTitle.getValue());
			fields.put("institute", institute.getValue().getNameOrg());
			fields.put("idOrg", institute.getValue().getIdOrg());
			fields.put("specialization", specialization.getValue());
			fields.put("venue", eVenue.getValue());
			/*fields.put("periodFrom", ePeriodFrom.getValue().format(formatter));
			fields.put("periodTo", ePeriodTo.getValue().format(formatter));*/
			fields.put("periodFrom", ePeriodFrom.getValue());
			fields.put("periodTo", ePeriodTo.getValue());
		}
		else { //work
			fields.put("company", company.getValue());
			/*fields.put("periodFrom", wPeriodFrom.getValue().format(formatter));
			fields.put("periodTo", wPeriodTo.getValue().format(formatter));*/
			fields.put("periodFrom", wPeriodFrom.getValue());
			fields.put("periodTo", wPeriodTo.getValue());
			fields.put("venue", wVenue.getValue());
			fields.put("role", role.getValue());
		}

		fields.put("idOrg", institute.getValue().getIdOrg());
		return fields;
	}

	public void setOrgSuggestions(List<OrgSuggestion> orgSuggestions) {
		institute.setItems(orgSuggestions);
		institute.setItemCaptionGenerator(OrgSuggestion::getNameOrg);
	}
}

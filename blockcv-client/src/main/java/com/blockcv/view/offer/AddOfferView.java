package com.blockcv.view.offer;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blockcv.view.View;
import com.blockcv.view.pages.SignupPageView;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AddOfferView extends VerticalLayout implements View {
	
	private final FormLayout offerFields;
	
	private final TextField title;
	private final NativeSelect<Integer> maxCandidates;
	private final TextField employmentSector;
	private final TextField workFunction;
	private final TextField requiredStudyTitle;
	private final TextField requiredCert;
	private final DateField expirationDate;
	private final ComboBox<String> contractType;
	private final TextField salaryRange;
	private final TextArea description;
	
	// INPUT VALIDATION
	private final Binder<TextField> textBinder;
	private final Binder<DateField> dateBinder;
	
	public AddOfferView() {
		
		title = new TextField("Titolo offerta");
		maxCandidates = new NativeSelect<>("N. massimo di candidati", Arrays.asList(1,2,3,4,5,6,7,8,9,10));
		maxCandidates.setEmptySelectionAllowed(false);
		maxCandidates.setSelectedItem(4);
		employmentSector = new TextField("Settore di impiego");
		workFunction = new TextField("Funzione lavorativa");
		requiredStudyTitle = new TextField("Titolo di studio richiesto");
		requiredCert = new TextField("Certificato richiesto");
		expirationDate = new DateField("Data di scadenza");
		expirationDate.setDateFormat("dd-MM-yyyy");
		contractType = new ComboBox<>("Tipo di contratto", Arrays.asList("A tempo indeterminato","A tempo determinato","Di somministrazione","A chiamata","Di lavoro accessorio","Apprendistato","Part-time","A progetto","Tirocinio"));
		contractType.setEmptySelectionAllowed(false);
		contractType.setSelectedItem("A tempo indeterminato");
		salaryRange = new TextField("Range dello stipendio");
		description = new TextArea("Descrizione");
/*
		title.setValue("Titolo Offerta");
		employmentSector.setValue("Titolo employmentSector");
		workFunction.setValue("Titolo workFunction");
		requiredStudyTitle.setValue("Titolo requiredStudyTitle");
		requiredCert.setValue("Titolo requiredCert");
		expirationDate.setValue(Calendar.getInstance().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		salaryRange.setValue("Titolo salaryRange");
		description.setValue("Titolo description");
		*/
		offerFields = new FormLayout(title, maxCandidates, employmentSector, workFunction, requiredStudyTitle, requiredCert, expirationDate, contractType, salaryRange, description);
		
		addComponent(offerFields);
		setCaption("Dati offerta");
		addStyleName("offerView");
		
		// INPUT VALIDATION
		textBinder = new Binder<>();
		dateBinder = new Binder<>();
		
		textBinder.forField(title).asRequired().bind(TextField::getValue, TextField::setValue);
		textBinder.forField(employmentSector).asRequired().bind(TextField::getValue, TextField::setValue);
		textBinder.forField(workFunction).asRequired().bind(TextField::getValue, TextField::setValue);
		textBinder.forField(requiredStudyTitle).asRequired().bind(TextField::getValue, TextField::setValue);
		textBinder.forField(requiredCert).asRequired().bind(TextField::getValue, TextField::setValue);
		dateBinder.forField(expirationDate).asRequired().withValidator(new DateRangeValidator("La data deve essere successiva al giorno corrente", LocalDate.now(), null)).bind(DateField::getValue, DateField::setValue);
		textBinder.forField(salaryRange).asRequired().bind(TextField::getValue, TextField::setValue);
	}
	
	public boolean controlFields() {
		return (textBinder.validate().isOk() && dateBinder.validate().isOk());
	}
	
	public void setFields(List<String> fields) {
		title.setValue(fields.get(0));
		maxCandidates.setSelectedItem(Integer.valueOf(fields.get(1)));
		employmentSector.setValue(fields.get(2));
		workFunction.setValue(fields.get(3));
		requiredStudyTitle.setValue(fields.get(4));
		requiredCert.setValue(fields.get(5));
		if(!(fields.get(6).equals("")))
			expirationDate.setValue(LocalDate.parse(fields.get(6), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		contractType.setSelectedItem(fields.get(7));
		salaryRange.setValue(fields.get(8));
		description.setValue(fields.get(9));
	}
	
	public Map<String,String> getFields() {
		Map<String,String> fields = new HashMap<>();
		fields.put("title", title.getValue());
		fields.put("maxCandidates", maxCandidates.getValue().toString());
		fields.put("employmentSector", employmentSector.getValue());
		fields.put("workFunction", workFunction.getValue());
		fields.put("requiredStudyTitle", requiredStudyTitle.getValue());
		fields.put("requiredCert", requiredCert.getValue());
		if(!expirationDate.isEmpty())
			fields.put("expirationDate", expirationDate.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		else
			fields.put("expirationDate", "");
		fields.put("contractType", contractType.getValue());
		fields.put("salaryRange", salaryRange.getValue());
		fields.put("description", description.getValue());
		return fields;
	}
}

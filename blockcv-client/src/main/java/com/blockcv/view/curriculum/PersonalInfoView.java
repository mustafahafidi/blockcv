package com.blockcv.view.curriculum;

import java.util.HashMap;
import java.util.Map;

import com.blockcv.view.View;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class PersonalInfoView extends HorizontalLayout implements View {

	private Label firstname;
	private Label lastname;
	private Label dateOfBirth;
	private Label placeOfBirth;
	private Label address;
	private Label phoneNumber;
	private Label gender;
	private Label fiscalCode;
	
	private CheckBox dateOfBirthV;
	private CheckBox placeOfBirthV;
	private CheckBox phoneNumberV;
	private CheckBox genderV;
	private CheckBox fiscalCodeV;
	
	public PersonalInfoView() {}
	
	public PersonalInfoView(String f, String l, String db, String pb, String a, String pn, String g, String fc, Map<String,Boolean> visibility) {
		
		firstname = new Label(f);
		lastname = new Label(l);
		dateOfBirth = new Label(db);
		placeOfBirth = new Label(pb);
		address = new Label(a);
		phoneNumber = new Label(pn);
		gender = new Label(g);
		fiscalCode = new Label(fc);
		
		firstname.setCaption("Nome");
		lastname.setCaption("Cognome");
		dateOfBirth.setCaption("Data di nascita");
		placeOfBirth.setCaption("Luogo di nascita");
		address.setCaption("Indirizzo");
		phoneNumber.setCaption("Telefono");
		gender.setCaption("Sesso");
		fiscalCode.setCaption("Codice Fiscale");
		
		firstname.setStyleName("personalInfoSingle");
		lastname.setStyleName("personalInfoSingle");
		dateOfBirth.setStyleName("personalInfoSingle");
		placeOfBirth.setStyleName("personalInfoSingle");
		address.setStyleName("personalInfoSingle");
		phoneNumber.setStyleName("personalInfoSingle");
		gender.setStyleName("personalInfoSingle");
		fiscalCode.setStyleName("personalInfoSingle");

		
		
		dateOfBirthV = new CheckBox("Visibile", visibility.get("dateOfBirth"));
		placeOfBirthV = new CheckBox("Visibile", visibility.get("placeOfBirth"));
		phoneNumberV = new CheckBox("Visibile", visibility.get("phoneNumber"));
		genderV = new CheckBox("Visibile", visibility.get("gender"));
		fiscalCodeV = new CheckBox("Visibile", visibility.get("fiscalCode"));
		
		dateOfBirthV.setStyleName("personalInfoCheck1");
		placeOfBirthV.setStyleName("personalInfoCheck1");
		phoneNumberV.setStyleName("personalInfoCheck2");
		genderV.setStyleName("personalInfoCheck2");
		fiscalCodeV.setStyleName("personalInfoCheck2");
		
		VerticalLayout info = new VerticalLayout(firstname, lastname, dateOfBirth, placeOfBirth, address, phoneNumber, gender, fiscalCode);;
		VerticalLayout vis = new VerticalLayout(new Label(), new Label(), dateOfBirthV, placeOfBirthV, new Label(), phoneNumberV, genderV, fiscalCodeV);
		addComponents(info,vis);
		setCaption("<h4 class=\"h4\">Informazioni Personali</h4>");
		setCaptionAsHtml(true);
		addStyleName(MaterialTheme.CARD_3);
		setVisibility(visibility.get("curriculum"));
		setWidth("100%");
	}
	
	public void setVisibility(boolean visible) {
		dateOfBirthV.setVisible(visible);
		placeOfBirthV.setVisible(visible);
		phoneNumberV.setVisible(visible);
		genderV.setVisible(visible);
		fiscalCodeV.setVisible(visible);
	}
	
	public Map<String,Boolean> getVisibility() {
		Map<String,Boolean> vis = new HashMap<>();
		vis.put("dateOfBirth", dateOfBirthV.getValue());
		vis.put("placeOfBirth", placeOfBirthV.getValue());
		vis.put("phoneNumber", phoneNumberV.getValue());
		vis.put("gender", genderV.getValue());
		vis.put("fiscalCode", fiscalCodeV.getValue());
		return vis;
	}
	
	
	// TEST GETTERS
	
	public Label getFirstname() {
		return firstname;
	}
	
	public CheckBox getDateOfBirthV() {
		return dateOfBirthV;
	}
	
}

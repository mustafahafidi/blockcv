package com.blockcv.view.offer;

import com.blockcv.view.View;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CandidateView extends VerticalLayout implements View {
	
	private final Label name;
	private final Label address;
	private final Label email;
	
	public CandidateView(String n, String l, String e) {
		name = new Label(n);
		address = new Label(l);
		address.setCaption("Località");
		email = new Label(e);
		
		addComponents(name, address, email);
		setCaption("Candidatura lavoratore");
		addStyleName(MaterialTheme.CARD_1);
		setWidth(40, Unit.PERCENTAGE);
	}
}

package com.blockcv.view.main;

import com.blockcv.view.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class FooterView extends CssLayout implements View {
	private final Label copyright;
	
	FooterView() {
		
		copyright = new Label("	&copy; BlockCV - Ifin Sistemi - Finity Systems Group <img class=\"logoFinity\"src=\"VAADIN/themes/mytheme/logo.png\">", ContentMode.HTML);
		//setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		setStyleName("footer");
		addComponent(copyright);
	}

	public void setWelcomeView() {
		setVisible(false);		
	}
	
	public void unsetWelcomeView() {
		setVisible(true);
	}
}

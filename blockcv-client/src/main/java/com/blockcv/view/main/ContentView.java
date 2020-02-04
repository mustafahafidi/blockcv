package com.blockcv.view.main;

import com.blockcv.view.View;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.CssLayout;

public class ContentView extends CssLayout implements View {
	
	public final String initialStyle = "v-scrollable scrollable contentView "+MaterialTheme.CARD_0;
	
	public ContentView() {
		setStyleName(initialStyle);
	}
	public void setWelcomeView() {
		setStyleName("v-scrollable");
	}

	public void unsetWelcomeView() {
		setStyleName(initialStyle);
	}

}

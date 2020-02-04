package com.blockcv.view.curriculum;

import com.blockcv.events.SelectedCVEvent;
import com.blockcv.view.View;
import com.blockcv.view.pages.SearchCVPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CompactCVView extends VerticalLayout implements View {
	
	private final SearchCVPageView parent;
	
	private final Label name;
	private final Label address;
	private final Label certifiable;
	
	public CompactCVView(SearchCVPageView view, String id, String firstname, String lastname, String a, boolean c) {
		
		parent = view;
		setId(id);
		
		name = new Label(firstname + " " + lastname);
		address = new Label(a);
		certifiable = new Label("Puoi certificare questo CV");
		certifiable.setStyleName(MaterialTheme.LABEL_COLORED);
		certifiable.setVisible(c);
		
		addComponents(name, address, certifiable);
		addStyleName(MaterialTheme.CARD_1);
		addLayoutClickListener(event -> {
			parent.getEventBus().post(new SelectedCVEvent(getId()));
			addStyleName("selectedCV");
			parent.setSelectedCompactCV(this);
		});
	}
	
	public boolean isCertifiable() {
		return certifiable.isVisible();
	}
	
	public void setCertifiable(boolean visible) {
		certifiable.setVisible(visible);
	}
}

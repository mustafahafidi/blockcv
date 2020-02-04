package com.blockcv.view.curriculum;

import java.util.Arrays;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.ExpVisibilityChanged;
import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.view.View;
import com.blockcv.view.pages.ManageCVPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class WorkingExpView extends VerticalLayout implements View {
	
	private final VerticalLayout parent;
	
	private final CheckBox visibility;
	private final Label company;
	private final Label periodFrom;
	private final Label periodTo;
	private final Label venue;
	private final Label role;
	
	public WorkingExpView(String id, VerticalLayout view, String c, String pf, String pt, String v, String r, Boolean visible, EventBus eventBus) {
		
		setId(id);
		parent = view;
		visibility = new CheckBox("Visibile", visible);
		company = new Label(c);
		periodFrom = new Label(pf);
		periodTo = new Label(pt);
		venue = new Label(v);
		role = new Label(r);
		
		company.setCaption("Azienda");
		periodFrom.setCaption("Periodo da");
		periodTo.setCaption("Periodo a");
		venue.setCaption("Sede");
		role.setCaption("Ruolo");

		company.setStyleName("personalInfoSingle");
		periodFrom.setStyleName("personalInfoSingle");
		periodTo.setStyleName("personalInfoSingle");
		venue.setStyleName("personalInfoSingle");
		role.setStyleName("personalInfoSingle");
		
		HorizontalLayout tmp = new HorizontalLayout(company, visibility);
		addComponents(tmp, new HorizontalLayout(periodFrom, periodTo), venue, role);
		tmp.setWidth("100%");
		tmp.setComponentAlignment(visibility, Alignment.TOP_RIGHT);
		addStyleName(MaterialTheme.CARD_0_5);
		if(parent instanceof ManageCVPageView) {
			ManageCVPageView pa = (ManageCVPageView)parent;
			addLayoutClickListener(event -> {
				pa.setAddExpViewFields("work", Arrays.asList(company.getValue(), periodFrom.getValue(), periodTo.getValue(), venue.getValue(), role.getValue()));
				pa.deselectExps();
				pa.setAddMode(false);
				pa.setSelectedExp(this);
				addStyleName("selectedExp");
			});
		}
		visibility.addValueChangeListener(new ValueChangeListener<Boolean>() {
			
			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				eventBus.post(new ExpVisibilityChanged(ExperienceType.WORKING, getId(), event.getValue()));
			}
		});
	}
	
	public void setCheckVisible(Boolean visible) {
		visibility.setVisible(visible);
	}


	public Boolean getVisibility() {
		return visibility.getValue();
	}
}

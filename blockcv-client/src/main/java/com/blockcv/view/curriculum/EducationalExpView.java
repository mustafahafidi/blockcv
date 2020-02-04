package com.blockcv.view.curriculum;

import java.util.Arrays;

import com.vaadin.ui.*;
import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.ExpVisibilityChanged;
import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.view.View;
import com.blockcv.view.pages.ManageCVPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.ClientConnector.AttachEvent;

public class EducationalExpView extends VerticalLayout implements View {
	
	private final VerticalLayout parent;
	
	private final EventBus eventBus;
	
	private final CheckBox visibility;
	private final Label studyTitle;
	private final String idOrg;
	private final Label institute;
	private final Label specialization;
	private final Label venue;
	private final Label periodFrom;
	private final Label periodTo;
	private final Button details;
	private final Label certified;

	public EducationalExpView(String id, String idOrg, VerticalLayout view, String st, String i, String s, String v, String pf, String pt, Boolean visible, String status, boolean orgView, EventBus eventBus) {
		this.eventBus = eventBus;

		setId(id);
		this.idOrg = idOrg;
		parent = view;

		visibility = new CheckBox("Visibile", visible);
		visibility.setVisible(!orgView);

		studyTitle = new Label(st);
		institute = new Label(i);
		specialization = new Label(s);
		venue = new Label(v);
		periodFrom = new Label(pf);
		periodTo = new Label(pt);

		certified = new Label((status.equals("approved"))?"Esp. Certificata":"");
		details = new Button("Dettagli ->");
		
		studyTitle.setCaption("Titolo di studio");
		institute.setCaption("Istituto");
		specialization.setCaption("Specializzazione");
		venue.setCaption("Sede");
		periodFrom.setCaption("Periodo da");
		periodTo.setCaption("Periodo a");
		
		studyTitle.setStyleName("personalInfoSingle");
		institute.setStyleName("personalInfoSingle");
		specialization.setStyleName("personalInfoSingle");
		venue.setStyleName("personalInfoSingle");
		periodFrom.setStyleName("personalInfoSingle");
		periodTo.setStyleName("personalInfoSingle");

		VerticalLayout detLayout = new VerticalLayout(details, certified);

		HorizontalLayout tmp = new HorizontalLayout(studyTitle, visibility, detLayout);
		addComponents(tmp, institute, specialization, venue, new HorizontalLayout(periodFrom, periodTo));
		tmp.setWidth("100%");
		tmp.setComponentAlignment(visibility, Alignment.TOP_RIGHT);
		addStyleName(MaterialTheme.CARD_0_5);
		EducationalExpView edView = this;
		details.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				ManageCVPageView pa = (ManageCVPageView)parent;
				pa.setAddExpViewFields("edu", Arrays.asList(studyTitle.getValue(), idOrg, institute.getValue(), specialization.getValue(), venue.getValue(), periodFrom.getValue(), periodTo.getValue()));
				pa.deselectExps();
				pa.setAddMode(false);
				pa.setSelectedExp(edView);
				addStyleName("selectedExp");
			}
		});
		/*if(parent instanceof ManageCVPageView) {
			ManageCVPageView pa = (ManageCVPageView)parent;
			addLayoutClickListener(event -> {


			});
			
		}*/
		visibility.addValueChangeListener(new ValueChangeListener<Boolean>() {
			
			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				eventBus.post(new ExpVisibilityChanged(ExperienceType.EDUCATIONAL, getId(), event.getValue()));
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

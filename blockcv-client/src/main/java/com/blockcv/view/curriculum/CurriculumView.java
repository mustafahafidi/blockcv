package com.blockcv.view.curriculum;

import java.util.List;
import java.util.Map;

import com.blockcv.view.View;
import com.blockcv.view.pages.SearchCVPageView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CurriculumView extends VerticalLayout implements View {
	
	private SearchCVPageView parent;
	
	private VerticalLayout personalInfo;
	private VerticalLayout educationalExps;
	private VerticalLayout workingExps;
	
	public CurriculumView() {}
	
	public CurriculumView(String cvID, SearchCVPageView view, Map<String,String> fields) {
		
		setId(cvID);
		parent = view;
		
		personalInfo = new VerticalLayout();
		personalInfo.setCaption("Informazioni personali");
		personalInfo.addStyleName(MaterialTheme.CARD_0_5);
		fields.forEach((k,v) -> personalInfo.addComponent(new Label(v)));
		
		educationalExps = new VerticalLayout();
		educationalExps.setCaption("Esperienze formative");
		workingExps = new VerticalLayout();
		workingExps.setCaption("Esperienze lavorative");
		addStyleName(MaterialTheme.CARD_2);
		addComponents(personalInfo, educationalExps, workingExps);
	}
	
	public void setEduExps(List<EducationalExpView> eduExps) {
		educationalExps.removeAllComponents();
		eduExps.forEach(exp -> educationalExps.addComponent(exp));
	}
	
	public void setWorkExps(List<WorkingExpView> workExps) {
		workingExps.removeAllComponents();
		workExps.forEach(exp -> workingExps.addComponent(exp));
	}
	public void setOrgView() {
		educationalExps.forEach(c -> ((EducationalExpView)c).setCheckVisible(false));
		workingExps.forEach(c -> ((WorkingExpView)c).setCheckVisible(false));
	}
	public void setWorkView() {
		educationalExps.forEach(c -> ((EducationalExpView)c).setCheckVisible(true));
		workingExps.forEach(c -> ((WorkingExpView)c).setCheckVisible(true));
	}
}

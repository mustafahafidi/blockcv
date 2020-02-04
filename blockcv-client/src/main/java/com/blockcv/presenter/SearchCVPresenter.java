package com.blockcv.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.Subscribe;

import com.blockcv.events.CertAssignEvent;
import com.blockcv.events.SearchCVEvent;
import com.blockcv.events.SelectedCVEvent;
import com.blockcv.model.SearchCVModel;
import com.blockcv.model.SearchCVModel.Curriculum;
import com.blockcv.view.NavigableView;
import com.blockcv.view.curriculum.CompactCVView;
import com.blockcv.view.curriculum.CurriculumView;
import com.blockcv.view.curriculum.EducationalExpView;
import com.blockcv.view.curriculum.WorkingExpView;
import com.blockcv.view.pages.SearchCVPageView;
import com.vaadin.ui.Notification.Type;

public class SearchCVPresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private SearchCVPageView searchCVView;
	private SearchCVModel searchCVModel;
	
	public SearchCVPresenter(MainPagePresenter mainPagePresenter) {
		
		mainPresenter = mainPagePresenter;
		mainPresenter.getEventBus().register(this);
		
		searchCVView = new SearchCVPageView(mainPresenter.getEventBus());
		searchCVModel = new SearchCVModel();
	}

	@Subscribe
	public void onSearchCVEvent(SearchCVEvent ev) {
		List<Map<String,String>> cvs = searchCVModel.getFilteredCVs(mainPresenter.getUserModel(), ev.getFilters());
		List<CompactCVView> compactCVViews = new ArrayList<>();
		cvs.forEach(cv -> compactCVViews.add(new CompactCVView(searchCVView, cv.get("cvID"), cv.get("firstname"), cv.get("lastname"), cv.get("address"), Boolean.parseBoolean(cv.get("certifiable")))));
		searchCVView.setCertifyVisible(false);
		searchCVView.setSelectedCV(new CurriculumView());
		searchCVView.setCompactCVs(compactCVViews);
	}
	
	@Subscribe
	public void onSelectedCVEvent(SelectedCVEvent ev) {
		
		String cvID = ev.getSelectedCVID();
		Curriculum cv = searchCVModel.getCV(cvID);
		
		searchCVView.deselectCVs();
		
		CurriculumView cvView = new CurriculumView(cvID, searchCVView, cv.getPersonalInfo());
		List<EducationalExpView> eduExps = new ArrayList<>();
		List<WorkingExpView> workExps = new ArrayList<>();
		cv.getEducationalExps().forEach(exp -> eduExps.add(new EducationalExpView(exp.getExpID(), exp.getIdOrg(), cvView, exp.getStudyTitle(), exp.getInstitute(), exp.getSpecialization(), exp.getVenue(), exp.getPeriodFrom(), exp.getPeriodTo(), true,exp.getStatus(), true, mainPresenter.getEventBus())));
		cv.getWorkingExps().forEach(exp -> workExps.add(new WorkingExpView(exp.getExpID(), cvView, exp.getCompany(), exp.getPeriodFrom(), exp.getPeriodTo(), exp.getVenue(), exp.getRole(), true, mainPresenter.getEventBus())));
		cvView.setEduExps(eduExps);
		cvView.setWorkExps(workExps);
		
		searchCVView.setSelectedCV(cvView);
		searchCVView.setCertifyVisible(cv.isCertifiable());
	}
	
	@Subscribe
	public void onCertAssignEvent(CertAssignEvent ev) {

		if(searchCVModel.certifyCV(ev.getCvID(), ev.getComment())) {
			searchCVView.setSelectedCVCertifiable(false);
			MainPagePresenter.showNotification("Certificazione assegnata", Type.HUMANIZED_MESSAGE);
		}
		else
			MainPagePresenter.showNotification("Errore nella proposta di certificazione", Type.ERROR_MESSAGE);
	}
	
	@Override
	public NavigableView getPageView() {
		return searchCVView;
	}

	@Override
	public void clean() {
		searchCVView = new SearchCVPageView(mainPresenter.getEventBus());
		searchCVModel = new SearchCVModel();
	}
	
	
	// TEST GETTERS
	
	public SearchCVModel getSearchCVModel() {
		return searchCVModel;
	}
	
}

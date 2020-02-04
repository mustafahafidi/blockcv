package com.blockcv.presenter;

import com.blockcv.events.*;
import com.blockcv.model.UserModel;

import java.io.File;

import java.util.Map;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification.Type;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.blockcv.model.ManageCVModel;
import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.view.NavigableView;
import com.blockcv.view.curriculum.EducationalExpView;
import com.blockcv.view.curriculum.PersonalInfoView;
import com.blockcv.view.curriculum.WorkingExpView;
import com.blockcv.view.pages.ManageCVPageView;

import javax.xml.transform.stream.StreamResult;

public class ManageCVPresenter implements Presenter {
	
	private MainPagePresenter mainPresenter;
	private ManageCVPageView manageCVView;
	private ManageCVModel manageCVModel;

	private StreamResource toExport;
	
	public ManageCVPresenter(MainPagePresenter mainPagePresenter) {
		
		mainPresenter = mainPagePresenter;
		mainPresenter.getEventBus().register(this);
		
		manageCVView = new ManageCVPageView(mainPresenter.getEventBus());
		manageCVModel = new ManageCVModel();
	}

	@Subscribe
	public void onChangedWorkerEvent(ChangedWorkerEvent ev) {
		// MODEL & VIEW INITIALIZATION
		manageCVModel.loadExperiences(mainPresenter.getUserModel());
		Map<String,Boolean> vis = manageCVModel.getInfoVisibility();
		Map<String,String> pi = mainPresenter.getUserModel().getPersonalInfo();

		/*pi.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));
		System.out.println(vis.size());
		vis.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));*/
		manageCVView.setPersonalInfo(new PersonalInfoView(pi.get("firstname"), pi.get("lastname"), pi.get("dateOfBirth"), pi.get("placeOfBirth"), pi.get("address"), pi.get("phoneNumber"), pi.get("gender"), pi.get("fiscalCode"), vis));
		
		manageCVView.setPublicCV(vis.get("curriculum"));
		manageCVView.setOrgSuggestions(manageCVModel.getOrgSuggestions());
		manageCVModel.getEducationalExps().forEach((id,exp) -> {
			EducationalExpView expView = new EducationalExpView(id, exp.get("idOrg"), manageCVView, exp.get("studyTitle"), exp.get("institute"), exp.get("specialization"), exp.get("venue"), exp.get("periodFrom"), exp.get("periodTo"), Boolean.valueOf(exp.get("visibility")), exp.get("status"), false, mainPresenter.getEventBus());
			expView.setCheckVisible(true);
			manageCVView.addEducationalExp(expView, vis.get("curriculum"));
		});
		manageCVModel.getWorkingExps().forEach((id,exp) -> {
			WorkingExpView expView = new WorkingExpView(id, manageCVView, exp.get("company"), exp.get("periodFrom"), exp.get("periodTo"), exp.get("venue"), exp.get("role"), Boolean.valueOf(exp.get("visibility")), mainPresenter.getEventBus());
			expView.setCheckVisible(true);
			manageCVView.addWorkingExp(expView, vis.get("curriculum"));
		});
	}
	
	@Subscribe
	public void onPublicCVEvent(PublicCVEvent ev) {
		manageCVView.setPublicCV(true);
		onSaveCVEvent(new SaveCVEvent(manageCVView.getVisibility()));
	}
	
	@Subscribe
	public void onImportEvent(ImportEvent ev) {
		File xml= ev.getXml();
		manageCVModel.addExperienceFromXml(xml);
		manageCVView.removeAllExps();
		Boolean visibleCV = manageCVModel.getInfoVisibility().get("curriculum");
		for(Map.Entry<String,Map<String,String>> entry : manageCVModel.getEducationalExps().entrySet()) {
			manageCVView.addEducationalExp(new EducationalExpView(entry.getKey(), entry.getValue().get("idOrg"), manageCVView, entry.getValue().get("studyTitle"), entry.getValue().get("institute"), entry.getValue().get("specialization"),entry.getValue().get("venue"), entry.getValue().get("periodFrom"),entry.getValue().get("periodTo"), Boolean.FALSE, entry.getValue().get("status"), false, mainPresenter.getEventBus()), visibleCV);
		}
		for(Map.Entry<String,Map<String,String>> entry : manageCVModel.getWorkingExps().entrySet()) {
			manageCVView.addWorkingExp(new WorkingExpView(entry.getKey(), manageCVView, entry.getValue().get("company"), entry.getValue().get("periodFrom"),entry.getValue().get("periodTo"), entry.getValue().get("venue"), entry.getValue().get("role"), Boolean.FALSE, mainPresenter.getEventBus()), visibleCV);
		}
        MainPagePresenter.showNotification("Informazioni importate dal file XML.", Type.HUMANIZED_MESSAGE);
	}

    @Subscribe
    public void onExportRequestEvent(ExportRequestEvent ev) {
		toExport = manageCVModel.getXmlCV();
        manageCVView.downloadCV(toExport);
    }

	@Subscribe
	public void onAddExpEvent(AddExpEvent ev) {
		Map<String,String> e = ev.getExpData();

		if(e.get("type").equals("Formativa")) {
			String expID = manageCVModel.addExperience(ExperienceType.EDUCATIONAL, e);
			manageCVView.addEducationalExp(new EducationalExpView(expID, e.get("idOrg"), manageCVView, e.get("studyTitle"), e.get("institute"), e.get("specialization"), e.get("venue"), e.get("periodFrom"), e.get("periodTo"), Boolean.FALSE, e.get("status"), false, mainPresenter.getEventBus()), manageCVModel.getInfoVisibility().get("curriculum"));
		} else {
			String expID = manageCVModel.addExperience(ExperienceType.WORKING, e);
			manageCVView.addWorkingExp(new WorkingExpView(expID, manageCVView, e.get("company"), e.get("periodFrom"),e.get("periodTo"), e.get("venue"), e.get("role"), Boolean.FALSE, mainPresenter.getEventBus()), manageCVModel.getInfoVisibility().get("curriculum"));
		}
	}
	
	@Subscribe
	public void onRemoveExpEvent(RemoveExpEvent ev) {
		manageCVView.removeSelectedExp();
		String expID = ev.getSelectedExpID();
		manageCVModel.removeExperience(expID);
	}
	
	@Subscribe
	public void onCertRequestEvent(CertRequestEvent ev) {
		String expID = ev.getSelectedExpID();
		UserModel userModel = mainPresenter.getUserModel();
		
		if(manageCVModel.requestExpCertification(userModel, ev.getSelectedExpID(), ev.getIdOrg(), ev.getExpType(),ev.getComment(),ev.getExperienceTitle()))
			MainPagePresenter.showNotification("La richiesta di certificazione dell'esperienza è stata inviata correttamente all'azienda", Type.HUMANIZED_MESSAGE);
		else
			MainPagePresenter.showNotification("Impossibile inviare la richiesta.", Type.ERROR_MESSAGE);
	}
	
	@Subscribe
	public void onExpVisibilityChange(ExpVisibilityChanged ev) {
		UserModel userModel = mainPresenter.getUserModel();
		manageCVModel.updateVisibility(ev.getExpid(), ev.getExptype(), ev.getStatus());
	}
	
	@Subscribe
	public void onSaveCVEvent(SaveCVEvent ev) {
		UserModel userModel = mainPresenter.getUserModel();
		if(manageCVModel.saveCurriculum(userModel, ev.getVisibility()))
			MainPagePresenter.showNotification("CV salvato correttamente", Type.HUMANIZED_MESSAGE);
		else
			MainPagePresenter.showNotification("Errore salvataggio CV", Type.ERROR_MESSAGE);
	}
	
	@Override
	public NavigableView getPageView() {
		return manageCVView;
	}

	@Override
	public void clean() {
		manageCVView = new ManageCVPageView(mainPresenter.getEventBus());
		manageCVModel = new ManageCVModel();
	}
	
	
	// TEST GETTERS
	
	public ManageCVModel getManageCVModel() {
		return manageCVModel;
	}
	
	public StreamResource getToExport() {
		return toExport;
	}
	
}

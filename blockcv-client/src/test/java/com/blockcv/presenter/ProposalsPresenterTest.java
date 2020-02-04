package com.blockcv.presenter;

import static org.junit.Assert.*;

import com.blockcv.model.UserModel;
import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.AcceptProposalEvent;
import com.blockcv.events.ChangedWorkerEvent;
import com.blockcv.events.RejectProposalEvent;
import com.blockcv.events.TryLoginEvent;
import com.blockcv.model.ProposalsModel;
import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.model.ProposalsModel.Proposal;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.ProposalsPageView;

public class ProposalsPresenterTest {

	private App app;
	private MainPagePresenter mainPresenter;
	private ProposalsPresenter proposalsPresenter;
	private ProposalsPageView proposalsPageView;
	private ProposalsModel proposalsModel;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		proposalsPresenter = (ProposalsPresenter)app.getMainPagePresenter().getPresenters().get(4);
		proposalsPageView = (ProposalsPageView)proposalsPresenter.getPageView();
		proposalsModel = proposalsPresenter.getProposalsModel();
		// LOGIN
		mainPresenter.getEventBus().post(new TryLoginEvent("email", "password"));
	}
	
	@Test
	public void testOnChangedUserEvent() {
		assertNotNull(proposalsModel.getProposals());
		
	}

	@Test
	public void testOnAcceptProposalEvent() {
		proposalsModel.addProposal(new UserModel(), new Proposal("","","","", "Formativa","sender", "comment", "experienceTitle", "pending"));
		proposalsPresenter.onAcceptProposalEvent(new AcceptProposalEvent("prop001"));
		assertEquals(0, proposalsModel.getProposals().size());
	}

	@Test
	public void testOnRejectProposalEvent() {
		proposalsModel.addProposal(new UserModel(), new Proposal("","","","", "Formativa","sender", "comment", "experienceTitle", "pending"));
		proposalsPresenter.onRejectProposalEvent(new RejectProposalEvent("prop001", ""));
		assertEquals(0, proposalsModel.getProposals().size());
	}

	@Test
	public void testGetPageView() {
		assertNotNull(proposalsPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = proposalsPresenter.getPageView();
		proposalsPresenter.clean();
		NavigableView after = proposalsPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

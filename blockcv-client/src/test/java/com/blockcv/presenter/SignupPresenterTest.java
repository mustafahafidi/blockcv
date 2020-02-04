package com.blockcv.presenter;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.blockcv.events.TrySignupEvent;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.DataAccess;
import com.blockcv.stub.AppStub;
import com.blockcv.stub.DataAccessStub;
import com.blockcv.stub.NavigatorStub;
import com.blockcv.view.NavigableView;
import com.blockcv.view.pages.LoginPageView;

public class SignupPresenterTest {

	private App app;
	private MainPagePresenter mainPresenter;
	private SignupPresenter signupPresenter;
	
	@Before
	public void setUp() {
		DataAccessStub.setUserType(UserType.WORKER);
		DataAccess.setInstance(DataAccessStub.getInstance());
		app = new AppStub();
		app.setMainPagePresenter();
		mainPresenter = app.getMainPagePresenter();
		signupPresenter = (SignupPresenter)app.getMainPagePresenter().getPresenters().get(7);
	}
	
	@Test
	public void testOnTrySignupEvent() {
		Map<String,String> personalInfo = new HashMap<>();
		personalInfo.put("firstname", "Firstname");
		personalInfo.put("lastname", "Lastname");
		personalInfo.put("dateOfBirth", "2000-01-01");
		personalInfo.put("placeOfBirth", "Place");
		personalInfo.put("address", "Address");
		personalInfo.put("phoneNumber", "0123456789");
		personalInfo.put("gender", "Maschio");
		personalInfo.put("fiscalCode", "ABCDEFGHIJKLMNOP");
		signupPresenter.onTrySignupEvent(new TrySignupEvent("email", "password", "password", personalInfo, UserType.WORKER));
		assertEquals(LoginPageView.URI, ((NavigatorStub)app.getNavigator()).getCurrentURI());
	}

	@Test
	public void testGetPageView() {
		assertNotNull(signupPresenter.getPageView());
	}

	@Test
	public void testClean() {
		NavigableView before = signupPresenter.getPageView();
		signupPresenter.clean();
		NavigableView after = signupPresenter.getPageView();
		assertNotEquals(before, after);
	}

}

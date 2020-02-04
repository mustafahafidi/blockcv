package com.blockcv.view.pages;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.TryLoginEvent;
import com.blockcv.presenter.MainPagePresenter;
import com.blockcv.view.NavigableView;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;

public class LoginPageView extends VerticalLayout implements NavigableView {
	
	public static final String URI = "login";
	public static final String MENU_NAME = "Accedi";
	
	private EventBus eventBus;
	private final Label pageTitle;
	private final FormLayout loginForm;
	
	private final TextField email;
	private final TextField password;
	private final Button loginButton;
	
	// INPUT VALIDATION
	private final Binder<TextField> textBinder;
	
	public LoginPageView(EventBus eventBus) {
		
		this.eventBus = eventBus;
		
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		pageTitle = new Label("ACCEDI AL TUO ACCOUNT");
		email = new TextField("Email");
		password = new PasswordField("Password");
		loginButton = new Button("Accedi", event -> {
			if(controlCredentials())
				this.eventBus.post(new TryLoginEvent(email.getValue(), password.getValue()));
			else
				MainPagePresenter.showNotification("Inserire credenziali valide", Type.WARNING_MESSAGE);
		});
		
		
/*
		email.setValue("asd@asd.it");
		password.setValue("Asdasd123");
*/
		
		pageTitle.setStyleName("pageTitle");
		loginForm = new FormLayout(email, password, loginButton);
		loginForm.setWidthUndefined();
		addComponents(pageTitle, loginForm);
		
		// INPUT VALIDATION
		textBinder = new Binder<>();
		
		textBinder.forField(email).asRequired("Inserisci una mail").withValidator(new EmailValidator("Inserire una mail valida")).bind(TextField::getValue, TextField::setValue);
		textBinder.forField(password).asRequired("Inserisci una password").bind(TextField::getValue, TextField::setValue);
	}
	
	// ###############################################################   DE-COMMENTARE PRIMA RIGA   ####################################################################
	private boolean controlCredentials() {
		//return textBinder.validate().isOk();
		return true;
	}
	
	@Override
	public String getUriFragment() {
		return URI;
	}

	@Override
	public String getMenuName() {
		return MENU_NAME;
	}
	
}

package com.blockcv.view.pages;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.TrySignupEvent;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.presenter.MainPagePresenter;
import com.blockcv.view.NavigableView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BinderValidationStatusHandler;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;

public class SignupPageView extends VerticalLayout implements NavigableView {
	
	public static final String URI = "signup";
	public static final String MENU_NAME = "Registrati";
	private static final boolean SKIP_VALIDATION = false;
	
	private EventBus eventBus;
	
	private final Label pageTitle;
	private final VerticalLayout personalInfo;
	private final VerticalLayout organizationInfo;
	private final VerticalLayout credentials;
	private final RadioButtonGroup<String> userType;
	private final Button registerButton;
	
	private final TextField firstname;
	private final TextField lastname;
	private final DateField dateOfBirth;
	private final TextField placeOfBirth;
	private final TextField address;
	private final TextField phoneNumber;
	private final RadioButtonGroup<String> gender;
	private final TextField fiscalCode;
	
	private final TextField orgName;
	private final TextField foundationYear;
	private final TextField venue;
	private final TextField orgPhone;
	private final TextField vatNumber;
	
	private final TextField email;
	private final TextField password;
	private final TextField repeat;
	
	// INPUT VALIDATION
	private final Binder<TextField> wTextBinder;
	private final Binder<DateField> wDateBinder;
	private final Binder<TextField> oTextBinder;
	private final Binder<YearField> oYearBinder;
	private final Binder<TextField> passwordBinder; 
	
	private class YearField {
		private Integer year;
		public Integer getYear() {
			return year;
		}
		public void setYear(Integer year) {
			this.year = year;
		}
	}
	
	public SignupPageView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		firstname = new TextField("Nome");
		lastname = new TextField("Cognome");
		dateOfBirth = new DateField("Data di nascita");
		placeOfBirth = new TextField("Luogo di nascita");
		address = new TextField("Residenza");
		phoneNumber = new TextField("Numero di telefono");
		gender = new RadioButtonGroup<>("Sesso", Arrays.asList("Maschio","Femmina"));
		gender.setSelectedItem("Maschio");
		fiscalCode = new TextField("Codice fiscale");
		
		
		
		orgName = new TextField("Nome");
		foundationYear = new TextField("Anno di fondazione");
		venue = new TextField("Indirizzo della sede");
		orgPhone = new TextField("Recapito telefonico");
		vatNumber = new TextField("Partita IVA");
		email = new TextField("Inserisci una email");
		password = new PasswordField("Inserisci una password");
		repeat = new PasswordField("Ripeti la password");
		
		/*

		orgName.setValue("Organizzazione");
		foundationYear.setValue("2015");
		venue.setValue("Cles TN");
		orgPhone.setValue("3459261069");
		vatNumber.setValue("P245245666");

		firstname.setValue("Mustafa");
		lastname.setValue("Hafidi");
		placeOfBirth.setValue("Luogo");
		address.setValue("Indirizzo");
		phoneNumber.setValue("3459261069");
		gender.setSelectedItem("Maschio");
		fiscalCode.setValue("HFDMTF96T09Z330C");
		email.setValue("asd@asd.it");
		password.setValue("Asdasd123");
		repeat.setValue("Asdasd123");
		dateOfBirth.setValue(get18Date());
*/
		
		pageTitle = new Label("REGISTRA IL TUO ACCOUNT");
		userType = new RadioButtonGroup<>("Come vuoi registrarti?", Arrays.asList("Organizzazione","Lavoratore"));
		userType.setSelectedItem("Lavoratore");
		userType.addValueChangeListener(item -> changeTypeForm(item.getValue()));
		personalInfo = new VerticalLayout(new HorizontalLayout(firstname, lastname), new HorizontalLayout(dateOfBirth, placeOfBirth), new HorizontalLayout(address, phoneNumber), gender, fiscalCode);
		personalInfo.setCaption("Informazioni personali");
		organizationInfo = new VerticalLayout(orgName, foundationYear, venue, orgPhone, vatNumber);
		organizationInfo.setCaption("Informazioni sull'organizzazione");
		credentials = new VerticalLayout(email, password, repeat);
		credentials.setCaption("Credenziali");
		registerButton = new Button("Registra account", event -> {
			if(userType.getValue().equals("Lavoratore") && controlPersonalInfo())
				eventBus.post(new TrySignupEvent(email.getValue(), password.getValue(), repeat.getValue(), getPersonalInfo(), UserType.WORKER));
			else if (userType.getValue().equals("Organizzazione") && controlOrganizationInfo())
				eventBus.post(new TrySignupEvent(email.getValue(), password.getValue(), repeat.getValue(), getOrganizationInfo(), UserType.ORGANIZATION));
			else
				MainPagePresenter.showNotification("Compilare tutti i campi correttamente", Type.WARNING_MESSAGE);
		});
		
		pageTitle.setStyleName("pageTitle");
		userType.addStyleNames(MaterialTheme.OPTIONGROUP_SMALL, MaterialTheme.OPTIONGROUP_HORIZONTAL);
		personalInfo.setStyleName(MaterialTheme.CARD_2);
		organizationInfo.setStyleName(MaterialTheme.CARD_2);
		credentials.setStyleName(MaterialTheme.CARD_2);
		registerButton.setStyleName(MaterialTheme.BUTTON_PRIMARY);
		addComponents(pageTitle, userType, personalInfo, organizationInfo, credentials, registerButton);
		changeTypeForm("Lavoratore");
		
		// INPUT VALIDATION
		wTextBinder = new Binder<>();
		wDateBinder = new Binder<>();
		oTextBinder = new Binder<>();
		oYearBinder = new Binder<>();
		passwordBinder = new Binder<>();
		
		wTextBinder.forField(firstname).asRequired().withValidator(new RegexpValidator("Sono accettati solo caratteri alfabetici, il primo maiuscolo", "[A-Z][a-z]*")).bind(TextField::getValue, TextField::setValue);
		wTextBinder.forField(lastname).asRequired().withValidator(new RegexpValidator("Sono accettati solo caratteri alfabetici, il primo maiuscolo", "[A-Z][a-z]*")).bind(TextField::getValue, TextField::setValue);
		wDateBinder.forField(dateOfBirth).asRequired().withValidator(new DateRangeValidator("La data deve corrispondere all'età adulta", null, get18Date())).bind(DateField::getValue, DateField::setValue);
		wTextBinder.forField(placeOfBirth).asRequired().bind(TextField::getValue, TextField::setValue);
		wTextBinder.forField(address).asRequired().bind(TextField::getValue, TextField::setValue);
		wTextBinder.forField(phoneNumber).asRequired().withValidator(new RegexpValidator("Sono accettati solo caratteri numerici", "[0-9]{3,12}")).bind(TextField::getValue, TextField::setValue);
		wTextBinder.forField(fiscalCode).asRequired().withValidator(new RegexpValidator("16 caratteri alfanumerici, lettere maiuscole", "[A-Z0-9]{16}")).bind(TextField::getValue, TextField::setValue);
		wTextBinder.forField(email).asRequired().withValidator(new EmailValidator("Inserire una mail valida")).bind(TextField::getValue, TextField::setValue);
		
		
		oTextBinder.forField(orgName).asRequired().bind(TextField::getValue, TextField::setValue);
		oYearBinder.forField(foundationYear).asRequired().withConverter(new StringToIntegerConverter("Sono accettate solo cifre")).withValidator(new IntegerRangeValidator("Inserire un anno valido", 0, Calendar.getInstance().get(Calendar.YEAR))).bind(YearField::getYear, YearField::setYear);
		oTextBinder.forField(venue).asRequired().bind(TextField::getValue, TextField::setValue);
		oTextBinder.forField(orgPhone).asRequired().withValidator(new RegexpValidator("Sono accettati solo caratteri numerici", "[0-9]{3,12}")).bind(TextField::getValue, TextField::setValue);
		oTextBinder.forField(vatNumber).asRequired().withValidator(new RegexpValidator("16 caratteri numerici", ".{5,20}")).bind(TextField::getValue, TextField::setValue);
		oTextBinder.forField(email).asRequired().withValidator(new EmailValidator("Inserire una mail valida")).bind(TextField::getValue, TextField::setValue);
		
		passwordBinder.forField(password).asRequired().withValidator(new RegexpValidator("La password deve contenere almeno 8 caratteri, di cui almeno una lettera maiuscola e un numero", "(?=.*[0-9])(?=.*[A-Z]).{8,}")).bind(TextField::getValue, TextField::setValue);
		passwordBinder.forField(repeat).asRequired().bind(TextField::getValue, TextField::setValue);
		
		
		
		
		
	}
	
	private boolean controlPersonalInfo() {
		return SKIP_VALIDATION || (wTextBinder.validate().isOk() && wDateBinder.validate().isOk() && passwordBinder.validate().isOk());
	}
	
	private boolean controlOrganizationInfo() {
		return SKIP_VALIDATION || (oTextBinder.validate().isOk() && oYearBinder.validate().isOk() && passwordBinder.validate().isOk());
	}
		
	private LocalDate get18Date() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -18);
		return c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public Map<String,String> getPersonalInfo() {
		Map<String,String> pi = new HashMap<>();
		pi.put("firstname", firstname.getValue());
		pi.put("lastname", lastname.getValue());
		pi.put("dateOfBirth", dateOfBirth.getValue().toString());
		pi.put("placeOfBirth", placeOfBirth.getValue());
		pi.put("address", address.getValue());
		pi.put("phoneNumber", phoneNumber.getValue());
		pi.put("gender", gender.getSelectedItem().get());
		pi.put("fiscalCode", fiscalCode.getValue());
		return pi;
	}
	
	public Map<String,String> getOrganizationInfo() {
		Map<String,String> oi = new HashMap<>();
		oi.put("orgName", orgName.getValue());
		oi.put("foundationYear", foundationYear.getValue());
		oi.put("venue", venue.getValue());
		oi.put("orgPhone", orgPhone.getValue());
		oi.put("vatNumber", vatNumber.getValue());
		return oi;
	}
	
	public void changeTypeForm(String userType) {
		boolean visible = (userType.equals("Lavoratore"));
		personalInfo.setVisible(visible);
		organizationInfo.setVisible(!visible);
	}
	
	@Override
	public String getUriFragment() {
		return URI;
	}

	@Override
	public String getMenuName() {
		return MENU_NAME;
	}
	
	
	// TEST GETTERS
	
	public TextField getFirstname() {
		return firstname;
	}
	
	public DateField getDateOfBirth() {
		return dateOfBirth;
	}
	
	public TextField getOrgName() {
		return orgName;
	}
	
	public VerticalLayout getPersonalInfoLayout() {
		return personalInfo;
	}
	
}

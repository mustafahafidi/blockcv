package com.blockcv.view.pages;

import org.greenrobot.eventbus.EventBus;

import com.blockcv.events.NavigateEvent;
import com.blockcv.events.WelcomePageHideEvent;
import com.blockcv.events.WelcomePageShowEvent;
import com.blockcv.view.NavigableView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;

public class HomePageView extends VerticalLayout implements NavigableView {
	
	public static final String URI = "homepage";
	public static final String MENU_NAME = "Home";
	
	private EventBus eventBus;
	
	private final Label pageTitle;
	private final Label content;
	
	private final Video videoBackground;
	
	public HomePageView(EventBus eventBus) {
		this.eventBus = eventBus;
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		pageTitle = new Label("BlockCV");
		Label pageSubtitle1 = new Label("E se il tuo curriculum fosse in blockchain?");
		Label pageSubtitle2 = new Label("L'applicazione su blockchain che ti permette di pubblicare e gestire il tuo curriculum, fornendoti contatto diretto con le aziende");
		
		pageSubtitle1.setStyleName("homePageSubtitle1");
		pageSubtitle2.setStyleName("homePageSubtitle2");
		content = new Label(/*"“I've been working on a new electronic cash system that's fully peer-to-peer, with no trusted third party”<br>\n" + 
				"Con questa frase inizia la storia della criptovaluta Bitcoin, la cui nascita porterà allo sviluppo della\n" + 
				"tecnologia blockchain e allo studio di nuovi modelli di governance. Ironicamente, la tecnologia nata per\n" + 
				"eliminare la centralizzazione e per escludere terze parti fidate quali banche o governi, è diventata ad oggi\n" + 
				"una delle maggiori fonti di investimento degli stessi, per i benefici che si possono trarre da tale tecnologia. <br>*/
				"L'applicazione su blockchain che ti permette di pubblicare e gestire il tuo curriculum, fornendoti contatto diretto con le aziende ", ContentMode.HTML);
		
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		content.setStyleName("pageTextContent");
		pageTitle.addStyleNames("homePageTitle", "h1");
		Responsive.makeResponsive(this);
		setSizeFull();
		
		videoBackground = new Video("", new ThemeResource("network.mp4"));
		videoBackground.setAutoplay(true);
		videoBackground.setMuted(true);
		videoBackground.setShowControls(false);
		videoBackground.setLoop(true);
		videoBackground.setStyleName("videoBackground");
		
		addComponent(pageTitle);
		addComponent(pageSubtitle1);
		addComponent(pageSubtitle2);
		addComponent(videoBackground);
		
		//addComponent(content);
		Button tryAppBtn = new Button("Entra", (m) -> eventBus.post(new NavigateEvent("login")));
		tryAppBtn.addStyleNames(MaterialTheme.BUTTON_FLAT, MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_BORDER,  "tryAppBtn");
		addComponent(tryAppBtn);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		eventBus.post(new WelcomePageShowEvent());
	}
	
	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		eventBus.post(new WelcomePageHideEvent(event));
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


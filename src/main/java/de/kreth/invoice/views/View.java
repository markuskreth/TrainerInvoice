package de.kreth.invoice.views;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.invoice.components.InvoiceItemOverviewComponent;
import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.User;
import de.kreth.invoice.data.UserAdress;
import de.kreth.invoice.data.UserBank;
import de.kreth.invoice.persistence.ArticleRepository;
import de.kreth.invoice.persistence.InvoiceItemRepository;
import de.kreth.invoice.persistence.UserAdressRepository;
import de.kreth.invoice.persistence.UserBankRepository;
import de.kreth.invoice.security.UserManager;

@PageTitle("")
@Route(value = "")
@PreAuthorize("hasRole('ROLE_TRAINER')")
public class View extends VerticalLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;
    private final UserManager userRepository;
    private final UserBankRepository bankRepository;
    private final UserAdressRepository adressRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ArticleRepository articleRepository;
    private InvoiceItemOverviewComponent invoiceItems;
    private User user;

    public View(@Autowired UserManager userRepository, @Autowired UserBankRepository userBankRepository,
	    @Autowired UserAdressRepository adressRepository,
	    @Autowired InvoiceItemRepository invoiceItemRepository,
	    @Autowired ArticleRepository articleRepository) {
	this.userRepository = userRepository;
	this.bankRepository = userBankRepository;
	this.adressRepository = adressRepository;
	this.invoiceItemRepository = invoiceItemRepository;
	this.articleRepository = articleRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

	user = userRepository.getLoggedInUser();
	if (user == null) {
	    user = userRepository.create();
	}

//	UserBank bank = bankRepository.findByUser(user);
//	UserAdress adress = adressRepository.findByUser(user);

//	if (isBankOrAdressInvalid(bank, adress)) {
//	    event.getUI().navigate(LoginDataView.class);
//	} else {
//	    checkArticle();
//	    createUi();
//	}

	checkArticle();
	createUi();
    }

    private void checkArticle() {
	if (articleRepository.count() <= 0) {

	    Article article = new Article();
	    LocalDateTime now = LocalDateTime.now();
	    article.setChangeDate(now);
	    article.setCreatedDate(now);
	    article.setDescription("Dummy Übungsleiter");
	    article.setTitle("Dummy");
	    article.setPricePerHour(BigDecimal.valueOf(7.5));
	    article.setUserId(user.getId());
	    articleRepository.save(article);
	}
    }

    private boolean isBankOrAdressInvalid(UserBank bank, UserAdress adress) {
	return bank == null || adress == null || !bank.isValid() || !adress.isValid();
    }

    private void createUi() {

	Button menuButton = new Button(VaadinIcon.MENU.create());
	menuButton.addClickListener(this::onMenuButtonClick);

	HorizontalLayout l = new HorizontalLayout(menuButton, new H1("Übungsleiter Abrechnung"));
	l.setAlignItems(Alignment.CENTER);
	add(l);

	Label name = new Label(user.getGivenName() + " " + user.getFamilyName());
	Label email = new Label(user.getEmail());
	add(name, email);

	invoiceItems = new InvoiceItemOverviewComponent(invoiceItemRepository, articleRepository, user);
	add(invoiceItems);

    }

    public void onMenuButtonClick(ClickEvent<Button> event) {
	ContextMenu menu = new ContextMenu();
	menu.setTarget(event.getSource());
	menu.addItem("Einstellungen", this::onSettingsButtonClick);
	menu.addItem("Über", this::onAboutButtonClick);
	menu.setVisible(true);
    }

    public void onSettingsButtonClick(ClickEvent<MenuItem> event) {
	Dialog dlg = new Dialog();
	dlg.add(new H1("Einstellungen"));
	dlg.add(new Text("Einstellugen für diese App. Noch nicht implementiert."));
	dlg.open();
    }

    public void onAboutButtonClick(ClickEvent<MenuItem> event) {

	Dialog dlg = new Dialog();
	dlg.add(new H1("Personeneditor"));
	dlg.add(new Text(
		"Personeneditor ist eine App zur Erfassung und Änderung von Personen im Trampolin des MTV Groß-Buchholz."));
	dlg.open();
    }

}
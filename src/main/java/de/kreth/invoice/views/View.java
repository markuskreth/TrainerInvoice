package de.kreth.invoice.views;

import static de.kreth.invoice.Application.getString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.invoice.Localization_Properties;
import de.kreth.invoice.business.ArticleBusiness;
import de.kreth.invoice.business.InvoiceBusiness;
import de.kreth.invoice.business.InvoiceItemBusiness;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.data.User;
import de.kreth.invoice.data.UserAdress;
import de.kreth.invoice.data.UserBank;
import de.kreth.invoice.security.UserManager;
import de.kreth.invoice.views.article.ArticleDialog;
import de.kreth.invoice.views.invoiceitem.InvoiceItemOverviewComponent;
import de.kreth.invoice.views.invoiceitem.InvoiceOverviewComponent;
import de.kreth.invoice.views.user.UserDetailsDialog;

@PageTitle("")
@Route(value = "")
@PreAuthorize("hasRole('INVOICE_USER')")
public class View extends VerticalLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;
    private final UserManager userManager;
    private final InvoiceItemBusiness invoiceItemBusiness;
    private final ArticleBusiness articleBusiness;
    private InvoiceItemOverviewComponent invoiceItems;
    private User user;
    private InvoiceBusiness invoiceRepository;
    private InvoiceOverviewComponent invoiceCompoent;

    public View(@Autowired UserManager userRepository,
	    @Autowired InvoiceItemBusiness invoiceItemRepository,
	    @Autowired InvoiceBusiness invoiceRepository,
	    @Autowired ArticleBusiness articleRepository) {
	this.userManager = userRepository;
	this.invoiceItemBusiness = invoiceItemRepository;
	this.invoiceRepository = invoiceRepository;
	this.articleBusiness = articleRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

	user = userManager.getLoggedInUser();
	if (user == null) {
	    user = userManager.create();
	}

	if (isBankAndAdressInvalid()) {
	    openDetailDialog();
	} else if (articleBusiness.findByUserId(user.getId()).isEmpty()) {
	    openArticleDialog();
	} else {
	    createUi();
	}

    }

    private void openDetailDialog() {
	UserDetailsDialog dlg = new UserDetailsDialog();
	dlg.setUser(user);
	dlg.open();
	dlg.addOpenedChangeListener(ev -> {
	    boolean bankAndAdressInvalid = isBankAndAdressInvalid();
	    doCloseDialog(dlg);
	    if (dlg.isValidAndClosedWithOk() && bankAndAdressInvalid) {
		openArticleDialog();
	    }
	});
    }

    private void doCloseDialog(UserDetailsDialog dlg) {
	if (dlg.isValidAndClosedWithOk()) {
	    this.user = dlg.getUser();
	    userManager.save(this.user);
	    getUI().ifPresent(ui -> ui.navigate(View.class));
	} else if (isBankAndAdressInvalid()) {
	    openDetailDialog();
	}
    }

    private void openArticleDialog() {
	ArticleDialog dlg = new ArticleDialog(articleBusiness, user);
	dlg.open();
	dlg.addOpenedChangeListener(ev -> {
	    if (articleBusiness.findByUserId(user.getId()).isEmpty()) {
		openArticleDialog();
	    } else {
		doCloseDialog(dlg);
	    }
	});
    }

    private void doCloseDialog(ArticleDialog dlg) {
	getUI().ifPresent(ui -> ui.navigate(View.class));
    }

    private boolean isBankAndAdressInvalid() {

	UserBank bank = user.getBank();
	UserAdress adress = user.getAdress();

	return bank == null
		|| adress == null
		|| !bank.isValid()
		|| !adress.isValid();
    }

    private void createUi() {

	if (getComponentCount() > 0 && invoiceItems != null && invoiceCompoent != null) {
	    invoiceItems.refreshData();
	    invoiceCompoent.refreshData();
	    return;
	}

	Button menuButton = new Button(VaadinIcon.MENU.create());
	menuButton.addClickListener(this::onMenuButtonClick);

	HorizontalLayout l = new HorizontalLayout(menuButton, new H1("Übungsleiter Abrechnung"));
	l.setAlignItems(Alignment.CENTER);
	add(l);

	Label name = new Label(user.getGivenName() + " " + user.getFamilyName());
	Label email = new Label(user.getEmail());
	Button openDetailDialog = new Button(getString(Localization_Properties.CAPTION_USER_DETAILS));
	openDetailDialog.addClickListener(ev -> openDetailDialog());
	Button openArticleDialog = new Button(getString(Localization_Properties.CAPTION_ARTICLES));
	openArticleDialog.addClickListener(ev -> openArticleDialog());
	FormLayout layout = new FormLayout(name, email, openDetailDialog, openArticleDialog);
	add(layout);

	invoiceItems = new InvoiceItemOverviewComponent(invoiceItemBusiness, articleBusiness, user);

	final List<InvoiceItem> itemsForInvoice = new ArrayList<>();
	invoiceItems.addSeelctionListener(ev -> {
	    itemsForInvoice.clear();
	    itemsForInvoice.addAll(ev.getValues());
	    if (itemsForInvoice.isEmpty() == false) {
		Long articleId = itemsForInvoice.get(0).getArticle().getId();
		for (Iterator<InvoiceItem> iterator = itemsForInvoice.iterator(); iterator.hasNext();) {
		    InvoiceItem invoiceItem = iterator.next();
		    if (invoiceItem.getArticle().getId() != articleId) {
			iterator.remove();
		    }
		}
	    }
	});
	invoiceCompoent = new InvoiceOverviewComponent(invoiceRepository, user, itemsForInvoice);
	invoiceCompoent.addInvoiceCountChangeListener(() -> invoiceItems.refreshData());

	layout.add(invoiceItems);
	layout.add(invoiceCompoent);

	invoiceItems.refreshData();
	invoiceCompoent.refreshData();
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
	dlg.add(new Text("Einstellugen für diese App. noch nicht implementiert."));
	dlg.open();
    }

    public void onAboutButtonClick(ClickEvent<MenuItem> event) {

	Dialog dlg = new Dialog();
	dlg.add(new H1("Abrechnungen"));
	dlg.add(new Text(
		"Abrechnungen ist eine App zur Erfassung von Übungsleiterstunden und Abrechnung im MTV Groß-Buchholz."));
	dlg.open();
    }

}
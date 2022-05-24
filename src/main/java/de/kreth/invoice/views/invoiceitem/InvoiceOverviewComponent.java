package de.kreth.invoice.views.invoiceitem;

import static de.kreth.invoice.Application.getString;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu.GridContextMenuItemClickEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.kreth.invoice.Localization_Properties;
import de.kreth.invoice.business.InvoiceBusiness;
import de.kreth.invoice.data.Invoice;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.data.User;
import de.kreth.invoice.views.invoice.InvoiceDialog;
import de.kreth.invoice.views.invoice.InvoiceDialog.InvoiceMode;
import de.kreth.invoice.views.invoice.InvoiceGrid;

public class InvoiceOverviewComponent extends VerticalLayout {

    private static final long serialVersionUID = 7784319346191720329L;
    private final InvoiceGrid grid;
    private final InvoiceBusiness business;
    private final User user;
    private final List<InvoiceItem> itemsForInvoice;
    private final List<InvoiceCreationListener> creationListener;

    public InvoiceOverviewComponent(InvoiceBusiness business, User user, List<InvoiceItem> itemsForInvoice) {
	super();
	this.business = business;
	this.user = user;
	this.itemsForInvoice = itemsForInvoice;
	this.grid = new InvoiceGrid();
	this.creationListener = new ArrayList<>();

	Button addButton = new Button(getString(Localization_Properties.CAPTION_INVOICE_CREATE),
		this::createNewRechnung);
	FormLayout titleComponent = new FormLayout(new H3(getString(Localization_Properties.CAPTION_INVOICES)),
		addButton);
	add(new VerticalLayout(titleComponent, grid));
	grid.addItemClickListener(ev -> openDialog(ev.getItem(), InvoiceMode.VIEW_ONLY));
	GridContextMenu<Invoice> menu = grid.addContextMenu();
	menu.addItem("Löschen", this::delete);

    }

    private void delete(GridContextMenuItemClickEvent<Invoice> event) {
	if (event.getItem().isPresent()) {
	    ConfirmDialog dlg = new ConfirmDialog();
	    dlg.setHeader(getString(Localization_Properties.MESSAGE_DELETE_TITLE));
	    dlg.setText(MessageFormat.format(getString(Localization_Properties.MESSAGE_DELETE_TEXT),
		    event.getItem().get()));
	    dlg.setCancelable(true);
	    dlg.setCancelText("Nicht " + getString(Localization_Properties.LABEL_DELETE));
	    dlg.setConfirmText(getString(Localization_Properties.LABEL_DELETE));
	    dlg.addConfirmListener(ev -> {
		business.delete(event.getItem().get());
		refreshData();
	    });
	    dlg.open();
	}
    }

    public void refreshData() {

	List<Invoice> loadAll = business.loadAll(invoice -> user.equals(invoice.getUser()));
	grid.setItems(loadAll);
	grid.getDataProvider().refreshAll();
    }

    private void createNewRechnung(ClickEvent<Button> ev) {
	if (itemsForInvoice.isEmpty()) {
	    Notification.show("Es kann keine Abrechnung ohne Positionen erzeugt werden.");
	    return;
	}
	Invoice invoice = new Invoice();
	invoice.setUser(user);
	invoice.setItems(itemsForInvoice);
	invoice.setInvoiceId(itemsForInvoice.get(0).getStart().getYear() + "-" +
		itemsForInvoice.get(0).getStart().getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));
	invoice.setInvoiceDate(LocalDateTime.now());
//	invoice.setInvoiceId(business.createNextInvoiceId(business.findByUserId(user.getId()), "Rechnung_{0}"));
	invoice.setReportRessource(itemsForInvoice.get(0).getArticle().getReport());
	openDialog(invoice, InvoiceMode.CREATE);
    }

    private void openDialog(Invoice invoice, InvoiceMode mode) {
	InvoiceDialog dlg = new InvoiceDialog(mode);
	dlg.setInvoice(invoice);
	dlg.addOkClickListener(evt -> {
	    business.save(invoice);
	    refreshData();
	    for (InvoiceCreationListener invoiceCreationListener : creationListener) {
		invoiceCreationListener.invoiceCreated();
	    }
	});
	dlg.open();
	dlg.addOpenedChangeListener(evt -> refreshData());
    }

    public void addInvoiceCreationListener(InvoiceCreationListener listener) {
	this.creationListener.add(listener);
    }

    public static interface InvoiceCreationListener {
	void invoiceCreated();
    }
}

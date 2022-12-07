package de.kreth.invoice.views.invoiceitem;

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
    private final List<InvoiceCountChangeListener> creationListener;
    private InvoiceDialog invoiceDialog;

    public InvoiceOverviewComponent(InvoiceBusiness business, User user, List<InvoiceItem> itemsForInvoice) {
	super();
	this.business = business;
	this.user = user;
	this.itemsForInvoice = itemsForInvoice;
	this.grid = new InvoiceGrid();
	this.creationListener = new ArrayList<>();

	Button addButton = new Button(Localization_Properties.CAPTION_INVOICE_CREATE.getText(),
		this::createNewRechnung);
	FormLayout titleComponent = new FormLayout(new H3(Localization_Properties.CAPTION_INVOICES.getText()),
		addButton);
	add(new VerticalLayout(titleComponent, grid));
	grid.addItemClickListener(ev -> openDialog(ev.getItem(), InvoiceMode.VIEW_ONLY));

    }

    private void confirmAndExecuteDelete(Invoice item) {
	ConfirmDialog dlg = new ConfirmDialog();
	dlg.setHeader(Localization_Properties.MESSAGE_DELETE_TITLE.getText());
	dlg.setText(MessageFormat.format(Localization_Properties.MESSAGE_DELETE_TEXT.getText(),
		item));
	dlg.setCancelable(true);
	dlg.setCancelText("Nicht " + Localization_Properties.LABEL_DELETE.getText());
	dlg.setConfirmText(Localization_Properties.LABEL_DELETE.getText());
	dlg.addConfirmListener(ev -> {
	    business.delete(item);
	    if (invoiceDialog != null) {
		invoiceDialog.close();
	    }
	    refreshData();
	    for (InvoiceCountChangeListener invoiceCreationListener : creationListener) {
		invoiceCreationListener.invoiceCreated();
	    }
	});
	dlg.open();
    }

    public void refreshData() {

	List<Invoice> loadAll = business.loadAll(invoice -> user.equals(invoice.getUser()));
	loadAll.sort((i1, i2) -> i1.getInvoiceDate().compareTo(i2.getInvoiceDate()));
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
	invoiceDialog = new InvoiceDialog(mode);
	List<String> invoiceIds = new ArrayList<>();

	grid.getDataProvider().getItems().forEach(inv -> invoiceIds.add(inv.getInvoiceId().strip()));
	invoiceDialog.setInvoice(invoice, invoiceIds);
	invoiceDialog.addOkClickListener(evt -> {
	    business.save(invoice);
	    refreshData();
	    for (InvoiceCountChangeListener invoiceCreationListener : creationListener) {
		invoiceCreationListener.invoiceCreated();
	    }
	});
	invoiceDialog.addDeleteClickListener(evt -> {
	    confirmAndExecuteDelete(invoice);
	});
	invoiceDialog.open();
	invoiceDialog.addOpenedChangeListener(evt -> refreshData());
	invoiceDialog.addOpenedChangeListener(ev -> {
	    if (ev.isOpened() == false) {
		invoiceDialog = null;
	    }
	});
    }

    public void addInvoiceCountChangeListener(InvoiceCountChangeListener listener) {
	this.creationListener.add(listener);
    }

    public static interface InvoiceCountChangeListener {
	void invoiceCreated();
    }
}

package de.kreth.invoice.views.invoice;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;

import de.kreth.invoice.data.Invoice;

public class InvoiceGrid extends Grid<Invoice> {

    private static final long serialVersionUID = 662980245990122807L;

    private final List<Invoice> items;

    public InvoiceGrid() {

	Column<Invoice> invoiceIdCol = addColumn(Invoice::getInvoiceId);
	invoiceIdCol.setHeader("Rechnungsnummer");

	Column<Invoice> invoiceDateCol = addColumn(new LocalDateTimeRenderer<>(Invoice::getInvoiceDate,
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
	invoiceDateCol.setHeader("Rechnungsdatum");

	Column<Invoice> invoiceSum = addColumn(new NumberRenderer<>(Invoice::getSum,
		NumberFormat.getCurrencyInstance()));
	invoiceSum.setHeader("Summe");

	items = new ArrayList<Invoice>();
	setItems(DataProvider.ofCollection(items));
    }

    public void setItems(List<Invoice> items) {
	this.items.clear();
	this.items.addAll(items);
	getDataProvider().refreshAll();
    }
}
